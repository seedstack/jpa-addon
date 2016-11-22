/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.samples.domain.tinyaggregate;

import org.seedstack.business.assembler.BaseAssembler;

public class TinyAssembler extends BaseAssembler<TinyAggRoot, TinyDto> {
    @Override
    protected void doAssembleDtoFromAggregate(TinyDto targetDto, TinyAggRoot sourceAggregate) {
        targetDto.setId(sourceAggregate.getEntityId());
        targetDto.setName(sourceAggregate.getName());
    }

    @Override
    protected void doMergeAggregateWithDto(TinyAggRoot targetAggregate, TinyDto sourceDto) {
        targetAggregate.setName(sourceDto.getName());
    }
}
