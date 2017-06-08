/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseRepository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class BaseJpaRepositoryIT {
    @Inject
    private SampleBaseRepository sampleBaseRepository;
    @Inject
    private SampleBaseJpaFactory sampleBaseJpaFactory;

    @Before
    public void setUp() throws Exception {
        sampleBaseRepository.clear();
    }

    @After
    public void tearDown() throws Exception {
        sampleBaseRepository.clear();
    }

    @Test
    public void addAggregate() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test"));
        assertThat(sampleBaseRepository.get("test")).isNotEmpty();
    }

    @Test(expected = AggregateExistsException.class)
    public void addDuplicateAggregate() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test"));
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test"));
    }

    @Test
    public void getAggregate() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test"));
        assertThat(sampleBaseRepository.get("test")).isNotEmpty();
        assertThat(sampleBaseRepository.get("unknownTest")).isEmpty();
    }

    @Test
    public void updateAggregate() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        sampleBaseRepository.add(test);
        assertThat(sampleBaseRepository.get("test").get().getField1()).isNull();
        test.setField1("modified");
        sampleBaseRepository.update(test);
        assertThat(sampleBaseRepository.get("test").get().getField1()).isEqualTo("modified");
    }

    @Test(expected = AggregateNotFoundException.class)
    public void updateUnknownAggregate() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        test.setField1("modified");
        sampleBaseRepository.update(test);
    }

    @Test
    public void removeAggregate() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        sampleBaseRepository.add(test);
        assertThat(sampleBaseRepository.get("test")).isNotEmpty();
        sampleBaseRepository.remove(test);
        assertThat(sampleBaseRepository.get("test")).isEmpty();
    }

    @Test
    public void removeById() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        sampleBaseRepository.add(test);
        assertThat(sampleBaseRepository.get("test")).isNotEmpty();
        sampleBaseRepository.remove("test");
        assertThat(sampleBaseRepository.get("test")).isEmpty();
    }

    @Test
    public void containsAggregate() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        SampleBaseJpaAggregateRoot unknownTest = sampleBaseJpaFactory.create("unknownTest");

        sampleBaseRepository.add(test);
        assertThat(sampleBaseRepository.contains(test)).isTrue();
        assertThat(sampleBaseRepository.contains(unknownTest)).isFalse();
    }

    @Test
    public void containsId() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        sampleBaseRepository.add(test);
        assertThat(sampleBaseRepository.contains("test")).isTrue();
        assertThat(sampleBaseRepository.contains("unknownTest")).isFalse();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void clear() {
        prepareClear();
        doClear();
        checkClearResult();
    }

    void prepareClear() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test4"));
        assertThat(sampleBaseRepository.get("test4")).isNotEmpty();
    }

    void doClear() {
        sampleBaseRepository.clear();
    }

    void checkClearResult() {
        assertThat(sampleBaseRepository.get("test4")).isEmpty();
        assertThat(sampleBaseRepository.isEmpty()).isTrue();
    }
}
