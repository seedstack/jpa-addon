/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.product;

import org.seedstack.business.domain.BaseAggregateRoot;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product extends BaseAggregateRoot<Long> {
    @Id
    private Long entityId;
    private String designation;
    private String summary;
    private String details;
    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL)
    private List<Picture> pictures;
    private Double price;

    public Product() {

    }

    public Product(long productId, String designation, String summary, String details, List<String> pictures, Double price) {
        entityId = productId;
        setDesignation(designation);
        setSummary(summary);
        setDetails(details);
        List<Picture> pics = null;
        if (pictures != null && !pictures.isEmpty()) {
            pics = new ArrayList<>();
            for (String picture : pictures) {
                pics.add(new Picture(picture, productId));
            }
        }
        setPictures(pics);
        setPrice(price);
    }

    @Override
    public Long getEntityId() {
        return entityId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return "Product{" +
                "entityId=" + entityId +
                ", designation='" + designation + '\'' +
                ", summary='" + summary + '\'' +
                ", details='" + details + '\'' +
                ", pictures=" + pictures +
                ", price=" + price +
                '}';
    }
}
