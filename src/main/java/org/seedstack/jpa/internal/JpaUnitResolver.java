/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.seedstack.jpa.JpaUnit;
import org.seedstack.shed.reflect.StandardAnnotationResolver;

import java.lang.reflect.Method;

class JpaUnitResolver extends StandardAnnotationResolver<Method, JpaUnit> {
    static JpaUnitResolver INSTANCE = new JpaUnitResolver();

    private JpaUnitResolver() {
        // no external instantiation allowed
    }
}
