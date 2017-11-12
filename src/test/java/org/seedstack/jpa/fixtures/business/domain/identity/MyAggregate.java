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

package org.seedstack.jpa.fixtures.business.domain.identity;

import java.util.Set;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.business.domain.Identity;
import org.seedstack.business.util.SequenceGenerator;

public class MyAggregate extends BaseAggregateRoot<Long> {

    @Identity(generator = SequenceGenerator.class)
    private Long id;

    private String name;

    private MyEntity mySubAggregate;

    private Set<MyEntity> mySubAggregates;

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Getter name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter name
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter mySubAggregate
     *
     * @return the mySubAggregate
     */
    public MyEntity getMySubAggregate() {
        return mySubAggregate;
    }

    /**
     * Setter mySubAggregate
     *
     * @param mySubAggregate the mySubAggregate to set
     */
    public void setMySubAggregate(MyEntity mySubAggregate) {
        this.mySubAggregate = mySubAggregate;
    }

    /**
     * Getter mySubAggregates
     *
     * @return the mySubAggregates
     */
    public Set<MyEntity> getMySubAggregates() {
        return mySubAggregates;
    }

    /**
     * Setter mySubAggregates
     *
     * @param mySubAggregates the mySubAggregates to set
     */
    public void setMySubAggregates(Set<MyEntity> mySubAggregates) {
        this.mySubAggregates = mySubAggregates;
    }
}
