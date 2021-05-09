/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
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
import org.seedstack.business.domain.IdentityService;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.domain.SortOption;
import org.seedstack.business.specification.Specification;
import org.seedstack.business.specification.dsl.SpecificationBuilder;
import org.seedstack.jpa.fixtures.business.domain.product.Picture;
import org.seedstack.jpa.fixtures.business.domain.product.Product;
import org.seedstack.jpa.fixtures.business.domain.test.VehicleType;
import org.seedstack.jpa.fixtures.business.domain.test.VehicleTypeId;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@Transactional
@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class SpecificationIT {
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private Product product6;
    private VehicleType vehicleType1 = new VehicleType(new VehicleTypeId("APFR", "code"));
    private VehicleType vehicleType2 = new VehicleType(new VehicleTypeId("ACFR", "code"));

    @Inject
    @Jpa
    private Repository<Product, Long> repository;
    @Inject
    @Jpa
    private Repository<VehicleType, VehicleTypeId> repository2;
    @Inject
    private SpecificationBuilder specificationBuilder;
    @Inject
    private IdentityService identityService;

    @Before
    public void setUp() throws Exception {
        product1 = createProduct(1L, "product1", "url1", "picture1", 2d);
        product2 = createProduct(2L, "product2", "url2", "picture2", 2d);
        product3 = createProduct(3L, "product3", "url3", "picture3", 2d);
        product4 = createProduct(4L, "product4", "url2", "   picture4", 6d);
        product5 = createProduct(5L, "product5", "url5", "picture4   ", 1d);
        product6 = createProduct(6L, "product6", "url6", "picture5", 5d);

        repository.clear();

        repository.add(product1);
        repository.add(product2);
        repository.add(product3);
        repository.add(product4);
        repository.add(product5);
        repository.add(product6);

        repository2.clear();

        repository2.add(vehicleType1);
        repository2.add(vehicleType2);
    }

    @After
    public void tearDown() throws Exception {
        repository.clear();
        repository2.clear();
    }

    @Test
    public void testTrue() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .all()
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product3, product4, product5, product6);
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
        ).containsExactlyInAnyOrder(product3);
        assertThat(repository.get(specificationBuilder.ofAggregate(Product.class)
                .identity().isNot(3L)
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product4, product5, product6);
    }

    @Test
    public void testGreaterThan() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").greaterThan(3)
                .build())
        ).containsExactlyInAnyOrder(product4, product5, product6);
    }

    @Test
    public void testGreaterThanOrEqualTo() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").greaterThanOrEqualTo(3)
                .build())
        ).containsExactlyInAnyOrder(product3, product4, product5, product6);
    }

    @Test
    public void testLessThan() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").lessThan(3)
                .build())
        ).containsExactlyInAnyOrder(product1, product2);
    }

    @Test
    public void testLessThanOrEqualTo() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("id").lessThanOrEqualTo(3)
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product3);
    }

    @Test
    public void testEquality() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("price").equalTo(2d)
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product3);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("price").equalTo(5d)
                .build())
        ).containsExactlyInAnyOrder(product6);
    }

    @Test
    public void testStringEquality() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture1")
                .build())
        ).containsExactlyInAnyOrder(product1);
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
        ).containsExactlyInAnyOrder(product4);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4").trimmingTail()
                .build())
        ).containsExactlyInAnyOrder(product5);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").equalTo("picture4").trimming()
                .build())
        ).containsExactlyInAnyOrder(product4, product5);
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
        ).containsExactlyInAnyOrder(product3);
    }

    @Test
    public void testStringMatching() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("picture?")
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product3, product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("picture*")
                .build())
        ).containsExactlyInAnyOrder(product1, product2, product3, product5, product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re5")
                .build())
        ).containsExactlyInAnyOrder(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pic*re5")
                .build())
        ).containsExactlyInAnyOrder(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("?ict?re5")
                .build())
        ).containsExactlyInAnyOrder(product6);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("*cture5")
                .build())
        ).containsExactlyInAnyOrder(product6);
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
        ).containsExactlyInAnyOrder(product4);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4").trimmingTail()
                .build())
        ).containsExactlyInAnyOrder(product5);
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").matching("pict?re4").trimming()
                .build())
        ).containsExactlyInAnyOrder(product4, product5);
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
        ).containsExactlyInAnyOrder(product3);
    }

    @Test
    public void testNot() throws Exception {
        assertThat(repository.get(specificationBuilder.of(Product.class)
                .property("pictures.url.url").not().equalTo("picture2").and()
                .property("pictures.url.url").not().equalTo("picture2/2")
                .build())
        ).containsExactlyInAnyOrder(product1, product3, product4, product5, product6);
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
        ).containsExactlyInAnyOrder(product2, product3, product4);
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
        ).containsExactlyInAnyOrder(product2);
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
        ).isSortedAccordingTo(Comparator.comparing(Product::getId).reversed())
                .hasSize(6);
    }

    @Test
    public void testNestedSort() throws Exception {
        assertThat(repository.get(Specification.any(),
                new SortOption()
                        .add("mainPicture.url", SortOption.Direction.DESCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing((Product p) -> p.getMainPicture().getUrl()).reversed())
                .hasSize(6);
    }

    @Test
    public void testNestedEmbeddedSort() throws Exception {
        assertThat(repository2.get(Specification.any(),
                new SortOption()
                        .add("id.brandCountryCode", SortOption.Direction.ASCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing((VehicleType vt) -> vt.getId().getBrandCountryCode())).hasSize(2);
    }

    @Test
    public void testMultiSort() throws Exception {
        assertThat(repository.get(Specification.any(),
                new SortOption()
                        .add("price", SortOption.Direction.ASCENDING)
                        .add("mainPicture.url", SortOption.Direction.DESCENDING)
                )
        ).isSortedAccordingTo(Comparator.comparing(Product::getPrice)
                .thenComparing(Comparator.comparing((Product product) -> product.getMainPicture().getUrl()).reversed()))
                .hasSize(6);
    }

    @Test
    public void testCompositeKey() throws Exception {
        assertThat(repository2.get(specificationBuilder.of(VehicleType.class)
                .property("id.brandCountryCode").equalTo("APFR")
                .build())
        ).contains(vehicleType1);
    }

    public Product createProduct(long id, String designation, String mainPictureUrl, String pictureUrl, double price) {
        List<Picture> pictures = new ArrayList<>();
        pictures.add(identityService.identify(new Picture(pictureUrl, id)));
        pictures.add(identityService.identify(new Picture(pictureUrl + "/2", id)));
        return new Product(id, designation, "summary", "details", mainPictureUrl, pictures, price);
    }
}
