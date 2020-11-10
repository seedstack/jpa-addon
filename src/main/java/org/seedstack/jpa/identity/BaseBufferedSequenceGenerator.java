/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.identity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.seedstack.business.domain.Entity;
import org.seedstack.business.util.SequenceGenerator;
import org.seedstack.jpa.internal.JpaErrorCode;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;

import com.google.common.base.Strings;

public abstract class BaseBufferedSequenceGenerator // extends BaseSequenceGenerator
        implements SequenceGenerator {

    private static final String ALLOCATION_SIZE = "allocationSize";
    private static final String SANITIZING_EXPRESSION = "^[A-Za-z0-9_-]*$";
    private static final String SEQUENCE_NAME = "sequenceName";
    private static final ConcurrentMap<String, SequenceBufferHolder> sequenceMap = new ConcurrentHashMap<>();

    @Inject
    private Application application;
    @Inject
    private EntityManager entityManager;

    private final String sequenceQuery;
    private final String setSequenceSQL;

    protected BaseBufferedSequenceGenerator(String peekSQL, String setSequenceSQL) {
        this.sequenceQuery = peekSQL;
        this.setSequenceSQL = setSequenceSQL;
    }

    protected BaseBufferedSequenceGenerator(String sequenceSQL) {
        this.sequenceQuery = sequenceSQL;
        this.setSequenceSQL = "";
    }

    @Override
    public <E extends Entity<Long>> Long generate(Class<E> entityClass) {
        if (StringUtils.isEmpty(this.setSequenceSQL)) {
            // If sequence is not set, ignore allocation retrieval
            sequenceMap.putIfAbsent(entityClass.getName(), new SequenceBufferHolder(1L));
        } else {
            sequenceMap.putIfAbsent(entityClass.getName(), createSequenceHolder(entityClass));
        }

        SequenceBufferHolder sequenceHolder = sequenceMap.get(entityClass.getName());
        synchronized (sequenceHolder) {
            long nextId = sequenceHolder.getNextId();
            if (nextId > 0) {
                return nextId;
            }
            Long baseId = this.retrieveNextSegment(sequenceHolder.getChunkSize(), entityClass);
            sequenceHolder.setChunkBaseId(baseId);
            return sequenceHolder.getNextId();
        }

    }

    /**
     * If the sequence has to be initialized, we notify the implementation the intent
     * 
     * @param sequenceName
     */
    protected abstract void ensureSequenceExistence(String sequenceName);

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected <E extends Entity<Long>> String getSequence(Class<E> entityClass) {
        String sequence = application.getConfiguration(entityClass).get(SEQUENCE_NAME);
        if (StringUtils.isBlank(sequence)) {
            throw SeedException.createNew(JpaErrorCode.NO_SEQUENCE_NAME_FOUND_FOR_ENTITY)
                    .put("entityClass", entityClass);
        }
        return sequence;
    }

    protected <E extends Entity<Long>> Long retrieveNextSegment(long chunkSize,
            Class<E> entityClass) {

        Long nextId = internalGeneration(entityClass);
        Long segmentLimit = nextId + chunkSize;
        String sequence = getSequence(entityClass);

        if (!Strings.isNullOrEmpty(setSequenceSQL)) {
            entityManager.createNativeQuery(String.format(setSequenceSQL, sequence, segmentLimit))
                    .executeUpdate();
        }
        return nextId;

    }

    protected final void validateSequence(String sequence) {
        if (StringUtils.isEmpty(sequence) || !sequence.matches(SANITIZING_EXPRESSION)) {
            throw new RuntimeException("Injection attempt detected");
        }
        ensureSequenceExistence(sequence);
    }

    // TODO: use Seedstack wrapped exceptions instead of RuntimeException
    private SequenceBufferHolder createSequenceHolder(Class<?> entityClass) {

        String allocationSize = application.getConfiguration(entityClass)
                .getOrDefault(ALLOCATION_SIZE, "1");

        Long alocation;
        try {
            alocation = Long.parseLong(allocationSize);
        } catch (NumberFormatException ex) {
            throw new RuntimeException(String.format("Invalid value for allocationSize at class %s",
                    entityClass.getClass().getName()), ex);
        }

        return new SequenceBufferHolder(alocation);

    }

    private <E extends Entity<Long>> Long internalGeneration(Class<E> entityClass) {

        String sequence = getSequence(entityClass);

        if (entityManager == null) {
            throw SeedException.createNew(JpaErrorCode.MISSING_ENTITY_MANAGER);
        }
        validateSequence(sequence);
        return ((Number) entityManager
                .createNativeQuery(String.format(sequenceQuery, sequence))
                .getSingleResult())
                        .longValue();
    }

    private static final class SequenceBufferHolder {
        private Long chunkBoundary;
        private final Long chunkSize;

        private final AtomicLong sequencer;

        SequenceBufferHolder(Long chunkSize) {
            this.sequencer = new AtomicLong();
            this.chunkBoundary = 0L;
            this.chunkSize = chunkSize;
        }

        public long getChunkSize() {
            return this.chunkSize;
        }

        public long getNextId() {
            long nextId = sequencer.getAndIncrement();
            if (nextId == this.chunkBoundary || chunkBoundary == 0L) {
                return -1L;
            }
            return nextId;
        }

        public void setChunkBaseId(long nextBaseId) {
            this.chunkBoundary = nextBaseId + this.chunkSize;
            this.sequencer.set(nextBaseId);
        }

    }

}
