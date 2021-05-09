/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.orm;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.jpa.spi.JpaRepositoryFactoryPriority;
import org.seedstack.shed.reflect.Classes;

@Priority(JpaRepositoryFactoryPriority.VENDOR_SPECIFIC_PRIORITY)
public class Hibernate52RepositoryFactory extends BaseJpaRepositoryFactory {

    private static final String ORG_HIBERNATE_QUERY_QUERY = "org.hibernate.query.Query";
    private static final boolean hibernate52Available = isHibernate52Available();

    private static boolean isHibernate52Available() {
        Optional<Class<Object>> hibernateQueryClass = Classes.optional(ORG_HIBERNATE_QUERY_QUERY);
        if (hibernateQueryClass.isPresent()) {
            try {
                hibernateQueryClass.get().getMethod("stream");
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isSupporting(EntityManager entityManager) {
        if (hibernate52Available) {
            try {
                // The provider is actually hibernate for this transaction
                entityManager.unwrap(Session.class);
                return true;
            } catch (PersistenceException e) {
                // ignore, return that hibernate is not supported
            }
        }
        return false;
    }

    @Override
    public <A extends AggregateRoot<I>, I> Repository<A, I> doCreateRepository(
            Class<A> aggregateRootClass,
            Class<I> identifierClass) {
        return new HibernateJpaRepository<>(aggregateRootClass, identifierClass);
    }

    /**
     * Extending {@link Jpa21RepositoryFactory.Jpa21Repository}, this implementation of business
     * framework repository takes advantage of Hibernate-specific features, like the ability to stream
     * results directly from the database without loading them in memory all at once.
     *
     * @param <A> the aggregate root class.
     * @param <I> the aggregate root identifier class.
     */
    public static class HibernateJpaRepository<A extends AggregateRoot<I>, I>
            extends Jpa21RepositoryFactory.Jpa21Repository<A, I> {

        protected HibernateJpaRepository(Class<A> aggregateRootClass, Class<I> idClass) {
            super(aggregateRootClass, idClass);
        }

        @SuppressWarnings("unchecked")
        protected Stream<A> buildStream(Query query) {
            return ((AbstractProducedQuery<A>) query.unwrap(AbstractProducedQuery.class))
                    .stream();
        }
    }
}
