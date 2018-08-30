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
import org.seedstack.seed.Application;

public abstract class BaseBufferedSequenceGenerator extends BaseSequenceGenerator
        implements SequenceGenerator {

    private static final String ALLOCATION_SIZE = "allocationSize";
    private static final ConcurrentMap<String, SequenceBufferHolder> sequenceMap = new ConcurrentHashMap<>();

    @Inject
    private Application application;
    @Inject
    private EntityManager entityManager;

    private final String setSequenceSQL;

    protected BaseBufferedSequenceGenerator(String peekSQL, String setSequenceSQL) {
        super(peekSQL);
        this.setSequenceSQL = setSequenceSQL;
    }

    @Override
    public <E extends Entity<Long>> Long generate(Class<E> entityClass) {
        sequenceMap.putIfAbsent(entityClass.getName(), createSequenceHolder(entityClass));

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

    protected <E extends Entity<Long>> Long retrieveNextSegment(long chunkSize,
            Class<E> entityClass) {

        Long nextId = super.generate(entityClass);
        Long segmentLimit = nextId + chunkSize;
        String sequence = getSequence(entityClass);

        entityManager.createNativeQuery(String.format(setSequenceSQL, sequence, segmentLimit))
                .executeUpdate();
        return nextId;

    }

    // TODO: use Seedstack wrapped exceptions instead of RuntimeException
    private SequenceBufferHolder createSequenceHolder(Class<?> entityClass) {

        String allocationSize = application.getConfiguration(entityClass)
                .getOrDefault(ALLOCATION_SIZE, "");

        if (StringUtils.isEmpty(allocationSize)) {
            throw new RuntimeException(String.format("Allocation size was not defined for class %s",
                    entityClass.getClass().getName()));
        }
        Long alocation;
        try {
            alocation = Long.parseLong(allocationSize);
        } catch (NumberFormatException ex) {
            throw new RuntimeException(String.format("Invalid value for allocationSize at class %s",
                    entityClass.getClass().getName()), ex);
        }

        return new SequenceBufferHolder(alocation);

    }

    private static final class SequenceBufferHolder {
        private final Long chunkSize;
        private final AtomicLong sequencer;

        private Long chunkBoundary;

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
