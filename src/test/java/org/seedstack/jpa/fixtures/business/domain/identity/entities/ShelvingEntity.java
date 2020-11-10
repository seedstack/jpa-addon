/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.jpa.fixtures.business.domain.identity.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.business.domain.Identity;
import org.seedstack.business.util.SequenceGenerator;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "Shelve")
public class ShelvingEntity extends BaseAggregateRoot<Long> {

    @Id
    @Identity(generator = SequenceGenerator.class)
    @Named("hsqlSequence")
    private Long id;
    private String name;

    @OneToMany(targetEntity = BookEntity.class)
    private Set<BookEntity> books;

    ShelvingEntity(String name) {
        this.name = name;
    }

    public void addBook(BookEntity book) {
        if (this.books == null) {
            this.books = new HashSet<>();
        }
        this.books.add(book);
    }

    public Set<BookEntity> getBooks() {
        if (books == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(books);
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("books", books)
                .toString();
    }

}
