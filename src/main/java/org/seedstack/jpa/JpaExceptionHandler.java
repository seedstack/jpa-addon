/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa;

import javax.persistence.EntityTransaction;
import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

/**
 * Exception handler for JPA transactions.
 */
public interface JpaExceptionHandler extends ExceptionHandler<EntityTransaction> {

  @Override
  boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata,
      EntityTransaction associatedTransaction);

}
