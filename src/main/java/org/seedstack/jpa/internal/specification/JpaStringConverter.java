/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.StringSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public abstract class JpaStringConverter<T, S extends StringSpecification> implements SpecificationConverter<S, JpaCriteriaBuilder<T>, Predicate> {
    protected Expression<String> applyOptions(StringSpecification.Options options, CriteriaBuilder criteriaBuilder, Expression<String> expression) {
        if (options.isTrimmed()) {
            expression = criteriaBuilder.trim(CriteriaBuilder.Trimspec.BOTH, expression);
        } else {
            if (options.isLeftTrimmed()) {
                expression = criteriaBuilder.trim(CriteriaBuilder.Trimspec.LEADING, expression);
            }
            if (options.isRightTrimmed()) {
                expression = criteriaBuilder.trim(CriteriaBuilder.Trimspec.TRAILING, expression);
            }
        }
        if (options.isIgnoringCase()) {
            expression = criteriaBuilder.upper(expression);
        }
        return expression;
    }
}
