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
import org.seedstack.business.domain.specification.builder.SpecificationBuilder;
import org.seedstack.jpa.fixtures.product.Product;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional
@JpaUnit("seed-biz-support")
@RunWith(SeedITRunner.class)
public class SpecificationIT {
    @Inject
    @Jpa
    private Repository<Product, Long> repository;

    @Inject
    private SpecificationBuilder specificationBuilder;

    @Before
    public void setUp() throws Exception {
        repository.clear();
        repository.add(createProduct());
    }

    @Test
    public void retrieveAggregatesBySpecification() throws Exception {
        System.out.println(repository.get(
                specificationBuilder.of(Product.class)
                        .property("pictures.name").equalTo("picture1")
                        .or()
                        .property("pictures.name").equalTo("picture2")
                        .build())
                .collect(toList()));
    }

    public Product createProduct() {
        List<String> pictures = new ArrayList<>();
        pictures.add("picture1");
        pictures.add("picture2");
        return new Product(1L, "designation", "summary", "details", pictures, 2d);
    }
}
