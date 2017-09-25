/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal;

import com.google.inject.assistedinject.Assisted;
import javax.inject.Inject;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.spi.GenericImplementation;
import org.seedstack.jpa.BaseJpaRepository;
import org.seedstack.jpa.Jpa;

/**
 * Default Jpa implementation for Repository. Used only when no implementation is provided for an
 * aggregate. <p> To inject this implementation you have to use {@link
 * org.seedstack.business.domain.Repository} as follows: </p>
 * <pre>
 * {@literal @}Inject
 * Repository{@literal <MyAggregateRoot, MyKey>} myAggregateRepository;
 * </pre>
 *
 * @param <A> the aggregate root type.
 * @param <I> the aggregate root identifier type.
 * @see org.seedstack.business.domain.Repository
 * @see BaseJpaRepository
 */
@Jpa
@GenericImplementation
public class DefaultJpaRepository<A extends AggregateRoot<I>, I> extends BaseJpaRepository<A, I> {

  /**
   * Constructs a DefaultJpaRepository.
   *
   * @param genericClasses the resolved generics for the aggregate root class and the key class
   */
  @SuppressWarnings("unchecked")
  @Inject
  public DefaultJpaRepository(@Assisted Object[] genericClasses) {
    super((Class<A>) genericClasses[0], (Class<I>) genericClasses[1]);
  }
}
