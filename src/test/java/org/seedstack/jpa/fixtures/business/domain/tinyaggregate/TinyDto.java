/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.tinyaggregate;

import org.seedstack.business.assembler.AggregateId;
import org.seedstack.business.assembler.FactoryArgument;

public class TinyDto {

    public String id;
    public String name;

    public TinyDto() {
    }

    public TinyDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @AggregateId
    @FactoryArgument
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
