/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.test;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.seedstack.business.domain.BaseAggregateRoot;

@Entity
public class VehicleType extends BaseAggregateRoot<VehicleTypeId> {

    /**
     * The id.
     */
    @EmbeddedId
    private VehicleTypeId id;

    /**
     * Instantiates a new vehicle type.
     */
    private VehicleType() {
        // For JPA
    }

    public VehicleType(VehicleTypeId id) {
        this.id = id;
    }
}