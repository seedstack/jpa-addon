/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import com.google.common.base.Strings;
import java.util.Optional;
import javax.inject.Inject;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jpa.JpaConfig;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

/**
 * This {@link org.seedstack.seed.transaction.spi.TransactionMetadataResolver} resolves metadata
 * for transactions marked
 * with {@link JpaUnit}.
 */
class JpaTransactionMetadataResolver implements TransactionMetadataResolver {

    @Inject
    private Application application;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation,
            TransactionMetadata defaults) {
        Optional<JpaUnit> jpaUnitOptional = JpaUnitResolver.INSTANCE
                .apply(methodInvocation.getMethod());

        if (jpaUnitOptional.isPresent() || JpaTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JpaTransactionHandler.class);
            result.setExceptionHandler(JpaExceptionHandler.class);
            if (jpaUnitOptional.isPresent() && !Strings.isNullOrEmpty(jpaUnitOptional.get().value())) {
                result.setResource(jpaUnitOptional.get().value());
            } else {
                String defaultUnit = application.getConfiguration().get(JpaConfig.class).getDefaultUnit();
                if (!Strings.isNullOrEmpty(defaultUnit)) {
                    result.setResource(defaultUnit);
                } else {
                    throw SeedException.createNew(JpaErrorCode.NO_JPA_UNIT_SPECIFIED_FOR_TRANSACTION)
                            .put("method", methodInvocation.getMethod().toString());
                }
            }
            return result;
        }
        return null;
    }
}