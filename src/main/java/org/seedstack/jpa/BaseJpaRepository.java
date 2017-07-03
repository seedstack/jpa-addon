/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.jpa.internal.JpaErrorCode;
import org.seedstack.jpa.spi.JpaRepositoryFactory;
import org.seedstack.seed.SeedException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private List<JpaRepositoryFactory> jpaRepositoryFactories;

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
        resolveImplementation().add(aggregate);
    }

    @Override
    public Stream<A> get(Specification<A> specification, Option... options) {
        return resolveImplementation().get(specification, options);
    }

    @Override
    public Optional<A> get(ID id) {
        return resolveImplementation().get(id);
    }

    @Override
    public boolean contains(Specification<A> specification) {
        return resolveImplementation().contains(specification);
    }

    @Override
    public boolean contains(ID id) {
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
    public void remove(ID id) {
        resolveImplementation().remove(id);
    }

    @Override
    public void remove(A aggregate) {
        resolveImplementation().remove(aggregate);
    }

    @Override
    public void update(A aggregate) {
        resolveImplementation().update(aggregate);
    }

    @Override
    public void clear() {
        resolveImplementation().clear();
    }

    @Inject
    @SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD", justification = "Method called by Guice")
    private void buildFactoryList(Set<JpaRepositoryFactory> jpaRepositoryFactories) {
        this.jpaRepositoryFactories = Lists.newArrayList(jpaRepositoryFactories);
        this.jpaRepositoryFactories.sort(Collections.reverseOrder(Comparator.comparingInt(this::getPriority)));
    }

    private int getPriority(JpaRepositoryFactory jpaRepositoryFactory) {
        Priority annotation = jpaRepositoryFactory.getClass().getAnnotation(Priority.class);
        return annotation != null ? annotation.value() : 0;
    }

    @SuppressWarnings("unchecked")
    private Repository<A, ID> resolveImplementation() {
        for (JpaRepositoryFactory jpaRepositoryFactory : jpaRepositoryFactories) {
            if (jpaRepositoryFactory.isSupporting(entityManager)) {
                return jpaRepositoryFactory.createRepository(getAggregateRootClass(), getIdentifierClass());
            }
        }
        throw SeedException.createNew(JpaErrorCode.UNABLE_TO_FIND_A_SUITABLE_JPA_REPOSITORY_IMPLEMENTATION);
    }
}
