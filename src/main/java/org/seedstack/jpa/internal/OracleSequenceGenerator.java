/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal;

import com.google.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.domain.Entity;
import org.seedstack.business.util.SequenceGenerator;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;

/**
 * Uses an Oracle sequence for identity management. This handler needs the Oracle
 * sequence name to be specified in class configuration as the 'identitySequenceName' property.
 */
@Named("oracleSequence")
class OracleSequenceGenerator implements SequenceGenerator {
    private static final String SEQUENCE_NAME = "identitySequenceName";
    @Inject(optional = true)
    private EntityManager entityManager;
    @Inject
    private Application application;

    @Override
    public <E extends Entity<Long>> Long generate(Class<E> entityClass) {
        String sequence = application.getConfiguration(entityClass).get(SEQUENCE_NAME);
        if (StringUtils.isBlank(sequence)) {
            throw SeedException.createNew(JpaErrorCode.NO_SEQUENCE_NAME_FOUND_FOR_ENTITY)
                    .put("entityClass", entityClass);
        }

        if (entityManager == null) {
            throw SeedException.createNew(JpaErrorCode.MISSING_ENTITY_MANAGER);
        }

        return ((Number) entityManager.createNativeQuery(String.format("SELECT %s.NEXTVAL FROM DUAL", sequence))
                .getSingleResult()).longValue();
    }
}
