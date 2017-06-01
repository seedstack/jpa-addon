/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.TrueSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import javax.persistence.criteria.Predicate;

public class JpaTrueConverter<T> implements SpecificationConverter<TrueSpecification<T>, JpaCriteriaBuilder<T>, Predicate> {
    @Override
    public Predicate convert(TrueSpecification<T> specification, JpaCriteriaBuilder<T> builder, SpecificationTranslator<JpaCriteriaBuilder<T>, Predicate> translator) {
        // this is always true
        return builder.getCriteriaBuilder().and();
    }
}
