/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.spi;

public class JpaRepositoryFactoryPriority {
    /**
     * Base priority for vendor-specific JPA repository factories.
     */
    public static final int VENDOR_SPECIFIC_PRIORITY = 100000;

    /**
     * Priority for built-in JPA 2.1 repository factory.
     */
    public static final int JPA_21_PRIORITY = 2100;

    /**
     * Priority for built-in JPA 2.0 repository factory.
     */
    public static final int JPA_20_PRIORITY = 2000;

    /**
     * Priority for built-in JPA 1.0 repository factory.
     */
    public static final int JPA_10_PRIORITY = 1000;

    /**
     * Default priority for JPA repository factories.
     */
    public static final int DEFAULT_PRIORITY = 0;
}
