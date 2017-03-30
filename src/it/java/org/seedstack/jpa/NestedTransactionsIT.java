/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.sample.Item1;
import org.seedstack.jpa.fixtures.sample.Item1Repository;
import org.seedstack.jpa.fixtures.sample.Item2;
import org.seedstack.jpa.fixtures.sample.Item2Repository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SeedITRunner.class)
public class NestedTransactionsIT {
    @Inject
    private Item1Repository item1Repository;
    @Inject
    private Item2Repository item2Repository;

    @Test(expected = IllegalArgumentException.class)
    @Transactional
    @JpaUnit("unit1")
    public void accessToWrongUnit() throws Exception {
        Item2 item2 = new Item2();
        item2.setID(10L);
        item2.setName("item2Name");
        item2Repository.save(item2);
        fail("should have failed");
    }

    @Test
    @Transactional
    @JpaUnit("unit1")
    public void accessToOtherUnitWithNestedTransaction() throws Exception {
        Item1 item1 = new Item1();
        item1.setID(20L);
        item1.setName("item1Name");
        item1Repository.save(item1);
        assertThat(item1.getID()).isEqualTo(20L);

        access_to_unit2_with_new_transaction();

        access_to_unit1_with_current_transaction();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit("unit2")
    protected void access_to_unit2_with_new_transaction() {
        Item2 item2 = new Item2();
        item2.setID(30L);
        item2.setName("item2Name");
        item2Repository.save(item2);
        assertThat(item2.getID()).isEqualTo(30L);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @JpaUnit("unit1")
    protected void access_to_unit1_with_current_transaction() {
        Item1 item1 = new Item1();
        item1.setID(40L);
        item1.setName("item1Name");
        item1Repository.save(item1);
        assertThat(item1.getID()).isEqualTo(40L);
    }

    @Test
    @Transactional
    @JpaUnit("unit1")
    public void nestedRollback() throws Exception {
        outer_successful_transaction();
        assertThat(item1Repository.load(50L).getName()).isEqualTo("OuterItem");
        assertThat(item1Repository.load(60L)).isNull();
    }

    @Transactional
    @JpaUnit("unit1")
    protected void outer_successful_transaction() {
        Item1 item1 = new Item1();
        item1.setID(50L);
        item1.setName("OuterItem");
        item1Repository.save(item1);

        try {
            inner_failing_transaction();
        } catch(Exception e) {
            // ignore
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit("unit1")
    protected void inner_failing_transaction() {
        Item1 item1 = new Item1();
        item1.setID(60L);
        item1.setName("InnerItem");
        item1Repository.save(item1);
        throw new IllegalStateException("Some exception");
    }
}
