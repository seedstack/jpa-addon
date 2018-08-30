/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.business.domain.identity.BookDTO;
import org.seedstack.jpa.fixtures.business.domain.identity.LibraryFactory;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.AbstractBook;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.BookEntity;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.LibraryAggregate;
import org.seedstack.jpa.fixtures.business.domain.identity.entities.ShelvingEntity;
import org.seedstack.jpa.identity.HsqlBufferedSequenceInterceptor;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@RunWith(SeedITRunner.class)
public class IdentityHandlerIT {
    @Inject
    private LibraryFactory factory;

    @Before
    public void setup() {
        HsqlBufferedSequenceInterceptor.resetSegmentCalls();
    }

    @Test
    @Transactional
    @JpaUnit("business")
    public void testSequenceAndBufferedIdCreator() {

        List<BookDTO> books = new ArrayList<>();

        books.add(new BookDTO("Lord of the rings", "Fantasy"));
        books.add(new BookDTO("Harry Potter and Philosopher Stone", "Fantasy"));
        books.add(new BookDTO("Computer Sciense for Dummies", "Teaching"));
        books.add(new BookDTO("Effective Java", "Teaching"));

        IntStream.range(1, 36)
                .mapToObj(x -> new BookDTO(String.format("Annonymous Book Nº %d", x), "Anonymous"))
                .forEach(books::add);
        LibraryAggregate newLibrary = factory.createLibrary("Small Library", books);

        assertThat(newLibrary.getId()).isNotNull();

        for (ShelvingEntity shelving : newLibrary.getShelves()) {
            assertThat(shelving.getId()).isNotNull();
            assertThat(shelving.getBooks()).isNotEmpty();
            assertThat(shelving.getBooks().stream())
                    .allSatisfy(BookEntity::getId).isNotNull();
        }

        // First chunk alocation + 3 more allocations due to 38 books on our library divided on
        // chunks of 3
        assertThat(HsqlBufferedSequenceInterceptor.getSegmentCalls()).isEqualTo(4);

    }

    @Test
    @Transactional
    @JpaUnit("business")
    public void testBufferedMultipleSequence() throws Exception {

        List<BookDTO> books = new ArrayList<>();

        IntStream.range(1, 60)
                .mapToObj(x -> new BookDTO(
                        String.format("%s Book Nº %d", x % 2 == 0 ? "Paper" : "Digital", x),
                        "Anonymous"))
                .forEach(books::add);

        List<AbstractBook> entities = new ArrayList<>();

        // Pairs will be Books
        // Odd will be DigitalBooks
        for (int i = 0; i < books.size(); i++) {
            if (i % 2 == 0) {
                entities.add(factory.createBook(books.get(i)));
            } else {
                entities.add(factory.createDigitalBook(books.get(i)));
            }
        }

        assertThat(entities).allSatisfy(x -> x.getId()).isNotNull();
        assertThat(HsqlBufferedSequenceInterceptor.getSegmentCalls()).isEqualTo(4);

    }

}
