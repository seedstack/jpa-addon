/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.inject.Named;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.simple.Item1;
import org.seedstack.jpa.fixtures.simple.Item1Repository;
import org.seedstack.jpa.fixtures.simple.Item2;
import org.seedstack.jpa.fixtures.simple.Item2Repository;
import org.seedstack.jpa.fixtures.simple.Item3Repository;
import org.seedstack.jpa.fixtures.simple.Unit3ExceptionHandler;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

@Transactional
@RunWith(SeedITRunner.class)
public class JpaIT {
    @Inject
    private Item1Repository item1Repository;
    @Inject
    private Item2Repository item2Repository;
    @Inject
    private Item3Repository item3Repository;
    @Inject
    @Named("unit3")
    private JpaExceptionHandler jpaExceptionHandler;

    @Test
    @JpaUnit("unit1")
    public void basicJpa() throws Exception {
        Item1 item1 = new Item1();
        item1.setId(1L);
        item1.setName("item1Name");

        item1Repository.save(item1);

        Assertions.assertThat(item1.getId()).isEqualTo(1L);

        Item1 item2 = new Item1();
        item2.setId(2L);
        item2.setName("item1Name");
        item1Repository.save(item2);
        Assertions.assertThat(item2.getId()).isEqualTo(2L);
    }

    @Test(expected = IllegalArgumentException.class)
    @Transactional
    @JpaUnit("unit1")
    public void accessToWrongUnit() throws Exception {
        Item2 item2 = new Item2();
        item2.setId(10L);
        item2.setName("item2Name");
        item2Repository.save(item2);
        fail("should have failed");
    }

    @Test
    @Transactional
    @JpaUnit("unit1")
    public void accessToOtherUnitWithNestedTransaction() throws Exception {
        Item1 item1 = new Item1();
        item1.setId(20L);
        item1.setName("item1Name");
        item1Repository.save(item1);
        assertThat(item1.getId()).isEqualTo(20L);

        accessToUnit2WithNewTransaction();

        accessToUnit1WithCurrentTransaction();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit("unit2")
    protected void accessToUnit2WithNewTransaction() {
        Item2 item2 = new Item2();
        item2.setId(30L);
        item2.setName("item2Name");
        item2Repository.save(item2);
        assertThat(item2.getId()).isEqualTo(30L);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @JpaUnit("unit1")
    protected void accessToUnit1WithCurrentTransaction() {
        Item1 item1 = new Item1();
        item1.setId(40L);
        item1.setName("item1Name");
        item1Repository.save(item1);
        assertThat(item1.getId()).isEqualTo(40L);
    }

    @Test
    @Transactional
    @JpaUnit("unit1")
    public void nestedRollback() throws Exception {
        outerSuccessfulTransaction();
        assertThat(item1Repository.load(50L).getName()).isEqualTo("OuterItem");
        assertThat(item1Repository.load(60L)).isNull();
    }

    @Transactional
    @JpaUnit("unit1")
    protected void outerSuccessfulTransaction() {
        Item1 item1 = new Item1();
        item1.setId(50L);
        item1.setName("OuterItem");
        item1Repository.save(item1);

        try {
            innerFailingTransaction();
        } catch (Exception e) {
            // ignore
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit("unit1")
    protected void innerFailingTransaction() {
        Item1 item1 = new Item1();
        item1.setId(60L);
        item1.setName("InnerItem");
        item1Repository.save(item1);
        throw new IllegalStateException("Some exception");
    }

    @Test
    @JpaUnit
    public void defaultUnit() throws Exception {
        Item1 item1 = new Item1();
        item1.setId(70L);
        item1.setName("defaultItem");
        item1Repository.save(item1);
        Assertions.assertThat(item1.getId()).isEqualTo(70L);
    }

    @Test
    @Transactional
    @JpaUnit("unit3")
    public void errorHandler() {
        Assertions.assertThat(((Unit3ExceptionHandler) jpaExceptionHandler).hasHandled()).isFalse();
        item3Repository.saveWithException();
        assertThat(((Unit3ExceptionHandler) jpaExceptionHandler).hasHandled()).isTrue();
    }
}
