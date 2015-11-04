/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.persistence.jpa.sample;


import org.seedstack.seed.it.ITBind;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@ITBind
public class Item3Repository {
    public Item3Repository() {
    }

    public void saveWithException() {
        throw new IllegalStateException("");
    }
}
