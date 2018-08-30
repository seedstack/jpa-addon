/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.jpa.fixtures.business.domain.identity;

import java.util.Collection;

import org.seedstack.business.domain.Factory;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.BookEntity;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.DigitalBookEntity;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.LibraryAggregate;

public interface LibraryFactory extends Factory<LibraryAggregate> {
    LibraryAggregate createLibrary(String name, Collection<BookDTO> books);

    BookEntity createBook(BookDTO books);

    DigitalBookEntity createDigitalBook(BookDTO books);
}
