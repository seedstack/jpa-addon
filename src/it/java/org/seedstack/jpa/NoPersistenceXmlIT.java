/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 18 mars 2015
 */
package org.seedstack.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.sample2.Item;
import org.seedstack.jpa.fixtures.sample2.ItemRepository;
import org.seedstack.jpa.fixtures.sample2.OtherItem;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@JpaUnit("unit4")
@RunWith(SeedITRunner.class)
public class NoPersistenceXmlIT {

    @Inject
    private ItemRepository itemRepository;

    @Test
    public void testRepository() {
        Item item = new Item();
        item.setID(10L);
        item.setName("itemName");
        itemRepository.save(item);
        assertThat(item.getID()).isEqualTo(10L);
    }

    @Test
    public void testOtherItem() {
        OtherItem item = new OtherItem();
        item.setName("name");
        itemRepository.save(item);
    }

}
