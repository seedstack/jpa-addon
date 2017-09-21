/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.OrSpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public class JpaOrConverter implements SpecificationConverter<OrSpecification<?>, JpaTranslationContext<?>, Predicate> {
    @Override
    public Predicate convert(OrSpecification<?> specification, JpaTranslationContext<?> context, SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        return context.getCriteriaBuilder().or(
                Arrays.stream(specification.getSpecifications())
                        .map(spec -> translator.translate(spec, context))
                        .toArray(Predicate[]::new)
        );
    }
}
