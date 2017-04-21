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
import org.seedstack.business.domain.RepositoryOptions;
import org.seedstack.business.domain.specification.Specification;
import org.seedstack.business.spi.domain.specification.SpecificationTranslator;
import org.seedstack.jpa.internal.specification.JpaCriteriaBuilder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
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
    public Optional<A> get(ID id) {
        return Optional.ofNullable(entityManager.find(getAggregateRootClass(), id));
    }

    @Override
    public Stream<A> get(Specification<A> specification, RepositoryOptions... options) {
        Class<A> entityClass = getAggregateRootClass();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<A> cq = cb.createQuery(entityClass);
        applySpecification(specification, cb, cq, cq.from(entityClass));
        return entityManager.createQuery(cq).getResultList().stream();
    }

    @Override
    public boolean contains(ID id) {
        // TODO: review performance
        Class<ID> keyClass = getIdentifierClass();
        Class<A> aggregateRootClass = getAggregateRootClass();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<A> criteriaQuery = criteriaBuilder.createQuery(aggregateRootClass);
        Root<A> root = criteriaQuery.from(aggregateRootClass);
        criteriaQuery.select(root.get(root.getModel().getId(keyClass).getName()));
        criteriaQuery.where(criteriaBuilder.equal(root.get(root.getModel().getId(keyClass)), criteriaBuilder.parameter(keyClass, "id")));

        return entityManager.createQuery(criteriaQuery).setParameter("id", id).getResultList().size() == 1;
    }

    @Override
    public long count(Specification<A> specification) {
        // TODO: real impl
        return get(specification).count();
    }

    @Override
    public long count() {
        // TODO: update for JPA 2
        // query as JPQL for JPA 1.0 compatibility
        return (Long) entityManager.createQuery(String.format("select count(e) from %s e", getAggregateRootClass().getCanonicalName())).getSingleResult();
    }

    @Override
    public void add(A aggregate) {
        entityManager.persist(aggregate);
    }

    @Override
    public A update(A aggregate) {
        return entityManager.merge(aggregate);
    }

    @Override
    public void remove(A aggregate) {
        entityManager.remove(aggregate);
    }

    @Override
    public void remove(ID id) {
        Optional<A> optionalAggregate = get(id);
        if (optionalAggregate.isPresent()) {
            entityManager.remove(optionalAggregate.get());
        } else {
            throw new EntityNotFoundException("Attempt to delete non-existent aggregate with id " + id + " of class " + getAggregateRootClass().getCanonicalName());
        }
    }

    @Override
    public long remove(Specification<A> specification) {
        // TODO: real impl
        AtomicLong count = new AtomicLong(0L);
        get(specification).forEach(aggregate -> {
            remove(aggregate);
            count.incrementAndGet();
        });
        return count.get();
    }

    @Override
    public void clear() {
        // TODO: update for JPA 2
        // query as JPQL for JPA 1.0 compatibility
        entityManager.createQuery(String.format("delete from %s", getAggregateRootClass().getCanonicalName())).executeUpdate();
    }

    private <T> void applySpecification(Specification<A> specification, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root) {
        cq.where(getSpecificationTranslator().translate(specification, new JpaCriteriaBuilder<>(cb, root, cq)));
        if (!root.getJoins().isEmpty()) {
            // When we have joins, we need to deduplicate the results
            cq.distinct(true);
        }
    }

    @SuppressWarnings("unchecked")
    private SpecificationTranslator<A, JpaCriteriaBuilder, Predicate> getSpecificationTranslator() {
        return injector.getInstance(Key.get(
                (TypeLiteral<SpecificationTranslator<A, JpaCriteriaBuilder, Predicate>>) TypeLiteral.get(Types.newParameterizedType(
                        SpecificationTranslator.class,
                        getAggregateRootClass(),
                        JpaCriteriaBuilder.class,
                        Predicate.class)),
                Jpa.class)
        );
    }
}
