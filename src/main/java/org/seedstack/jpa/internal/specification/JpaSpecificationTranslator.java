/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.specification.BaseSpecificationTranslator;
import org.seedstack.business.specification.Specification;

import javax.persistence.criteria.Predicate;

public class JpaSpecificationTranslator extends BaseSpecificationTranslator<JpaCriteriaBuilder<?>, Predicate> {
    @Override
    public <T> Predicate translate(Specification<T> specification, JpaCriteriaBuilder<?> criteriaBuilder) {
        return convert(specification, criteriaBuilder);
    }
}
