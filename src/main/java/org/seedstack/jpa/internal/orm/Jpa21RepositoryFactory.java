/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal.orm;

import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.Specification;
import org.seedstack.jpa.internal.specification.JpaTranslationContext;
import org.seedstack.jpa.spi.JpaRepositoryFactoryPriority;
import org.seedstack.shed.reflect.Classes;

@Priority(JpaRepositoryFactoryPriority.JPA_21_PRIORITY)
public class Jpa21RepositoryFactory extends BaseJpaRepositoryFactory {

  private static final String CRITERIA_DELETE = "javax.persistence.criteria.CriteriaDelete";
  private static final boolean jpa21Available = isJpa21Available();

  private static boolean isJpa21Available() {
    return Classes.optional(CRITERIA_DELETE).isPresent();
  }

  @Override
  public boolean isSupporting(EntityManager entityManager) {
    return jpa21Available;
  }

  @Override
  public <A extends AggregateRoot<I>, I> Repository<A, I> doCreateRepository(
      Class<A> aggregateRootClass, Class<I> identifierClass) {
    return new Jpa21Repository<>(aggregateRootClass, identifierClass);
  }

  /**
   * Extending {@link Jpa20RepositoryFactory.Jpa20Repository}, this implementation of business
   * framework repository takes advantage of JPA 2.1 features. Specifications are fully supported on
   * {@link #get(Specification, Option...)}, {@link #count(Specification)} and {@link
   * #remove(Specification)} methods.
   *
   * @param <A> the aggregate root class.
   * @param <I> the aggregate root identifier class.
   */
  public static class Jpa21Repository<A extends AggregateRoot<I>, I>
      extends Jpa20RepositoryFactory.Jpa20Repository<A, I> {

    protected Jpa21Repository(Class<A> aggregateRootClass, Class<I> idClass) {
      super(aggregateRootClass, idClass);
    }

    @Override
    public long remove(Specification<A> specification) {
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();
      Class<A> entityClass = getAggregateRootClass();
      CriteriaDelete<A> cd = cb.createCriteriaDelete(entityClass);

      cd.where(specificationTranslator
          .translate(specification, new JpaTranslationContext<>(cb, cd.from(entityClass))));

      return entityManager.createQuery(cd).executeUpdate();
    }

  }
}
