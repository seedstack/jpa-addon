/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.jpa.fixtures.business.domain.identity.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.seedstack.business.domain.Identity;
import org.seedstack.business.util.SequenceGenerator;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "Books")
public class BookEntity extends AbstractBook {

    @Id
    @Identity(generator = SequenceGenerator.class)
    private Long id;

    private String name;

    BookEntity(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("name", name).toString();
    }

}
