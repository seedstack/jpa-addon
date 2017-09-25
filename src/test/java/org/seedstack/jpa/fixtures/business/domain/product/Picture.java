/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.jpa.fixtures.business.domain.product;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.seedstack.business.domain.BaseEntity;

@Entity
public class Picture extends BaseEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long entityId;
  private String name;
  private Long productId;

  public Picture(String name, Long productId) {
    super();
    this.name = name;
    this.productId = productId;
  }


  public Picture() {

  }

  @Override
  public Long getId() {
    return entityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
