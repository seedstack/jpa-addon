/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.domain.SortOption;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.specification.dsl.SpecificationBuilder;
import org.seedstack.jpa.fixtures.business.domain.product.Product;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@Transactional
@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class SpecificationIT {

    private final Product product1 = createProduct(1L, "product1", "url1", "picture1", 2d);
    private final Product product2 = createProduct(2L, "product2", "url2", "picture2", 2d);
    private final Product product3 = createProduct(3L, "product3", "url3", "picture3", 2d);
    private final Product product4 = createProduct(4L, "product4", "url2", "   picture4", 6d);
    private final Product product5 = createProduct(5L, "product5", "url5", "picture4   ", 1d);
    private final Product product6 = createProduct(6L, "product6", "url6", "picture5", 5d);
    @Inject
    @Jpa
    private Repository<Product, Long> repository;
    @Inject
    private SpecificationBuilder specificationBuilder;

    @Before
    public void setUp() throws Exception {
        repository.clear();
        repository.add(product1);
        repository.add(product2);
        repository.add(product3);
        repository.add(product4);
        repository.add(product5);
        repository.add(product6);
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
    }

    @Test
    public void testTrue() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .all()
                .build())
        ).containsExactly(product1, product2, product3, product4, product5, product6);
    }

    @Test
    public void testFalse() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .none()
                .build())
        ).isEmpty();
    }

    @Test
    public void testIdentity() throws Exception {
        assertThat(repository.get(specificationBuilder.ofAggregate(Product.class)
                .identity().is(3L)
                .build())
        ).containsExactly(product3);
        assertThat(repository.get(specificationBuilder.ofAggregate(Product.class)
                .identity().isNot(3L)
                .build())
        ).containsExactly(product1, product2, product4, product5, product6);
    }

    @Test
    public void testGreaterThan() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").greaterThan(3)
                .build())
        ).containsExactly(product4, product5, product6);
    }

    @Test
    public void testLessThan() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").lessThan(3)
                .build())
        ).containsExactly(product1, product2);
    }

    @Test
    public void testEquality() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("price").equalTo(2d)
                .build())
        ).containsExactly(product1, product2, product3);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("price").equalTo(5d)
                .build())
        ).containsExactly(product6);
    }

    @Test
    public void testStringEquality() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture1")
                .build())
        ).containsExactly(product1);
    }

    @Test
    public void testStringEqualityWithTrim() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4")
                .build())
        ).isEmpty();
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4").trimmingLead()
                .build())
        ).containsExactly(product4);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4").trimmingTail()
                .build())
        ).containsExactly(product5);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4").trimming()
                .build())
        ).containsExactly(product4, product5);
    }

    @Test
    public void testStringEqualityIgnoringCase() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("PICTurE3")
                .build())
        ).isEmpty();
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("PICTurE3").ignoringCase()
                .build())
        ).containsExactly(product3);
    }

    @Test
    public void testStringMatching() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("picture?")
                .build())
        ).containsExactly(product1, product2, product3, product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("picture*")
                .build())
        ).containsExactly(product1, product2, product3, product5, product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re5")
                .build())
        ).containsExactly(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pic*re5")
                .build())
        ).containsExactly(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("?ict?re5")
                .build())
        ).containsExactly(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("*cture5")
                .build())
        ).containsExactly(product6);
    }

    @Test
    public void testStringMatchingWithTrim() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4")
                .build())
        ).isEmpty();
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4").trimmingLead()
                .build())
        ).containsExactly(product4);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4").trimmingTail()
                .build())
        ).containsExactly(product5);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4").trimming()
                .build())
        ).containsExactly(product4, product5);
    }

    @Test
    public void testStringMatchingIgnoringCase() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("PI*urE3")
                .build())
        ).isEmpty();
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("PI*urE3").ignoringCase()
                .build())
        ).containsExactly(product3);
    }

    @Test
    public void testNot() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").not().equalTo("picture2")
                .build())
        ).containsExactly(product1, product3, product4, product5, product6);
    }

    @Test
    public void testOr() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture2")
                .or()
                .property("designation").equalTo("product3")
                .or()
                .property("designation").equalTo("product4")
                .build())
        ).containsExactly(product2, product3, product4);
    }

    @Test
    public void testAnd() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture2")
                .and()
                .property("designation").equalTo("product2")
                .and()
                .property("price").equalTo(2d)
                .build())
        ).containsExactly(product2);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture3")
                .and()
                .property("designation").equalTo("product2")
                .build())
        ).isEmpty();
    }

    @Test
    public void testSort() throws Exception {
        assertThat(repository.get(Specification.any(),
                new SortOption()
                        .add("id", SortOption.Direction.DESCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing(Product::getId).reversed());
    }

    @Test
    public void testNestedSort() throws Exception {
        assertThat(repository.get(Specification.any(),
                new SortOption()
                        .add("mainPicture.url", SortOption.Direction.DESCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing((Product p) -> p.getMainPicture().getUrl()).reversed());
    }

    @Test
    public void testMultiSort() throws Exception {
        assertThat(repository.get(Specification.any(),
                new SortOption()
                        .add("price", SortOption.Direction.ASCENDING)
                        .add("mainPicture.url", SortOption.Direction.DESCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing(Product::getPrice)
                .thenComparing(
                        Comparator.comparing((Product product) -> product.getMainPicture().getUrl()).reversed()));
    }

    public Product createProduct(long id, String designation, String mainPictureUrl, String pictureUrl, double price) {
        List<String> pictures = new ArrayList<>();
        pictures.add(pictureUrl);
        return new Product(id, designation, "summary", "details", mainPictureUrl, pictures, price);
    }
}
