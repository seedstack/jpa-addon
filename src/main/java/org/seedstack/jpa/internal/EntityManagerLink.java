/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;

class EntityManagerLink implements TransactionalLink<EntityManager> {

    private final ThreadLocal<Deque<EntityManager>> perThreadObjectContainer = ThreadLocal
            .withInitial(ArrayDeque::new);

    @Override
    public EntityManager get() {
        EntityManager entityManager = this.perThreadObjectContainer.get().peek();

        if (entityManager == null) {
            throw SeedException.createNew(JpaErrorCode.ACCESSING_ENTITY_MANAGER_OUTSIDE_TRANSACTION);
        }

        return entityManager;
    }

    EntityTransaction getCurrentTransaction() {
        EntityManager entityManager = this.perThreadObjectContainer.get().peek();

        if (entityManager != null) {
            return entityManager.getTransaction();
        } else {
            return null;
        }
    }

    void push(EntityManager entityManager) {
        perThreadObjectContainer.get().push(entityManager);
    }

    EntityManager pop() {
        Deque<EntityManager> entityManagers = perThreadObjectContainer.get();
        EntityManager entityManager = entityManagers.pop();
        if (entityManagers.isEmpty()) {
            perThreadObjectContainer.remove();
        }
        return entityManager;
    }
}
