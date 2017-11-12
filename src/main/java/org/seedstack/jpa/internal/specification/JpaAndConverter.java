/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal.specification;

import java.util.Arrays;
import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.AndSpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;

class JpaAndConverter implements
        SpecificationConverter<AndSpecification<?>, JpaTranslationContext<?>, Predicate> {

    @Override
    public Predicate convert(AndSpecification<?> specification, JpaTranslationContext<?> context,
            SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        return context.getCriteriaBuilder().and(
                Arrays.stream(specification.getSpecifications())
                        .map(spec -> translator.translate(spec, context))
                        .toArray(Predicate[]::new)
        );
    }
}
