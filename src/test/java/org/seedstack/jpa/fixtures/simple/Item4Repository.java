/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.simple;

import org.seedstack.seed.it.ITBind;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@ITBind
public class Item4Repository {
    private EntityManager entityManager;

    @Inject
    public Item4Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Item4 item) {
        entityManager.persist(item);
    }

    public void save(OtherItem4 item) {
        entityManager.persist(item);
    }
}