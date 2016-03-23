/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.samples.domain.tinyaggregate;

import org.seedstack.business.assembler.MatchingEntityId;
import org.seedstack.business.assembler.MatchingFactoryParameter;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class TinyDto {

    public String id;

    public String name;

    public TinyDto() {
    }

    public TinyDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @MatchingEntityId
    @MatchingFactoryParameter
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
