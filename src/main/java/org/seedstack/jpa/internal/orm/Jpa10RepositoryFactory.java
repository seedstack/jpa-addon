/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.orm;

import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseRepository;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.FalseSpecification;
import org.seedstack.business.specification.IdentitySpecification;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.specification.TrueSpecification;
import org.seedstack.jpa.spi.JpaRepositoryFactoryPriority;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.util.Optional;
import java.util.stream.Stream;

@Priority(JpaRepositoryFactoryPriority.JPA_10_PRIORITY)
public class Jpa10RepositoryFactory extends BaseJpaRepositoryFactory {
    /**
     * JPA 1.0 compliant implementation of business framework repository. It has limited support for specifications:
     * only {@link TrueSpecification}, {@link FalseSpecification} and {@link IdentitySpecification} are supported. They
     * respectively execute the operation on all aggregates, no aggregate and one specific aggregate.
     *
     * @param <A>  the aggregate root class.
     * @param <ID> the aggregate root identifier class.
     */
    public static class Jpa10Repository<A extends AggregateRoot<ID>, ID> extends BaseRepository<A, ID> {
        @Inject
        protected EntityManager entityManager;

        protected Jpa10Repository(Class<A> aggregateRootClass, Class<ID> idClass) {
            super(aggregateRootClass, idClass);
        }

        @Override
        public void add(A aggregate) {
            try {
                entityManager.persist(aggregate);
            } catch (EntityExistsException e) {
                throw new AggregateExistsException("Aggregate " + getAggregateRootClass() + " identified with " + aggregate.getId() + " is already persisted", e);
            }
        }

        @Override
        public Stream<A> get(Specification<A> specification, Options... options) {
            if (specification instanceof TrueSpecification) {
                return buildStream(entityManager.createQuery(String.format("select e from %s e", getAggregateRootClass().getCanonicalName())));
            } else if (specification instanceof FalseSpecification) {
                return Stream.empty();
            } else if (specification instanceof IdentitySpecification) {
                A found = entityManager.find(getAggregateRootClass(), ((IdentitySpecification) specification).getExpectedIdentifier());
                if (found != null) {
                    return Stream.of(found);
                } else {
                    return Stream.empty();
                }
            } else {
                throw new UnsupportedOperationException("Only TrueSpecification (get all entities), FalseSpecification (get no entity) or IdentitySpecification (get one entity) is supported with JPA 1.0");
            }
        }

        @Override
        public Optional<A> get(ID id) {
            return Optional.ofNullable(entityManager.find(getAggregateRootClass(), id));
        }

        @SuppressWarnings("unchecked")
        protected Stream<A> buildStream(Query query) {
            return query.getResultList().stream();
        }

        @Override
        public long count(Specification<A> specification) {
            if (specification instanceof TrueSpecification) {
                return (Long) entityManager.createQuery(String.format("select count(e) from %s e", getAggregateRootClass().getCanonicalName())).getSingleResult();
            } else if (specification instanceof FalseSpecification) {
                return 0L;
            } else if (specification instanceof IdentitySpecification) {
                A found = entityManager.find(getAggregateRootClass(), ((IdentitySpecification) specification).getExpectedIdentifier());
                if (found != null) {
                    return 1L;
                } else {
                    return 0L;
                }
            } else {
                throw new UnsupportedOperationException("Only TrueSpecification (count all entities), FalseSpecification (count no entity) or IdentitySpecification (count one entity) is supported with JPA 1.0");
            }
        }

        @Override
        public long remove(Specification<A> specification) {
            if (specification instanceof TrueSpecification) {
                return entityManager.createQuery(String.format("delete from %s", getAggregateRootClass().getCanonicalName())).executeUpdate();
            } else if (specification instanceof FalseSpecification) {
                return 0L;
            } else if (specification instanceof IdentitySpecification) {
                A found = entityManager.find(getAggregateRootClass(), ((IdentitySpecification) specification).getExpectedIdentifier());
                if (found != null) {
                    entityManager.remove(found);
                    return 1L;
                } else {
                    return 0L;
                }
            } else {
                throw new UnsupportedOperationException("Only TrueSpecification (remove all entities), FalseSpecification (remove no entity) or IdentitySpecification (remove one entity) is supported with JPA 1.0");
            }
        }

        @Override
        public void remove(ID id) {
            try {
                entityManager.remove(entityManager.getReference(getAggregateRootClass(), id));
            } catch (EntityNotFoundException e) {
                throw new AggregateNotFoundException("Aggregate " + getAggregateRootClass() + " identified with " + id + " cannot be removed", e);
            }
        }

        @Override
        public void remove(A aggregate) {
            entityManager.remove(aggregate);
        }

        @Override
        public void update(A aggregate) {
            ID id = aggregate.getId();
            try {
                entityManager.getReference(getAggregateRootClass(), id);
                entityManager.merge(aggregate);
            } catch (EntityNotFoundException e) {
                throw new AggregateNotFoundException("Aggregate " + getAggregateRootClass() + " identified with " + id + " cannot be updated", e);
            }
        }
    }

    @Override
    public boolean isSupporting(EntityManager entityManager) {
        return true;
    }

    @Override
    public <A extends AggregateRoot<ID>, ID> Repository<A, ID> doCreateRepository(Class<A> aggregateRootClass, Class<ID> identifierClass) {
        return new Jpa10Repository<>(aggregateRootClass, identifierClass);
    }
}
