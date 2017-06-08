/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.orm;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.jpa.internal.specification.JpaCriteriaBuilder;
import org.seedstack.jpa.spi.JpaRepositoryFactoryPriority;
import org.seedstack.shed.reflect.Classes;

import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

@Priority(JpaRepositoryFactoryPriority.JPA_21_PRIORITY)
public class Jpa21RepositoryFactory extends BaseJpaRepositoryFactory {
    private static final String JAVAX_PERSISTENCE_CRITERIA_CRITERIA_DELETE = "javax.persistence.criteria.CriteriaDelete";
    private static final boolean jpa21Available = isJpa21Available();

    /**
     * Extending {@link Jpa20RepositoryFactory.Jpa20Repository}, this implementation of business framework repository
     * takes advantage of JPA 2.1 features. Specifications are fully supported on {@link #get(Specification, Options...)},
     * {@link #count(Specification)} and {@link #remove(Specification)} methods.
     *
     * @param <A>  the aggregate root class.
     * @param <ID> the aggregate root identifier class.
     */
    public static class Jpa21Repository<A extends AggregateRoot<ID>, ID> extends Jpa20RepositoryFactory.Jpa20Repository<A, ID> {

        protected Jpa21Repository(Class<A> aggregateRootClass, Class<ID> idClass) {
            super(aggregateRootClass, idClass);
        }

        @Override
        public long remove(Specification<A> specification) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            Class<A> entityClass = getAggregateRootClass();
            CriteriaDelete<A> cd = cb.createCriteriaDelete(entityClass);

            cd.where(specificationTranslator.translate(specification, new JpaCriteriaBuilder<>(cb, cd.from(entityClass))));

            return entityManager.createQuery(cd).executeUpdate();
        }

    }

    @Override
    public boolean isSupporting(EntityManager entityManager) {
        return jpa21Available;
    }

    @Override
    public <A extends AggregateRoot<ID>, ID> Repository<A, ID> doCreateRepository(Class<A> aggregateRootClass, Class<ID> identifierClass) {
        return new Jpa21Repository<>(aggregateRootClass, identifierClass);
    }

    private static boolean isJpa21Available() {
        return Classes.optional(JAVAX_PERSISTENCE_CRITERIA_CRITERIA_DELETE).isPresent();
    }
}
