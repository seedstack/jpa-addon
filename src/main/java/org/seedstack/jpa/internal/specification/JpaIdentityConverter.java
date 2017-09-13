/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.specification.IdentitySpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class JpaIdentityConverter<A extends AggregateRoot<ID>, ID> implements SpecificationConverter<IdentitySpecification<A, ID>, JpaTranslationContext<A>, Predicate> {
    @Override
    @SuppressWarnings("unchecked")
    public Predicate convert(IdentitySpecification<A, ID> specification, JpaTranslationContext<A> context, SpecificationTranslator<JpaTranslationContext<A>, Predicate> translator) {
        ID expectedIdentifier = specification.getExpectedIdentifier();
        Root<A> root = context.getRoot();
        return context.getCriteriaBuilder().equal(
                root.get(root.getModel().getId((Class<ID>) expectedIdentifier.getClass())),
                expectedIdentifier
        );
    }
}
