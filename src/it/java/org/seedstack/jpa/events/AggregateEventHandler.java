/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.events;

import org.seedstack.business.EventHandler;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.domain.events.AggregatePersistedEvent;
import org.seedstack.jpa.EventTransactionIT;
import org.seedstack.jpa.Jpa;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.samples.domain.tinyaggregate.TinyAggRoot;
import org.seedstack.seed.core.utils.SeedCheckUtils;

import javax.inject.Inject;

/**
 * Sample of EventHandler used for test.
 */
public class AggregateEventHandler implements EventHandler<AggregatePersistedEvent> {

    static int counter;
    static int autoRepoCounter;
    @Inject
    @Jpa
    private Repository<TinyAggRoot, String> repository;

    @Override
    public void handle(AggregatePersistedEvent event) {
        // Event handler should be able to inject domain elements
        SeedCheckUtils.checkIfNotNull(repository);

        if (SampleBaseJpaAggregateRoot.class.equals(event.getAggregateRoot())) {
            if ("fail".equals(event.getContext().getArgs()[0])) {
                // when an exception is thrown the transaction should be rolled back
                throw new RuntimeException("I don't want you to persist that!");
            } else {
                EventTransactionIT.aopWorks = true; // listen persist event on user's repo
            }
        } else if (TinyAggRoot.class.equals(event.getAggregateRoot())) {
            EventTransactionIT.aopWorksOnDefaultRepo = true; // listen persist event on default repo
        }
    }
}
