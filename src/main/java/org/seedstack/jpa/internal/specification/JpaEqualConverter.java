/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.specification.EqualSpecification;
import org.seedstack.business.spi.domain.specification.SpecificationTranslator;
import org.seedstack.jpa.Jpa;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

@Jpa
public class JpaEqualConverter<A extends AggregateRoot<?>> extends AbstractJpaConverter<A, EqualSpecification<A>> {
    @Override
    public Predicate convert(EqualSpecification<A> specification, JpaCriteriaBuilder<A> builder, SpecificationTranslator<A, JpaCriteriaBuilder<A>, Predicate> translator) {
        Expression<A> expression = join(specification, builder.getRoot());
        Predicate predicate;
        if (specification.getExpectedValue() == null) {
            predicate = builder.getCriteriaBuilder().isNull(expression);
        } else {
            predicate = builder.getCriteriaBuilder().equal(expression, specification.getExpectedValue());
        }
        return predicate;
    }
}
