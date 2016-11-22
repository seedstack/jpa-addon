/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.SeedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityManagerLinkTest {

    EntityManagerLink entityManagerLinkUnderTest;

    @Before
    public void before() {
        entityManagerLinkUnderTest = new EntityManagerLink();
    }

    @Test
    public void pushTest() {
        EntityManager entityManager = mockEntityManager();
        entityManagerLinkUnderTest.push(entityManager);
        Assertions.assertThat(entityManagerLinkUnderTest.get()).isEqualTo(entityManager);
        Assertions.assertThat(entityManagerLinkUnderTest.getCurrentTransaction()).isNotNull();
    }

    @Test(expected = SeedException.class)
    public void getTest() {
        EntityManager entityManager = mockEntityManager();
        entityManagerLinkUnderTest.push(entityManager);
        entityManagerLinkUnderTest.pop();
        entityManagerLinkUnderTest.get();
    }

    @Test
    public void popTest() {
        EntityManager entityManager = mockEntityManager();
        entityManagerLinkUnderTest.push(entityManager);
        entityManagerLinkUnderTest.pop();
        Assertions.assertThat(entityManagerLinkUnderTest.getCurrentTransaction()).isNull();
    }


    private EntityManager mockEntityManager() {
        EntityManager entityManager = mock(EntityManager.class);
        EntityTransaction entityTransaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        return entityManager;
    }
}
