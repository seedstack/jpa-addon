/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.jpa.fixtures.business.domain.identity.entities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.seedstack.business.domain.BaseFactory;
import org.seedstack.business.domain.Create;
import org.seedstack.jpa.fixtures.business.domain.identity.BookDTO;
import org.seedstack.jpa.fixtures.business.domain.identity.LibraryFactory;

class DefaultLibraryFactory extends BaseFactory<LibraryAggregate> implements LibraryFactory {

    @Override
    @Create
    public BookEntity createBook(BookDTO book) {
        return new BookEntity(book.getName());
    }

    @Override
    @Create
    public DigitalBookEntity createDigitalBook(BookDTO book) {
        return new DigitalBookEntity(book.getName());
    }

    @Override
    @Create
    public LibraryAggregate createLibrary(String name, Collection<BookDTO> books) {
        LibraryAggregate library = new LibraryAggregate(name);
        if (books == null) {
            return library;
        }
        Map<String, List<BookDTO>> booksPerShelve = books.stream()
                .collect(Collectors.groupingBy(BookDTO::getShelving));

        for (Entry<String, List<BookDTO>> kvp : booksPerShelve.entrySet()) {
            ShelvingEntity shelving = createShelving(kvp.getKey());
            kvp.getValue().forEach(book -> shelving.addBook(this.createBook(book)));
            library.addShelving(shelving);
        }
        return library;
    }

    @Create
    public ShelvingEntity createShelving(String shelvingName) {
        return new ShelvingEntity(shelvingName);
    }
}
