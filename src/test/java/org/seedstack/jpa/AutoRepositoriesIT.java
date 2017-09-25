/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Factory;
import org.seedstack.business.domain.Repository;
import org.seedstack.jpa.fixtures.business.domain.tinyaggregate.TinyAggRoot;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@Transactional
@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class AutoRepositoriesIT {

  @Inject
  @Jpa
  private Repository<TinyAggRoot, String> repository;
  @Inject
  private Factory<TinyAggRoot> factory;

  @Before
  public void setUp() throws Exception {
    repository.clear();
  }

  @After
  public void tearDown() throws Exception {
    repository.clear();
  }

  @Test
  public void retrieveAggregateFromRepository() {
    repository.add(factory.create("hello"));
    TinyAggRoot tinyAggRoot = repository.get("hello").get();
    Assertions.assertThat(tinyAggRoot).isNotNull();
  }
}
