/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.simple;

import org.seedstack.business.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class SampleSimpleJpaEntity extends BaseEntity<Long> {
    @Id
    private Long entityId;
    private String field1;
    private String field2;
    private Date field3;

    SampleSimpleJpaEntity() {
    }

    @Override
    public Long getId() {
        return entityId;
    }

    public String getField1() {
        return field1;
    }


    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public Date getField3() {
        return field3;
    }

    public void setField3(Date field3) {
        this.field3 = field3;
    }
}
