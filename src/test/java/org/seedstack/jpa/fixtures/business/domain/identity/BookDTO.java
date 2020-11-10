/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.seedstack.jpa.fixtures.business.domain.identity;

public class BookDTO {

    private String name;

    private String shelve;

    public BookDTO(String name, String shelve) {
        super();
        this.name = name;
        this.shelve = shelve;
    }

    public String getName() {
        return name;
    }

    public String getShelving() {
        return shelve;
    }

}
