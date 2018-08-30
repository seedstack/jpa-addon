/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.identity;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.seedstack.business.domain.Entity;
import org.seedstack.business.util.SequenceGenerator;
import org.seedstack.jpa.internal.JpaErrorCode;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;

/**
 * Base sequence generator for database-based sequence generators
 */
abstract class BaseSequenceGenerator implements SequenceGenerator {
    static final String SANITIZING_EXPRESSION = "^[A-Za-z0-9_-]*$";
    static final String SEQUENCE_NAME = "sequenceName";

    @Inject
    private Application application;

    @Inject
    private EntityManager entityManager;

    private final String sequenceQuery;

    protected BaseSequenceGenerator(String query) {
        this.sequenceQuery = query;
    }

    @Override
    public <E extends Entity<Long>> Long generate(Class<E> entityClass) {

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

    protected final void validateSequence(String sequence) {
        if (StringUtils.isEmpty(sequence) || !sequence.matches(SANITIZING_EXPRESSION)) {
            throw new RuntimeException("Injection attempt detected");
        }
        ensureSequenceExistence(sequence);
    }

}
