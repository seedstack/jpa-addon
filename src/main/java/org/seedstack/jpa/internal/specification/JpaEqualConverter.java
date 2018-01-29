/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.EqualSpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;

class JpaEqualConverter implements
        SpecificationConverter<EqualSpecification<?>, JpaTranslationContext<?>, Predicate> {

    @Override
    public Predicate convert(EqualSpecification<?> specification, JpaTranslationContext<?> context,
            SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        if (specification.getExpectedValue() == null) {
            return context.getCriteriaBuilder().isNull(context.pickExpression());
        } else {
            return context.getCriteriaBuilder()
                    .equal(context.pickExpression(), specification.getExpectedValue());
        }
    }
}
