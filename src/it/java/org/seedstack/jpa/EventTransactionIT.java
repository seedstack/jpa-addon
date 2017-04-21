/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Factory;
import org.seedstack.business.domain.Repository;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseRepository;
import org.seedstack.jpa.fixtures.samples.domain.tinyaggregate.TinyAggRoot;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.UUID;

/**
 * This class tests that events are fired when repository methods are called.
 */
@RunWith(SeedITRunner.class)
@JpaUnit("seed-biz-support")
public class EventTransactionIT {
    private static final String ID = "id";
    private static final String FAIL = "fail";
    public static boolean aopWorks = false;
    public static boolean aopWorksOnDefaultRepo = false;
    @Inject
    private SampleBaseRepository sampleBaseRepository;
    @Inject
    private SampleBaseJpaFactory sampleBaseJpaFactory;
    @Inject
    @Jpa
    private Repository<TinyAggRoot, String> tinyRepo;
    @Inject
    private Factory<TinyAggRoot> factory;

    @Test
    @Transactional
    public void aop_on_repo_should_works() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create(UUID.randomUUID().toString()));
        Assertions.assertThat(aopWorks).isTrue();
    }

    @Test
    @Transactional
    public void aop_on_generic_repo_should_work() {
        tinyRepo.add(factory.create(UUID.randomUUID().toString()));
        Assertions.assertThat(aopWorksOnDefaultRepo).isTrue();
    }

    @Test
    public void event_handler_should_rollback_the_repo_transaction() {
        try {
            // EventHandler throw an exception
            persist_failed();
        } catch (Exception e) { /* do nothing */ }
        // the repository transaction have been rolled back
        check_data_was_not_inserted();

        // Event handler succeed
        persist_succeeded();
        // the transaction is committed
        check_data_was_inserted();
    }

    @Transactional
    public void check_data_was_inserted() {
        Assertions.assertThat(sampleBaseRepository.get(ID)).isNotEmpty();
    }

    @Transactional
    public void check_data_was_not_inserted() {
        Assertions.assertThat(sampleBaseRepository.get(FAIL)).isEmpty();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist_failed() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create(FAIL));
        throw new RuntimeException();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist_succeeded() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create(ID));
    }

}
