/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.jpa;

import com.google.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.business.domain.identity.MyAggregate;
import org.seedstack.jpa.fixtures.business.domain.identity.MyAggregateFactory;
import org.seedstack.jpa.fixtures.business.domain.identity.MyEntity;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@RunWith(SeedITRunner.class)
public class IdentityHandlerIT {

  @Inject
  private MyAggregateFactory myAggregateFactory;

  @Test
  @Transactional
  @JpaUnit("business")
  public void test_transactional_identityhandler() {
    MyAggregate aggregate = myAggregateFactory.createMyAggregate("test");
    Assertions.assertThat(aggregate.getId()).isNotNull();
    Assertions.assertThat(aggregate.getMySubAggregate().getId()).isNotNull();
    for (MyEntity entity : aggregate.getMySubAggregates()) {
      Assertions.assertThat(entity.getId()).isNotNull();
    }
    MyAggregate aggregate2 = myAggregateFactory.createMyAggregate("test2");
    Assertions.assertThat(aggregate2.getId()).isNotNull();
    Assertions.assertThat(aggregate2.getMySubAggregate().getId()).isNotNull();
    for (MyEntity entity : aggregate2.getMySubAggregates()) {
      Assertions.assertThat(entity.getId()).isNotNull();
    }
  }
}
