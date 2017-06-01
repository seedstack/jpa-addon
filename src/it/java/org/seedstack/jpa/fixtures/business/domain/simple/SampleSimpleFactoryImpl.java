/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.simple;

import org.seedstack.business.domain.BaseFactory;

import java.util.Date;

public class SampleSimpleFactoryImpl extends BaseFactory<SampleSimpleJpaAggregateRoot> implements SampleSimpleFactory {

    @Override
    public SampleSimpleJpaAggregateRoot createSampleSimpleJpaAggregateRoot(Integer id, String f1, String f2, Date d3, String f4) {
        SampleSimpleJpaAggregateRoot sampleSimpleJpaAggregateRoot = new SampleSimpleJpaAggregateRoot();
        sampleSimpleJpaAggregateRoot.setEntityId(id);
        sampleSimpleJpaAggregateRoot.setField1(f1);
        sampleSimpleJpaAggregateRoot.setField2(f2);
        sampleSimpleJpaAggregateRoot.setField3(d3);
        sampleSimpleJpaAggregateRoot.setField4(f4);
        return sampleSimpleJpaAggregateRoot;
    }

}
