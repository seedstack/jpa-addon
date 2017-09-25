/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.jpa.internal;

import org.seedstack.shed.exception.ErrorCode;

public enum JpaErrorCode implements ErrorCode {
  ACCESSING_ENTITY_MANAGER_OUTSIDE_TRANSACTION,
  DATA_SOURCE_NOT_FOUND,
  MISSING_ENTITY_MANAGER,
  NO_JPA_UNIT_SPECIFIED_FOR_TRANSACTION,
  NO_PERSISTED_CLASSES_IN_UNIT,
  NO_SEQUENCE_NAME_FOUND_FOR_ENTITY,
  UNABLE_TO_CREATE_JPA_JOIN_FOR_SPECIFICATION,
  UNABLE_TO_FIND_A_SUITABLE_JPA_REPOSITORY_IMPLEMENTATION
}