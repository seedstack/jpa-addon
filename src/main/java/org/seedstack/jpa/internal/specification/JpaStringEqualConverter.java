/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.seedstack.business.specification.StringEqualSpecification;
import org.seedstack.business.specification.StringSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class JpaStringEqualConverter<T> implements SpecificationConverter<StringEqualSpecification, JpaCriteriaBuilder<T>, Predicate> {
    @Override
    @SuppressFBWarnings(value = "DM_CONVERT_CASE", justification = "Better to use the default locale than force an english locale")
    public Predicate convert(StringEqualSpecification specification, JpaCriteriaBuilder<T> builder, SpecificationTranslator<JpaCriteriaBuilder<T>, Predicate> translator) {
        String expectedValue = specification.getExpectedString();
        CriteriaBuilder criteriaBuilder = builder.getCriteriaBuilder();
        StringSpecification.Options options = specification.getOptions();

        if (expectedValue == null) {
            return criteriaBuilder.isNull(applyOptions(options, criteriaBuilder, builder.pickExpression()));
        } else {
            return criteriaBuilder.equal(applyOptions(options, criteriaBuilder, builder.pickExpression()), options.isIgnoringCase() ? expectedValue.toUpperCase() : expectedValue);
        }
    }

    private Expression<String> applyOptions(StringSpecification.Options options, CriteriaBuilder criteriaBuilder, Expression<String> expression) {
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
