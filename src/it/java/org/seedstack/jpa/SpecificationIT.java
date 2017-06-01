/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.specification.builder.SpecificationBuilder;
import org.seedstack.jpa.fixtures.business.domain.product.Product;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.seedstack.business.specification.Specification.any;

@Transactional
@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class SpecificationIT {
    @Inject
    @Jpa
    private Repository<Product, Long> repository;
    @Inject
    private SpecificationBuilder specificationBuilder;
    private final Product product1 = createProduct(1L, "product1", "picture1");
    private final Product product2 = createProduct(2L, "product2", "picture2");
    private final Product product3 = createProduct(3L, "product3", "picture3");
    private final Product product4 = createProduct(4L, "product4", "   picture4");
    private final Product product5 = createProduct(5L, "product5", "picture4   ");

    @Before
    public void setUp() throws Exception {
        repository.remove(any());
        repository.add(product1);
        repository.add(product2);
        repository.add(product3);
        repository.add(product4);
        repository.add(product5);
    }

    @Test
    public void testStringEquality() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.name").equalTo("picture1")
                .build())
        ).containsExactly(product1);
    }

    @Test
    public void testStringEqualityWithTrim() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.name").equalTo("picture4")
                .build())
        ).isEmpty();
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.name").equalTo("picture4").leftTrimmed()
                .build())
        ).containsExactly(product4);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.name").equalTo("picture4").rightTrimmed()
                .build())
        ).containsExactly(product5);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.name").equalTo("picture4").trimmed()
                .build())
        ).containsExactly(product4, product5);
    }

    public Product createProduct(long id, String designation, String pictureUrl) {
        List<String> pictures = new ArrayList<>();
        pictures.add(pictureUrl);
        return new Product(id, designation, "summary", "details", pictures, 2d);
    }
}
