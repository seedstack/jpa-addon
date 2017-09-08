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
import org.seedstack.business.domain.LimitOption;
import org.seedstack.business.domain.OffsetOption;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.domain.SortOption;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

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
        private static final String SELECT_QUERY = "select e from %s e";
        private static final String COUNT_QUERY = "select count(e) from %s e";
        private static final String DELETE_QUERY = "delete from %s";

        @Inject
        protected EntityManager entityManager;

        protected Jpa10Repository(Class<A> aggregateRootClass, Class<ID> idClass) {
            super(aggregateRootClass, idClass);
        }

        @Override
        public void add(A aggregate) throws AggregateExistsException {
            try {
                entityManager.persist(aggregate);
            } catch (EntityExistsException e) {
                throw new AggregateExistsException("Aggregate " + getAggregateRootClass() + " identified with " + aggregate.getId() + " is already persisted", e);
            }
        }

        @Override
        public Stream<A> get(Specification<A> specification, Option... options) {
            if (specification instanceof TrueSpecification) {
                return buildStream(applyOffsetAndLimit(entityManager.createQuery(applySort(String.format(SELECT_QUERY, getAggregateRootClass().getCanonicalName()), options)), options));
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
                throw new UnsupportedOperationException("Only TrueSpecification (get all), FalseSpecification (get none) or IdentitySpecification (get one) is supported with JPA 1.0");
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
                return (Long) entityManager.createQuery(String.format(COUNT_QUERY, getAggregateRootClass().getCanonicalName())).getSingleResult();
            } else if (specification instanceof FalseSpecification) {
                return 0L;
            } else if (specification instanceof IdentitySpecification) {
                // With JPA 1.0 a load is required to check for entity existence without knowing its model
                A found = entityManager.find(getAggregateRootClass(), ((IdentitySpecification) specification).getExpectedIdentifier());
                if (found != null) {
                    return 1L;
                } else {
                    return 0L;
                }
            } else {
                throw new UnsupportedOperationException("Only TrueSpecification (count all), FalseSpecification (count none) or IdentitySpecification (count one) is supported with JPA 1.0");
            }
        }

        @Override
        public long remove(Specification<A> specification) {
            if (specification instanceof TrueSpecification) {
                return entityManager.createQuery(String.format(DELETE_QUERY, getAggregateRootClass().getCanonicalName())).executeUpdate();
            } else if (specification instanceof FalseSpecification) {
                return 0L;
            } else if (specification instanceof IdentitySpecification) {
                try {
                    entityManager.remove(entityManager.getReference(getAggregateRootClass(), ((IdentitySpecification) specification).getExpectedIdentifier()));
                    return 1L;
                } catch (EntityNotFoundException e) {
                    return 0L;
                }
            } else {
                throw new UnsupportedOperationException("Only TrueSpecification (remove all), FalseSpecification (remove none) or IdentitySpecification (remove one) is supported with JPA 1.0");
            }
        }

        @Override
        public void remove(ID id) throws AggregateNotFoundException {
            try {
                entityManager.remove(entityManager.getReference(getAggregateRootClass(), id));
            } catch (EntityNotFoundException e) {
                throw new AggregateNotFoundException("Non-existent aggregate " + getAggregateRootClass() + " identified with " + id + " cannot be removed", e);
            }
        }

        @Override
        public void remove(A aggregate) throws AggregateNotFoundException {
            try {
                entityManager.remove(aggregate);
            } catch (EntityNotFoundException e) {
                throw new AggregateNotFoundException("Non-existent aggregate " + getAggregateRootClass() + " identified with " + aggregate.getId() + " cannot be removed", e);
            }
        }

        @Override
        public void update(A aggregate) throws AggregateNotFoundException {
            if (!contains(aggregate)) {
                throw new AggregateNotFoundException("Non-existent aggregate " + getAggregateRootClass() + " identified with " + aggregate.getId() + " cannot be updated");
            }
            entityManager.merge(aggregate);
        }

        protected Query applyOffsetAndLimit(Query query, Option... options) {
            for (Option option : options) {
                if (option instanceof OffsetOption) {
                    applyOffset(query, (OffsetOption) option);
                } else if (option instanceof LimitOption) {
                    applyLimit(query, (LimitOption) option);
                }
            }
            return query;
        }

        private void applyOffset(Query query, OffsetOption offsetOption) {
            long offset = offsetOption.getOffset();
            checkArgument(offset > Integer.MAX_VALUE, "JPA only supports offsetting results up to " + Integer.MAX_VALUE);
            query.setFirstResult((int) offset);
        }

        private void applyLimit(Query query, LimitOption limitOption) {
            long limit = limitOption.getLimit();
            checkArgument(limit > Integer.MAX_VALUE, "JPA only supports result limiting up to " + Integer.MAX_VALUE);
            query.setMaxResults((int) limit);
        }

        private String applySort(String jpql, Option... options) {
            for (Option option : options) {
                if (option instanceof SortOption) {
                    StringBuilder sb = new StringBuilder(jpql);
                    List<SortOption.SortedAttribute> sortedAttributes = ((SortOption) option).getSortedAttributes();
                    if (!sortedAttributes.isEmpty()) {
                        sb.append(" order by ");
                        for (int i = 0; i < sortedAttributes.size(); i++) {
                            if (i > 0) {
                                sb.append(", ");
                            }
                            sb.append(String.format("e.%s", sortedAttributes.get(i).getAttribute()));
                        }
                    }
                    return sb.toString();
                }
            }
            return jpql;
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
