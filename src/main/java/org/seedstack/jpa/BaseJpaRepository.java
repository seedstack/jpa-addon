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
import org.hibernate.query.internal.AbstractProducedQuery;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.spi.specification.SpecificationTranslator;
import org.seedstack.jpa.internal.specification.JpaCriteriaBuilder;
import org.seedstack.seed.Logging;
import org.seedstack.shed.reflect.Classes;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * This class can serve as a base class for the JPA repositories. It provides methods for common CRUD operations as
 * well as access to the entity manager through the {@link #getEntityManager()} protected method.
 *
 * @param <A>  Aggregate root class
 * @param <ID> Identifier class
 */
public abstract class BaseJpaRepository<A extends AggregateRoot<ID>, ID> extends BaseRepository<A, ID> {
    private static final String ORG_HIBERNATE_QUERY_QUERY = "org.hibernate.query.Query";
    private static final boolean hibernateStreamingAvailable = isHibernateStreamingAvailable();
    @Logging
    private Logger logger;
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

        cq.where(getSpecificationTranslator().translate(specification, new JpaCriteriaBuilder<>(cb, root)));
        if (!root.getJoins().isEmpty()) {
            // When we have joins, we need to deduplicate the results
            cq.distinct(true);
        }

        TypedQuery<A> query = entityManager.createQuery(cq);
        if (hibernateStreamingAvailable) {
            try {
                return streamWithHibernate(query);
            } catch (Exception e) {
                // ignore, fallback to classic JPA
            }
        }
        return query.getResultList().stream();
    }

    @Override
    public Optional<A> get(ID id) {
        return Optional.ofNullable(entityManager.find(getAggregateRootClass(), id));
    }

    @Override
    public long remove(Specification<A> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<A> entityClass = getAggregateRootClass();
        CriteriaDelete<A> cd = cb.createCriteriaDelete(entityClass);

        cd.where(getSpecificationTranslator().translate(specification, new JpaCriteriaBuilder<>(cb, cd.from(entityClass))));

        return entityManager.createQuery(cd).executeUpdate();
    }


    @Override
    public boolean remove(ID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<A> entityClass = getAggregateRootClass();
        CriteriaDelete<A> cd = cb.createCriteriaDelete(entityClass);

        Root<A> root = cd.from(entityClass);
        cd.where(cb.equal(root.get(root.getModel().getId(getIdentifierClass())), id));

        int deletedCount = entityManager.createQuery(cd).executeUpdate();
        if (deletedCount > 1) {
            throw new IllegalStateException("More than one aggregate has been removed");
        }
        return deletedCount == 1;
    }

    @Override
    public boolean remove(A aggregate) {
        try {
            entityManager.remove(aggregate);
            return true;
        } catch (IllegalArgumentException e) {
            logger.debug("Aggregate " + String.valueOf(aggregate) + " could not be removed by JPA", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Stream<A> streamWithHibernate(TypedQuery<A> query) {
        return ((AbstractProducedQuery<A>) query.unwrap(AbstractProducedQuery.class)).stream();
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

    private static boolean isHibernateStreamingAvailable() {
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
}
