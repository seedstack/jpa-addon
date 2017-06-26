/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures;

import org.seedstack.business.EventHandler;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.domain.events.AggregatePersistedEvent;
import org.seedstack.business.event.BaseEventHandler;
import org.seedstack.jpa.EventTransactionIT;
import org.seedstack.jpa.Jpa;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.samples.domain.tinyaggregate.TinyAggRoot;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class AggregateEventHandler extends BaseEventHandler<AggregatePersistedEvent> {
    @Inject
    @Jpa
    private Repository<TinyAggRoot, String> repository;

    @Override
    public void handle(AggregatePersistedEvent event) {
        checkNotNull(repository);

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
