/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
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

public class JpaTranslationContext<T> {
    private final CriteriaBuilder criteriaBuilder;
    private final Root<T> root;
    private Expression<?> expression;

    public JpaTranslationContext(CriteriaBuilder criteriaBuilder, Root<T> root) {
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
    }

    public JpaTranslationContext(JpaTranslationContext<T> source) {
        this.criteriaBuilder = source.criteriaBuilder;
        this.root = source.root;
        this.expression = source.expression;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    /**
     * Returns the JPA root entity used to build the criteria.
     *
     * @return the JPA root entity.
     */
    public Root<T> getRoot() {
        return root;
    }

    /**
     * Returns the expression currently active in the building context.
     *
     * @param <E> the type of the expression.
     * @return the JPA expression.
     */
    @SuppressWarnings("unchecked")
    public <E> Expression<E> pickExpression() {
        Preconditions.checkState(this.expression != null, "No expression has been set");
        Expression<E> result = (Expression<E>) this.expression;
        expression = null;
        return result;
    }

    /**
     * Sets the expression currently active in the building context.
     *
     * @param expression the expression.
     * @param <E>        the type of the expressions.
     */
    public <E> void setExpression(Expression<E> expression) {
        Preconditions.checkState(this.expression == null, "An expression is already set");
        this.expression = expression;
    }
}
