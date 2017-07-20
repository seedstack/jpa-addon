/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.domain.Entity;
import org.seedstack.business.domain.SequenceGenerator;
import org.seedstack.seed.ClassConfiguration;
import org.seedstack.seed.SeedException;

import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Handles oracle sequence for identity management. This handler needs the Oracle
 * sequence name specified in class configuration in the 'identitySequenceName' property.
 */
@Named("oracleSequence")
class OracleSequenceGenerator implements SequenceGenerator<Long> {
    private static final String SEQUENCE_NAME = "identitySequenceName";
    @Inject(optional = true)
    private EntityManager entityManager;

    @Override
    public <E extends Entity<Long>> Long generate(Class<E> entityClass, ClassConfiguration<E> entityConfiguration) {
        String sequence = entityConfiguration.get(SEQUENCE_NAME);
        if (StringUtils.isBlank(sequence)) {
            throw SeedException.createNew(JpaErrorCode.NO_SEQUENCE_NAME_FOUND_FOR_ENTITY)
                    .put("entityClass", entityClass);
        }

        if (entityManager == null) {
            throw SeedException.createNew(JpaErrorCode.MISSING_ENTITY_MANAGER);
        }

        return ((Number) entityManager.createNativeQuery("SELECT " + sequence + ".NEXTVAL FROM DUAL").getSingleResult()).longValue();
    }
}
