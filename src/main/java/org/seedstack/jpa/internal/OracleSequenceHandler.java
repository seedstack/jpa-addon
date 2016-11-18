/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import com.google.inject.Inject;
import jodd.typeconverter.impl.LongConverter;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.domain.Entity;
import org.seedstack.business.domain.identity.IdentityErrorCodes;
import org.seedstack.business.domain.identity.SequenceHandler;
import org.seedstack.seed.SeedException;

import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Handles oracle sequence for identity management. This handler needs the oracle
 * sequence name property passed through props configuration using the full
 * entity class name as section and <b>identity.sequence-name</b> as key
 */
@Named("oracle-sequence")
class OracleSequenceHandler implements SequenceHandler<Entity<Long>, Long> {
    private static final String SEQUENCE_NAME = "identity.sequence-name";
    @Inject(optional = true)
    private EntityManager entityManager;

    @Override
    public Long handle(Entity<Long> entity, Configuration entityConfiguration) {
        String sequence = entityConfiguration.getString(SEQUENCE_NAME);
        if (StringUtils.isBlank(sequence)) {
            throw SeedException.createNew(IdentityErrorCodes.NO_SEQUENCE_NAME_FOUND_FOR_ENTITY).put("entityClass", entity.getClass());
        }

        if (entityManager == null) {
            throw SeedException
                    .createNew(JpaErrorCode.NO_ENTITY_MANAGER_CONFIGURED)
                    .put("reason", "incrementing Oracle sequence " + sequence);
        }

        Object id = entityManager.createNativeQuery("SELECT " + sequence + ".NEXTVAL FROM DUAL").getSingleResult();
        Long convertedId;
        try {
            convertedId = new LongConverter().convert(id);
        } catch (ConversionException e) {
            throw SeedException.wrap(e, IdentityErrorCodes.ID_CAST_EXCEPTION).put("object", id)
                    .put("objectType", id.getClass())
                    .put("entity", entity.getClass().getSimpleName());
        }

        return convertedId;
    }
}
