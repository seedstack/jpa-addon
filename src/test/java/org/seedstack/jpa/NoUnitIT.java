/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.simple.Item1;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.testing.ConfigurationProfiles;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
@ConfigurationProfiles("noUnit")
public class NoUnitIT {
    @Inject
    private EntityManager entityManager;

    @Test
    public void entityManagerIsNotNull() {
        assertThat(entityManager).isNotNull();
    }

    @Test(expected = SeedException.class)
    public void usingEntityManagerThrowException() {
        entityManager.find(Item1.class, 1);
    }
}
