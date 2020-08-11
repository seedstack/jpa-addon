/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.LessThanSpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;

class JpaLessThanConverter<V extends Comparable<? super V>> implements
        SpecificationConverter<LessThanSpecification<V>, JpaTranslationContext<?>, Predicate> {

    @Override
    public Predicate convert(LessThanSpecification<V> specification, JpaTranslationContext<?> context,
            SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        return context.getCriteriaBuilder()
                .lessThan(context.pickExpression(), specification.getExpectedValue());
    }
}
