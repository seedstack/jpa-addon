/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.fixtures.business.domain.tinyaggregate;

import org.seedstack.business.assembler.BaseAssembler;

public class TinyAssembler extends BaseAssembler<TinyAggRoot, TinyDto> {

  @Override
  public void mergeAggregateIntoDto(TinyAggRoot sourceAggregate, TinyDto targetDto) {
    targetDto.setId(sourceAggregate.getId());
    targetDto.setName(sourceAggregate.getName());
  }

  @Override
  public void mergeDtoIntoAggregate(TinyDto sourceDto, TinyAggRoot targetAggregate) {
    targetAggregate.setName(sourceDto.getName());
  }
}
