/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import com.google.inject.assistedinject.Assisted;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.spi.GenericImplementation;
import org.seedstack.jpa.BaseJpaRepository;
import org.seedstack.jpa.Jpa;

import javax.inject.Inject;

/**
 * Default Jpa implementation for Repository. Used only when no implementation is provided for an aggregate.
 *
 * To inject this implementation you have to use {@link org.seedstack.business.domain.Repository} as follows:
 * <pre>
 * {@literal @}Inject
 * Repository{@literal <MyAggregateRoot, MyKey>} myAggregateRepository;
 * </pre>
 *
 * @param <AGGREGATE> the aggregate root
 * @param <KEY>       the aggregate key
 * @author pierre.thirouin@ext.mpsa.com
 * @see org.seedstack.business.domain.Repository
 * @see BaseJpaRepository
 */
@Jpa
@GenericImplementation
public class DefaultJpaRepository<AGGREGATE extends AggregateRoot<KEY>, KEY> extends BaseJpaRepository<AGGREGATE, KEY> {

    /**
     * Constructs a DefaultJpaRepository.
     *
     * @param genericClasses the resolved generics for the aggregate root class and the key class
     */
    @SuppressWarnings("unchecked")
    @Inject
    public DefaultJpaRepository(@Assisted Object[] genericClasses) {
        super((Class<AGGREGATE>) genericClasses[0], (Class<KEY>) genericClasses[1]);
    }
}
