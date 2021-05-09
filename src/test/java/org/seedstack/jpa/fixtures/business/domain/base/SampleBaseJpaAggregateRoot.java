/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.base;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.seed.Logging;
import org.slf4j.Logger;

@Entity
public class SampleBaseJpaAggregateRoot extends BaseAggregateRoot<String> {

    @Transient
    @Logging
    public Logger logger;
    @Id
    private String id;
    private String field1;
    private String field2;
    private Date field3;

    SampleBaseJpaAggregateRoot() {
        // required for JPA
    }

    SampleBaseJpaAggregateRoot(String id) {
        this.id = id;
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
