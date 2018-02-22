/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.simple.Item4;
import org.seedstack.jpa.fixtures.simple.Item4Repository;
import org.seedstack.jpa.fixtures.simple.OtherItem4;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@Transactional
@JpaUnit("unit4")
@RunWith(SeedITRunner.class)
public class ExternalMappingIT {

    @Inject
    private Item4Repository itemRepository;

    @Test
    public void testRepository() {
        Item4 item = new Item4();
        item.setId(10L);
        item.setName("itemName");
        itemRepository.save(item);
        assertThat(item.getId()).isEqualTo(10L);
    }

    @Test
    public void testOtherItem() {
        OtherItem4 item = new OtherItem4();
        item.setName("name");
        itemRepository.save(item);
    }
}
