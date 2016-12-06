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
import org.seedstack.business.domain.identity.SequenceHandler;
import org.seedstack.seed.ClassConfiguration;
import org.seedstack.seed.SeedException;

import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Handles oracle sequence for identity management. This handler needs the Oracle
 * sequence name specified in class configuration in the 'identitySequenceName' property.
 */
@Named("oracleSequence")
class OracleSequenceHandler implements SequenceHandler<Entity<Long>, Long> {
    private static final String SEQUENCE_NAME = "identitySequenceName";
    @Inject(optional = true)
    private EntityManager entityManager;

    @Override
    public Long handle(Entity<Long> entity, ClassConfiguration<Entity<Long>> entityConfiguration) {
        String sequence = entityConfiguration.get(SEQUENCE_NAME);
        if (StringUtils.isBlank(sequence)) {
            throw SeedException.createNew(JpaErrorCode.NO_SEQUENCE_NAME_FOUND_FOR_ENTITY).put("entityClass", entity.getClass());
        }

        if (entityManager == null) {
            throw SeedException.createNew(JpaErrorCode.MISSING_ENTITY_MANAGER);
        }

        return ((Number) entityManager.createNativeQuery("SELECT " + sequence + ".NEXTVAL FROM DUAL").getSingleResult()).longValue();
    }
}
