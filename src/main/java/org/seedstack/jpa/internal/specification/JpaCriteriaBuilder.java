/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class JpaCriteriaBuilder<T> {
    private final CriteriaBuilder criteriaBuilder;
    private final Root<T> root;
    private final CriteriaQuery<T> criteriaQuery;

    public JpaCriteriaBuilder(CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<T> criteriaQuery) {
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
        this.criteriaQuery = criteriaQuery;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public Root<T> getRoot() {
        return root;
    }

    public CriteriaQuery<T> getCriteriaQuery() {
        return criteriaQuery;
    }
}
