/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.fixtures.business.domain.tinyaggregate;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.seedstack.business.domain.BaseAggregateRoot;

@Entity
public class TinyAggRoot extends BaseAggregateRoot<String> {

    @Id
    private String id;
    private String name;

    public TinyAggRoot() {
    }

    TinyAggRoot(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
