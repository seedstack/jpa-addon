/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.core.utils.SeedReflectionUtils;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

/**
 * This {@link org.seedstack.seed.transaction.spi.TransactionMetadataResolver} resolves metadata for transactions marked
 * with {@link JpaUnit}.
 *
 * @author adrien.lauer@mpsa.com
 */
class JpaTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultJpaUnit;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        JpaUnit jpaUnit = SeedReflectionUtils.getMethodOrAncestorMetaAnnotatedWith(methodInvocation.getMethod(), JpaUnit.class);

        if (jpaUnit != null || JpaTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JpaTransactionHandler.class);
            result.setExceptionHandler(JpaExceptionHandler.class);
            result.setResource(jpaUnit == null ? defaultJpaUnit : jpaUnit.value());
            return result;
        }

        return null;
    }
}