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
import org.seedstack.business.spi.SpecificationTranslator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

class JpaStringEqualConverter extends JpaStringConverter<StringEqualSpecification> {
    @Override
    @SuppressFBWarnings(value = "DM_CONVERT_CASE", justification = "Better to use the default locale than force an english locale")
    public Predicate convert(StringEqualSpecification specification, JpaTranslationContext<?> context, SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        String expectedValue = specification.getExpectedString();
        CriteriaBuilder criteriaBuilder = context.getCriteriaBuilder();
        StringSpecification.Options options = specification.getOptions();

        if (expectedValue == null) {
            return criteriaBuilder.isNull(applyOptions(
                    options,
                    criteriaBuilder, context.pickExpression())
            );
        } else {
            return criteriaBuilder.equal(
                    applyOptions(options, criteriaBuilder, context.pickExpression()),
                    options.isIgnoringCase() ? expectedValue.toUpperCase() : expectedValue
            );
        }
    }
}
