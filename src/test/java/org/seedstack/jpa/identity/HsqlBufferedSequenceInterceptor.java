/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.seedstack.jpa.identity;

import javax.inject.Named;

import org.seedstack.business.domain.Entity;

@Named("hsqlInterceptor")
public class HsqlBufferedSequenceInterceptor extends HSQLBufferedSequenceGenerator {

    private static int segmentCalls = 0;

    @Override
    protected <E extends Entity<Long>> Long retrieveNextSegment(long chunkSize,
            Class<E> entityClass) {
        segmentCalls += 1;
        return super.retrieveNextSegment(chunkSize, entityClass);
    }

    public static int getSegmentCalls() {
        return segmentCalls;
    }

    public static void resetSegmentCalls() {
        HsqlBufferedSequenceInterceptor.segmentCalls = 0;
    }

}
