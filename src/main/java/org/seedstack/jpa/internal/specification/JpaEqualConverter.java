/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.EqualSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import javax.persistence.criteria.Predicate;

public class JpaEqualConverter<T, V> implements SpecificationConverter<EqualSpecification<V>, JpaCriteriaBuilder<T>, Predicate> {
    @Override
    public Predicate convert(EqualSpecification<V> specification, JpaCriteriaBuilder<T> builder, SpecificationTranslator<JpaCriteriaBuilder<T>, Predicate> translator) {
        if (specification.getExpectedValue() == null) {
            return builder.getCriteriaBuilder().isNull(builder.pickExpression());
        } else {
            return builder.getCriteriaBuilder().equal(builder.pickExpression(), specification.getExpectedValue());
        }
    }
}
