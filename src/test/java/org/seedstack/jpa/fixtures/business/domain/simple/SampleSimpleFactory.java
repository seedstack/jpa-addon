/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.simple;

import java.util.Date;
import org.seedstack.business.domain.Factory;

public interface SampleSimpleFactory extends Factory<SampleSimpleJpaAggregateRoot> {

    SampleSimpleJpaAggregateRoot createSampleSimpleJpaAggregateRoot(Integer id, String f1, String f2,
            Date d3, String f4);
}
