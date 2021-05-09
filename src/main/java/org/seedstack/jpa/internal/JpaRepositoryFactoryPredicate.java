/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.seedstack.jpa.spi.JpaRepositoryFactory;
import org.seedstack.shed.reflect.ClassPredicates;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

/**
 * Matches JPA repository factories.
 */
class JpaRepositoryFactoryPredicate implements Predicate<Class<?>> {
    static final JpaRepositoryFactoryPredicate INSTANCE = new JpaRepositoryFactoryPredicate();

    private JpaRepositoryFactoryPredicate() {
        // no instantiation allowed
    }

    @Override
    public boolean test(Class<?> candidate) {
        return ClassPredicates.classIsAssignableFrom(JpaRepositoryFactory.class)
                .and(ClassPredicates.classModifierIs(Modifier.ABSTRACT)
                        .or(ClassPredicates.classIsInterface())).negate()
                .test(candidate);
    }
}