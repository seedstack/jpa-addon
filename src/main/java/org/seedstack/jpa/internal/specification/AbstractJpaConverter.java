/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.specification.AbstractValueSpecification;
import org.seedstack.business.spi.domain.specification.SpecificationConverter;
import org.seedstack.jpa.internal.JpaErrorCode;
import org.seedstack.seed.SeedException;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

abstract class AbstractJpaConverter<A extends AggregateRoot<?>, S extends AbstractValueSpecification<A>> implements SpecificationConverter<A, S, JpaCriteriaBuilder<A>, Predicate> {
    <T> Expression<T> join(S specification, From<?, ?> from) {
        Expression<T> path;
        try {
            String[] properties = specification.getPath().split("\\.");
            if (properties.length > 1) {
                path = joinRecursively(properties, findOrCreateJoin(properties[0], from), 1).get(properties[properties.length - 1]);
            } else {
                path = from.get(properties[0]);
            }
        } catch (IllegalArgumentException e) {
            throw SeedException.wrap(e, JpaErrorCode.UNABLE_TO_CREATE_JPA_JOIN_FOR_SPECIFICATION)
                    .put("property", specification.getPath());
        }
        return path;
    }

    /**
     * Join recursively using the split property path.
     *
     * @param properties property path.
     * @param join       current join.
     * @param index      index of the current property to be joined.
     * @return the resulting join.
     */
    private Join<?, ?> joinRecursively(String[] properties, Join<?, ?> join, int index) {
        if (index < properties.length - 1) {
            return joinRecursively(properties, findOrCreateJoin(properties[index], join), index + 1);
        } else {
            return join;
        }
    }

    /**
     * Find an existing join for the property or create a new join.
     *
     * @param property property to be joined
     * @param from     clause letting us keep track of all already existing joins
     * @return the join
     */
    private Join<?, ?> findOrCreateJoin(String property, From<?, ?> from) {
        for (Join<?, ?> rootJoin : from.getJoins()) {
            if (rootJoin.getAttribute().getName().equals(property)) {
                return rootJoin;
            }
        }
        return from.join(property);
    }
}
