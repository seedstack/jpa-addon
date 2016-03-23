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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;


/**
 * This class can serve as a base class for the JPA repositories. It provides methods for common CRUD operations as
 * well as access to the entity manager through the {@link #getEntityManager()} protected method.
 *
 * @param <A> Aggregate root class
 * @param <K> Key class
 * @author epo.jemba@ext.mpsa.com
 * @author pierre.thirouin@ext.mpsa.com
 */
public abstract class BaseJpaRepository<A extends AggregateRoot<K>, K> extends BaseRepository<A, K> {
    @Inject
    protected EntityManager entityManager;

    /**
     * Constructor.
     */
    public BaseJpaRepository() {
    }

    protected BaseJpaRepository(Class<A> aggregateRootClass, Class<K> kClass) {
        super(aggregateRootClass, kClass);
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
    protected A doLoad(K id) {
        return entityManager.find(getAggregateRootClass(), id);
    }

    @Override
    protected void doClear() {
        entityManager.createQuery("delete from " + getAggregateRootClass().getCanonicalName()).executeUpdate();
    }

    @Override
    protected void doDelete(K id) {
        A aggregate = load(id);
        if (aggregate == null) {
            throw new EntityNotFoundException("Attempt to delete non-existent aggregate with id " + id + " of class " + getAggregateRootClass().getCanonicalName());
        }
        entityManager.remove(aggregate);
    }

    @Override
    protected void doDelete(A aggregate) {
        entityManager.remove(aggregate);
    }

    @Override
    protected void doPersist(A aggregate) {
        entityManager.persist(aggregate);
    }

    @Override
    protected A doSave(A aggregate) {
        return entityManager.merge(aggregate);
    }
}
