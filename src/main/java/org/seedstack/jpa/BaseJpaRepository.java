/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.seed.core.utils.SeedReflectionUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 * This class can serve as a base class for the JPA repositories. It provides methods for common CRUD operations as
 * well as access to the entity manager through the {@link #getEntityManager()} protected method.
 *
 * @param <A> Aggregate root class
 * @param <K> Key class
 */
public abstract class BaseJpaRepository<A extends AggregateRoot<K>, K> extends BaseRepository<A, K> {
    @Inject
    protected EntityManager entityManager;

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
     * @param keyClass           the key class.
     */
    protected BaseJpaRepository(Class<A> aggregateRootClass, Class<K> keyClass) {
        super(aggregateRootClass, keyClass);
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
    public A load(K id) {
        return entityManager.find(getAggregateRootClass(), id);
    }

    @Override
    public boolean exists(K id) {
        if (SeedReflectionUtils.isClassPresent("javax.persistence.criteria.CriteriaBuilder")) {
            Class<K> keyClass = getKeyClass();
            Class<A> aggregateRootClass = getAggregateRootClass();

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<A> criteriaQuery = criteriaBuilder.createQuery(aggregateRootClass);
            Root<A> root = criteriaQuery.from(aggregateRootClass);
            criteriaQuery.select(root.get(root.getModel().getId(keyClass).getName()));
            criteriaQuery.where(criteriaBuilder.equal(root.get(root.getModel().getId(keyClass)), criteriaBuilder.parameter(keyClass, "id")));

            return entityManager.createQuery(criteriaQuery).setParameter("id", id).getResultList().size() == 1;
        } else {
            return load(id) != null;
        }
    }

    @Override
    public long count() {
        // query as JPQL for JPA 1.0 compatibility
        return (Long) entityManager.createQuery(String.format("select count(e) from %s e", getAggregateRootClass().getCanonicalName())).getSingleResult();
    }

    @Override
    public void clear() {
        // query as JPQL for JPA 1.0 compatibility
        entityManager.createQuery(String.format("delete from %s", getAggregateRootClass().getCanonicalName())).executeUpdate();
    }

    @Override
    public void delete(K id) {
        A aggregate = load(id);
        if (aggregate == null) {
            throw new EntityNotFoundException("Attempt to delete non-existent aggregate with id " + id + " of class " + getAggregateRootClass().getCanonicalName());
        }
        entityManager.remove(aggregate);
    }

    @Override
    public void delete(A aggregate) {
        entityManager.remove(aggregate);
    }

    @Override
    public void persist(A aggregate) {
        entityManager.persist(aggregate);
    }

    @Override
    public A save(A aggregate) {
        return entityManager.merge(aggregate);
    }
}
