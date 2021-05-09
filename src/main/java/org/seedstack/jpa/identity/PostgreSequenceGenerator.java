/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.identity;

import javax.inject.Named;

/**
 * Uses a PostgreSQL sequence for identity management. This handler needs the PostgreSQL sequence
 * name to be specified in class configuration as the 'identitySequenceName' property.
 */
@Named("postgreSqlSequence")
public class PostgreSequenceGenerator extends BaseBufferedSequenceGenerator {

    private static final String POSTGRE_SEQUENCE_QUERY = "SELECT nextval('%1$s')";

    public PostgreSequenceGenerator() {
        super(POSTGRE_SEQUENCE_QUERY);
    }

    @Override
    protected void ensureSequenceExistence(String sequenceName) {
        // Not Needed
    }

}
