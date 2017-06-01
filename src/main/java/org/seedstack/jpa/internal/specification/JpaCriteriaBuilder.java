/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import com.google.common.base.Preconditions;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public class JpaCriteriaBuilder<T> {
    private final CriteriaBuilder criteriaBuilder;
    private final Root<T> root;
    private Expression<?> expression;

    public JpaCriteriaBuilder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public Root<T> getRoot() {
        return root;
    }

    @SuppressWarnings("unchecked")
    public <E> Expression<E> pickExpression() {
        Preconditions.checkState(this.expression != null, "No expression has been set");
        Expression<E> result = (Expression<E>) this.expression;
        expression = null;
        return result;
    }

    public <E> void setExpression(Expression<E> expression) {
        Preconditions.checkState(this.expression == null, "An expression is already set");
        this.expression = expression;
    }
}
