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
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaAggregateRoot;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseJpaFactory;
import org.seedstack.jpa.fixtures.samples.domain.base.SampleBaseRepository;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-biz-support")
@RunWith(SeedITRunner.class)
public class BaseJpaRepositoryIT {
    @Inject
    SampleBaseRepository sampleBaseRepository;
    @Inject
    SampleBaseJpaFactory sampleBaseJpaFactory;

    @Test
    @Transactional
    public void persist_and_load() {
        sampleBaseRepository.persist(sampleBaseJpaFactory.create("test1"));
        assertThat(sampleBaseRepository.load("test1")).isNotNull();
    }

    @Test
    @Transactional
    public void persist_and_save() {
        SampleBaseJpaAggregateRoot test3 = sampleBaseJpaFactory.create("test3");
        sampleBaseRepository.persist(test3);
        assertThat(sampleBaseRepository.load("test3").getField1()).isNull();
        test3.setField1("modified");
        sampleBaseRepository.save(test3);
        assertThat(sampleBaseRepository.load("test3").getField1()).isEqualTo("modified");
    }

    @Test
    @Transactional
    public void persist_and_delete() {
        SampleBaseJpaAggregateRoot test4 = sampleBaseJpaFactory.create("test4");
        sampleBaseRepository.persist(test4);
        assertThat(sampleBaseRepository.load("test4")).isNotNull();
        sampleBaseRepository.delete(test4);
        assertThat(sampleBaseRepository.load("test3")).isNull();
    }

    @Test(expected = EntityNotFoundException.class)
    @Transactional
    public void delete_inexistent_entity() {
        sampleBaseRepository.delete("test5");
        fail("should have failed");
    }

    @Test
    public void clear() {
        doClear();
        checkClearResult();
    }

    @Transactional
    protected void doClear() {
        sampleBaseRepository.persist(sampleBaseJpaFactory.create("test2"));
        assertThat(sampleBaseRepository.load("test2")).isNotNull();
        sampleBaseRepository.clear();
    }

    @Transactional()
    protected void checkClearResult() {
        assertThat(sampleBaseRepository.load("test2")).isNull();
    }
}
