/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.identity;

import javax.inject.Named;

import org.seedstack.business.util.SequenceGenerator;

/**
 * Uses a H2 sequence for identity management. This handler needs the H2 sequence name to be
 * specified in class configuration as the 'identitySequenceName' property.
 */
@Named("hsqlSequence")
public class HSQLSequenceGenerator extends BaseBufferedSequenceGenerator  implements SequenceGenerator {

    static final String HSQL_SEQUENCE_QUERY = "VALUES NEXT VALUE FOR %1$s";

    public HSQLSequenceGenerator() {
        super(HSQL_SEQUENCE_QUERY);
    }

    @Override
    protected void ensureSequenceExistence(String sequenceName) {

        getEntityManager()
                .createNativeQuery(
                        String.format("CREATE SEQUENCE IF NOT EXISTS %1$s", sequenceName))
                .executeUpdate();

    }

}
