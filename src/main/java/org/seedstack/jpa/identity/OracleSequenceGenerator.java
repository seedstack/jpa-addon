/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.identity;

import javax.inject.Named;

import org.seedstack.business.util.SequenceGenerator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Uses an Oracle sequence for identity management. This handler needs the Oracle sequence name to
 * be specified in class configuration as the 'identitySequenceName' property.
 */
@Named("oracleSequence")
public class OracleSequenceGenerator extends BaseBufferedSequenceGenerator
        implements SequenceGenerator {

    private static final String ORACLE_SEQUENCE_QUERY = "SELECT %1$s.NEXTVAL FROM DUAL";

    public OracleSequenceGenerator() {
        super(ORACLE_SEQUENCE_QUERY);
    }

    @Override
    protected void ensureSequenceExistence(String sequenceName) {
        // Not needed
    }
}
