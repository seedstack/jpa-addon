/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import com.google.inject.assistedinject.Assisted;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.specification.BaseSpecificationTranslator;
import org.seedstack.business.domain.specification.Specification;
import org.seedstack.business.spi.GenericImplementation;
import org.seedstack.business.spi.domain.specification.SpecificationTranslator;
import org.seedstack.jpa.Jpa;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;

@Jpa
@GenericImplementation
public class DefaultJpaSpecificationTranslator<A extends AggregateRoot<?>> extends BaseSpecificationTranslator<A, JpaCriteriaBuilder<A>, Predicate> implements SpecificationTranslator<A, JpaCriteriaBuilder<A>, Predicate> {
    @Inject
    @SuppressWarnings("unchecked")
    public DefaultJpaSpecificationTranslator(@Assisted Object[] genericClasses) {
        super(
                (Class<A>) genericClasses[0],
                (Class<JpaCriteriaBuilder<A>>) genericClasses[1],
                (Class<Predicate>) genericClasses[2]
        );
    }

    @Override
    public Predicate translate(Specification<A> specification, JpaCriteriaBuilder<A> jpaCriteriaBuilder) {
        return convert(specification, jpaCriteriaBuilder);
    }
}
