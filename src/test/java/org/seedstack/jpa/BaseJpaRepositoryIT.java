/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.seedstack.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.AggregateExistsException;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.SortOption;
import org.seedstack.business.specification.Specification;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseRepository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

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
    public void addOrUpdate() {
        SampleBaseJpaAggregateRoot test = sampleBaseJpaFactory.create("test");
        test.setField1("before");
        sampleBaseRepository.addOrUpdate(test);
        assertThat(sampleBaseRepository.contains("test")).isTrue();
        assertThat(sampleBaseRepository.get("test").get().getField1()).isEqualTo("before");
        SampleBaseJpaAggregateRoot test2 = sampleBaseJpaFactory.create("test");
        test2.setField1("after");
        sampleBaseRepository.addOrUpdate(test2);
        assertThat(sampleBaseRepository.contains("test")).isTrue();
        assertThat(sampleBaseRepository.get("test").get().getField1()).isEqualTo("after");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void clear() {
        prepareClear();
        doClear();
        checkClearResult();
    }

    @Test
    public void sortOption() {
        SampleBaseJpaAggregateRoot test4 = sampleBaseJpaFactory.create("test4");
        test4.setField1("a");
        sampleBaseRepository.add(test4);
        SampleBaseJpaAggregateRoot test2 = sampleBaseJpaFactory.create("test2");
        test2.setField1("b");
        sampleBaseRepository.add(test2);
        SampleBaseJpaAggregateRoot test9 = sampleBaseJpaFactory.create("test9");
        test9.setField1("a");
        sampleBaseRepository.add(test9);
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test1"));
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test6"));
        List<String> ascending = sampleBaseRepository
                .get(Specification.any(), new SortOption().add("id"))
                .map(SampleBaseJpaAggregateRoot::getId)
                .collect(Collectors.toList());
        assertThat(ascending).containsExactly("test1", "test2", "test4", "test6", "test9");

        List<String> descending = sampleBaseRepository
                .get(Specification.any(), new SortOption(SortOption.Direction.DESCENDING).add("id"))
                .map(SampleBaseJpaAggregateRoot::getId)
                .collect(Collectors.toList());
        assertThat(descending).containsExactly("test9", "test6", "test4", "test2", "test1");
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
