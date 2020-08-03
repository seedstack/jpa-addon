/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.fixtures.simple;

import org.seedstack.seed.Bind;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@Bind
public class Item5Repository {
    private EntityManager entityManager;

    @Inject
    public Item5Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Item5 item) {
        entityManager.persist(item);
    }

    public Item5 load(long key) {
        return entityManager.find(Item5.class, key);
    }
}
