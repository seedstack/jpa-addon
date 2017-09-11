/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.AndSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public class JpaAndConverter<T> implements SpecificationConverter<AndSpecification<T>, JpaCriteriaBuilder<T>, Predicate> {
    @Override
    public Predicate convert(AndSpecification<T> specification, JpaCriteriaBuilder<T> builder, SpecificationTranslator<JpaCriteriaBuilder<T>, Predicate> translator) {
        return builder.getCriteriaBuilder().and(
                Arrays.stream(specification.getSpecifications())
                        .map(spec -> translator.translate(spec, builder))
                        .toArray(Predicate[]::new)
        );
    }
}
