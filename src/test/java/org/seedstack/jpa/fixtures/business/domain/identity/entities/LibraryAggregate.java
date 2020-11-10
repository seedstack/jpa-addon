/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.identity.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.business.domain.Identity;
import org.seedstack.business.util.UuidGenerator;

import com.google.common.base.MoreObjects;

public class LibraryAggregate extends BaseAggregateRoot<UUID> {

    @Identity(generator = UuidGenerator.class)
    private UUID id;
    private String name;

    private Set<ShelvingEntity> shelves;

    public LibraryAggregate(String name) {
        this.name = name;
    }

    public void addShelving(ShelvingEntity shelving) {
        if (shelves == null) {
            shelves = new HashSet<>();
        }
        this.shelves.add(shelving);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ShelvingEntity> getShelves() {
        if (shelves == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(shelves);

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("name", name)
                .add("shelves", shelves).toString();
    }

}
