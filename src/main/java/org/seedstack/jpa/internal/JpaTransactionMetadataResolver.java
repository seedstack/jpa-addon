/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

import java.util.Optional;

/**
 * This {@link org.seedstack.seed.transaction.spi.TransactionMetadataResolver} resolves metadata for transactions marked
 * with {@link JpaUnit}.
 */
class JpaTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultJpaUnit;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<JpaUnit> jpaUnit = JpaUnitResolver.INSTANCE.apply(methodInvocation.getMethod());

        if (jpaUnit.isPresent() || JpaTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JpaTransactionHandler.class);
            result.setExceptionHandler(JpaExceptionHandler.class);
            result.setResource(jpaUnit.isPresent() ? resolveUnit(jpaUnit.get()) : defaultJpaUnit);
            return result;
        }

        return null;
    }

    private String resolveUnit(JpaUnit jpaUnit) {
        String value = jpaUnit.value();
        if (value.isEmpty()) {
            return defaultJpaUnit;
        } else {
            return value;
        }
    }
}