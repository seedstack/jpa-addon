/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.jpa.internal.JpaErrorCode;
import org.seedstack.jpa.spi.JpaRepositoryFactory;
import org.seedstack.seed.SeedException;

/**
 * This class can serve as a base class for the JPA repositories. It provides methods for common
 * CRUD operations as well as access to the entity manager through the {@link #getEntityManager()}
 * protected method.
 *
 * @param <A> Aggregate root class
 * @param <I> Identifier class
 */
public abstract class BaseJpaRepository<A extends AggregateRoot<I>, I>
        extends BaseRepository<A, I> {

    @Inject
    private EntityManager entityManager;
    @Inject
    private Set<JpaRepositoryFactory> jpaRepositoryFactories;

    /**
     * Default constructor.
     */
    public BaseJpaRepository() {
    }

    /**
     * This protected constructor is intended to be used by JPA repositories that already know their
     * aggregate root and key classes. It is notably used internally by {@link
     * org.seedstack.jpa.internal.DefaultJpaRepository} for providing a default JPA repository for all
     * aggregates.
     *
     * @param aggregateRootClass the aggregate root class.
     * @param idClass            the id class.
     */
    protected BaseJpaRepository(Class<A> aggregateRootClass, Class<I> idClass) {
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
    public void add(A aggregate) throws AggregateExistsException {
        resolveImplementation().add(aggregate);
    }

    @Override
    public Stream<A> get(Specification<A> specification, Option... options) {
        return resolveImplementation().get(specification, options);
    }

    @Override
    public Optional<A> get(I id) {
        return resolveImplementation().get(id);
    }

    @Override
    public boolean contains(Specification<A> specification) {
        return resolveImplementation().contains(specification);
    }

    @Override
    public boolean contains(I id) {
        return resolveImplementation().contains(id);
    }

    @Override
    public boolean contains(A aggregate) {
        return resolveImplementation().contains(aggregate);
    }

    @Override
    public long count(Specification<A> specification) {
        return resolveImplementation().count(specification);
    }

    @Override
    public long size() {
        return resolveImplementation().size();
    }

    @Override
    public boolean isEmpty() {
        return resolveImplementation().isEmpty();
    }

    @Override
    public long remove(Specification<A> specification) {
        return resolveImplementation().remove(specification);
    }

    @Override
    public void remove(I id) throws AggregateNotFoundException {
        resolveImplementation().remove(id);
    }

    @Override
    public void remove(A aggregate) throws AggregateNotFoundException {
        resolveImplementation().remove(aggregate);
    }

    @Override
    public A update(A aggregate) throws AggregateNotFoundException {
        return resolveImplementation().update(aggregate);
    }

    @Override
    public void clear() {
        resolveImplementation().clear();
    }

    @SuppressWarnings("unchecked")
    private Repository<A, I> resolveImplementation() {
        for (JpaRepositoryFactory jpaRepositoryFactory : jpaRepositoryFactories) {
            if (jpaRepositoryFactory.isSupporting(entityManager)) {
                return jpaRepositoryFactory.createRepository(getAggregateRootClass(), getIdentifierClass());
            }
        }
        throw SeedException
                .createNew(JpaErrorCode.UNABLE_TO_FIND_A_SUITABLE_JPA_REPOSITORY_IMPLEMENTATION);
    }
}
