/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.sample;

import org.seedstack.seed.Logging;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.slf4j.Logger;

import javax.persistence.EntityTransaction;

public class Unit3ExceptionHandler implements JpaExceptionHandler {
    private boolean handled = false;

    @Logging
    Logger logger;

    @Override
    public boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata, EntityTransaction associatedTransaction) {
        handled = true;

        logger.debug("inside exception handler");
        return true;
    }

    public boolean hasHandled() {
        return handled;
    }

}