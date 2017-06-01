/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.business.domain.base.SampleBaseRepository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@JpaUnit("business")
@RunWith(SeedITRunner.class)
public class BaseJpaRepositoryIT {
    @Inject
    private SampleBaseRepository sampleBaseRepository;
    @Inject
    private SampleBaseJpaFactory sampleBaseJpaFactory;

    @Test
    @Transactional
    public void persist() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test1"));
        assertThat(sampleBaseRepository.get("test1")).isNotEmpty();
    }

    @Test
    @Transactional
    public void get() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test2"));
        assertThat(sampleBaseRepository.get("test2")).isNotEmpty();
    }

    @Test
    @Transactional
    public void save() {
        SampleBaseJpaAggregateRoot test3 = sampleBaseJpaFactory.create("test3");
        sampleBaseRepository.add(test3);
        assertThat(sampleBaseRepository.get("test3").get().getField1()).isNull();
        test3.setField1("modified");
        sampleBaseRepository.update(test3);
        assertThat(sampleBaseRepository.get("test3").get().getField1()).isEqualTo("modified");
    }

    @Test
    public void clear() {
        doClear();
        checkClearResult();
    }

    @Transactional
    protected void doClear() {
        sampleBaseRepository.add(sampleBaseJpaFactory.create("test4"));
        assertThat(sampleBaseRepository.get("test4")).isNotEmpty();
        sampleBaseRepository.clear();
    }

    @Transactional()
    protected void checkClearResult() {
        assertThat(sampleBaseRepository.get("test4")).isEmpty();
    }

    @Test
    @Transactional
    public void delete() {
        SampleBaseJpaAggregateRoot test5 = sampleBaseJpaFactory.create("test5");
        sampleBaseRepository.add(test5);
        assertThat(sampleBaseRepository.get("test5")).isNotEmpty();
        sampleBaseRepository.remove(test5);
        assertThat(sampleBaseRepository.get("test5")).isEmpty();
        try {
            sampleBaseRepository.remove("test6");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @Transactional
    public void exists() {
        SampleBaseJpaAggregateRoot test7 = sampleBaseJpaFactory.create("test7");
        sampleBaseRepository.add(test7);
        assertThat(sampleBaseRepository.contains("test7")).isTrue();
        assertThat(sampleBaseRepository.contains("test8")).isFalse();
    }
}
