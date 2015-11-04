/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.seedstack.business.jpa.samples.domain.identity;

import com.google.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

/**
 * IdentityHandlerIT
 * 
 * @author redouane.loulou@ext.mpsa.com
 *
 */
@RunWith(SeedITRunner.class)
public class IdentityHandlerIT {

	@Inject
	private MyAggregateFactory myAggregateFactory;

	
	@Test
	@Transactional
	@JpaUnit("seed-biz-support")
	public void test_transactional_identityhandler(){
		MyAggregate aggregate = myAggregateFactory.createMyAggregate("test");
		Assertions.assertThat(aggregate.getEntityId()).isNotNull();
		Assertions.assertThat(aggregate.getMySubAggregate().getEntityId()).isNotNull();
		for (MyEntity entity : aggregate.getMySubAggregates()) {
			Assertions.assertThat(entity.getEntityId()).isNotNull();
		}
		MyAggregate aggregate2 = myAggregateFactory.createMyAggregate("test2");
		Assertions.assertThat(aggregate2.getEntityId()).isNotNull();
		Assertions.assertThat(aggregate2.getMySubAggregate().getEntityId()).isNotNull();
		for (MyEntity entity : aggregate2.getMySubAggregates()) {
			Assertions.assertThat(entity.getEntityId()).isNotNull();
		}
	}
}
