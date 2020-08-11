/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.AttributeSpecification;
import org.seedstack.business.spi.SpecificationConverter;
import org.seedstack.business.spi.SpecificationTranslator;
import org.seedstack.jpa.internal.JpaUtils;

class JpaAttributeConverter implements
        SpecificationConverter<AttributeSpecification<?, ?>, JpaTranslationContext<?>, Predicate> {

    @Override
    public Predicate convert(AttributeSpecification<?, ?> specification,
            JpaTranslationContext<?> context,
            SpecificationTranslator<JpaTranslationContext<?>, Predicate> translator) {
        context.setExpression(JpaUtils.join(specification.getPath(), context.getRoot()));
        return translator.translate(specification.getValueSpecification(), context);
    }
}
