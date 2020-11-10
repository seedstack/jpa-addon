/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.identity.entities;

import javax.persistence.Id;

import org.seedstack.business.domain.Identity;
import org.seedstack.business.util.SequenceGenerator;

public class DigitalBookEntity extends AbstractBook {

    @Id
    @Identity(generator = SequenceGenerator.class)
    private Long id;

    private String name;

    DigitalBookEntity(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
