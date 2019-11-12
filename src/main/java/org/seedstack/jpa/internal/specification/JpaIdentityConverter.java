/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.specification.IdentitySpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;

class JpaIdentityConverter<A extends AggregateRoot<I>, I> implements
        SpecificationConverter<IdentitySpecification<A, I>, JpaTranslationContext<A>, Predicate> {

    @Override
    @SuppressWarnings("unchecked")
    public Predicate convert(IdentitySpecification<A, I> specification,
            JpaTranslationContext<A> context,
            SpecificationTranslator<JpaTranslationContext<A>, Predicate> translator) {
        I expectedIdentifier = specification.getExpectedIdentifier();
        Root<A> root = context.getRoot();
        return context.getCriteriaBuilder().equal(
                root.get(root.getModel().getId((Class<I>) expectedIdentifier.getClass())),
                expectedIdentifier
        );
    }
}
