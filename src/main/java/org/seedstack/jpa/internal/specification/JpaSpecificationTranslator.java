/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.Predicate;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.spi.BaseSpecificationTranslator;

class JpaSpecificationTranslator extends
        BaseSpecificationTranslator<JpaTranslationContext<?>, Predicate> {

    @Override
    public <S extends Specification<?>> Predicate translate(S specification,
            JpaTranslationContext<?> context) {
        return convert(specification, context);
    }
}
