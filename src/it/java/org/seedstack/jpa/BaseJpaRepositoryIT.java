/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseRepository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@JpaUnit("seed-biz-support")
@RunWith(SeedITRunner.class)
public class BaseJpaRepositoryIT {
    @Inject
    SampleBaseRepository sampleBaseRepository;
    @Inject
    SampleBaseJpaFactory sampleBaseJpaFactory;

    @Test
    @Transactional
    public void persist() {
        sampleBaseRepository.persist(sampleBaseJpaFactory.create("test1"));
        assertThat(sampleBaseRepository.load("test1")).isNotNull();
    }

    @Test
    @Transactional
    public void load() {
        sampleBaseRepository.persist(sampleBaseJpaFactory.create("test2"));
        assertThat(sampleBaseRepository.load("test2")).isNotNull();
    }

    @Test
    @Transactional
    public void save() {
        SampleBaseJpaAggregateRoot test3 = sampleBaseJpaFactory.create("test3");
        sampleBaseRepository.persist(test3);
        assertThat(sampleBaseRepository.load("test3").getField1()).isNull();
        test3.setField1("modified");
        sampleBaseRepository.save(test3);
        assertThat(sampleBaseRepository.load("test3").getField1()).isEqualTo("modified");
    }

    @Test
    public void clear() {
        doClear();
        checkClearResult();
    }

    @Transactional
    protected void doClear() {
        sampleBaseRepository.persist(sampleBaseJpaFactory.create("test4"));
        assertThat(sampleBaseRepository.load("test4")).isNotNull();
        sampleBaseRepository.clear();
    }

    @Transactional()
    protected void checkClearResult() {
        assertThat(sampleBaseRepository.load("test4")).isNull();
    }

    @Test
    @Transactional
    public void delete() {
        SampleBaseJpaAggregateRoot test5 = sampleBaseJpaFactory.create("test5");
        sampleBaseRepository.persist(test5);
        assertThat(sampleBaseRepository.load("test5")).isNotNull();
        sampleBaseRepository.delete(test5);
        assertThat(sampleBaseRepository.load("test5")).isNull();
        try {
            sampleBaseRepository.delete("test6");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @Transactional
    public void exists() {
        SampleBaseJpaAggregateRoot test7 = sampleBaseJpaFactory.create("test7");
        sampleBaseRepository.persist(test7);
        assertThat(sampleBaseRepository.exists("test7")).isTrue();
        assertThat(sampleBaseRepository.exists("test8")).isFalse();
    }
}
