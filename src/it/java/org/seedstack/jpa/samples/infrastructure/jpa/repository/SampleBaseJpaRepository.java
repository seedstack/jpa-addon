/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.samples.infrastructure.jpa.repository;

import org.seedstack.jpa.BaseJpaRepository;
import org.seedstack.jpa.samples.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.samples.domain.base.SampleBaseRepository;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class SampleBaseJpaRepository extends BaseJpaRepository<SampleBaseJpaAggregateRoot, String> implements SampleBaseRepository {
}
