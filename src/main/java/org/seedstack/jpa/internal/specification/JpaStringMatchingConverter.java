/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal.specification;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.StringMatchingSpecification;
import org.seedstack.business.specification.StringSpecification;
import org.seedstack.business.spi.SpecificationTranslator;

class JpaStringMatchingConverter extends JpaStringConverter<StringMatchingSpecification> {

  @Override
  @SuppressFBWarnings(value = "DM_CONVERT_CASE", justification = "Better to use the default "
      + "locale than force an english locale")
  public Predicate convert(StringMatchingSpecification specification,
      JpaTranslationContext<?> context,
      SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
    String expectedValue = specification.getExpectedString();
    CriteriaBuilder criteriaBuilder = context.getCriteriaBuilder();
    StringSpecification.Options options = specification.getOptions();

    if (expectedValue == null) {
      return criteriaBuilder.isNull(applyOptions(
          options,
          criteriaBuilder, context.pickExpression())
      );
    } else {
      return criteriaBuilder.like(
          applyOptions(options, criteriaBuilder, context.pickExpression()),
          convertPattern(options.isIgnoringCase() ? expectedValue.toUpperCase() : expectedValue)
      );
    }
  }

  private String convertPattern(String pattern) {
    return pattern.replace(StringMatchingSpecification.MULTI_CHARACTER_WILDCARD, "%")
        .replace(StringMatchingSpecification.SINGLE_CHARACTER_WILDCARD, "_");
  }
}
