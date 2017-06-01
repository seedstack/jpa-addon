/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.spi.specification.SpecificationTranslator;
import org.seedstack.jpa.internal.specification.JpaCriteriaBuilder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;


/**
 * This class can serve as a base class for the JPA repositories. It provides methods for common CRUD operations as
 * well as access to the entity manager through the {@link #getEntityManager()} protected method.
 *
 * @param <A>  Aggregate root class
 * @param <ID> Identifier class
 */
public abstract class BaseJpaRepository<A extends AggregateRoot<ID>, ID> extends BaseRepository<A, ID> {
    @Inject
    private EntityManager entityManager;
    @Inject
    private Injector injector;

    /**
     * Default constructor.
     */
    public BaseJpaRepository() {
    }

    /**
     * This protected constructor is intended to be used by JPA repositories that already know their aggregate root and
     * key classes. It is notably used internally by {@link org.seedstack.jpa.internal.DefaultJpaRepository} for providing
     * a default JPA repository for all aggregates.
     *
     * @param aggregateRootClass the aggregate root class.
     * @param idClass            the id class.
     */
    protected BaseJpaRepository(Class<A> aggregateRootClass, Class<ID> idClass) {
        super(aggregateRootClass, idClass);
    }

    /**
     * Provides access to the entity manager for implementing custom data access methods.
     *
     * @return the entity manager.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void add(A aggregate) {
        entityManager.persist(aggregate);
    }

    @Override
    public Stream<A> get(Specification<A> specification, Repository.Options... options) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<A> entityClass = getAggregateRootClass();
        CriteriaQuery<A> cq = cb.createQuery(entityClass);
        Root<A> root = cq.from(entityClass);

        // Apply specification as where clause
        cq.where(getSpecificationTranslator().translate(specification, new JpaCriteriaBuilder<>(cb, root)));
        if (!root.getJoins().isEmpty()) {
            // When we have joins, we need to deduplicate the results
            cq.distinct(true);
        }

        // Execute query
        return entityManager.createQuery(cq).getResultList().stream();
    }

    @Override
    public long remove(Specification<A> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<A> entityClass = getAggregateRootClass();
        CriteriaDelete<A> cd = cb.createCriteriaDelete(entityClass);

        // Apply specification as where clause
        cd.where(getSpecificationTranslator().translate(specification, new JpaCriteriaBuilder<>(cb, cd.from(entityClass))));

        // Execute query
        return entityManager.createQuery(cd).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private SpecificationTranslator<JpaCriteriaBuilder, Predicate> getSpecificationTranslator() {
        return injector.getInstance(Key.get(
                (TypeLiteral<SpecificationTranslator<JpaCriteriaBuilder, Predicate>>) TypeLiteral.get(Types.newParameterizedType(
                        SpecificationTranslator.class,
                        JpaCriteriaBuilder.class,
                        Predicate.class)
                )
        ));
    }
}
