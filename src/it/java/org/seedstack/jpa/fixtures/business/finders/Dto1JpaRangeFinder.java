/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.finders;

import org.seedstack.business.finder.BaseRangeFinder;
import org.seedstack.business.finder.Range;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class Dto1JpaRangeFinder extends BaseRangeFinder<Dto1, String> {
    @Inject
    EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    protected List<Dto1> computeResultList(Range range, String param1) {
        Query query = entityManager.createQuery("select new " + Dto1.class.getName() + "(  a.entityId, a.field1, a.field2 , a.field3 , a.field4  )  from SampleSimpleJpaAggregateRoot a where a.field2 = :param1");
        query.setFirstResult((int) range.getOffset());
        query.setMaxResults((int) range.getSize());
        return (List<Dto1>) query.getResultList();
    }

    @Override
    protected long computeFullRequestSize(String param1) {
        Query query = entityManager.createQuery("select count(*) from SampleSimpleJpaAggregateRoot a where a.field2 = :param1");
        query.setParameter("param1", param1);
        return (Long) query.getSingleResult();
    }
}
