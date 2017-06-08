/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.orm;

import com.google.inject.Injector;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.jpa.spi.JpaRepositoryFactory;

import javax.inject.Inject;

public abstract class BaseJpaRepositoryFactory implements JpaRepositoryFactory {
    @Inject
    private Injector injector;

    @Override
    public <A extends AggregateRoot<ID>, ID> Repository<A, ID> createRepository(Class<A> aggregateRootClass, Class<ID> identifierClass) {
        Repository<A, ID> repository = doCreateRepository(aggregateRootClass, identifierClass);
        injector.injectMembers(repository);
        return repository;
    }

    public abstract <A extends AggregateRoot<ID>, ID> Repository<A, ID> doCreateRepository(Class<A> aggregateRootClass, Class<ID> identifierClass);
}
