/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.embed;

import org.seedstack.business.domain.BaseValueObject;

import javax.persistence.Embeddable;

@Embeddable
class SampleEmbeddedKey extends BaseValueObject {
    private Long keyValue;

    SampleEmbeddedKey() {
    }

    public SampleEmbeddedKey(Long keyValue) {
        this.keyValue = keyValue;
    }

    public Long getKeyValue() {
        return keyValue;
    }
}
