/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.samples.domain.simple;

import org.seedstack.business.domain.GenericFactory;

import java.util.Date;

/**
 *
 * 
 * @author epo.jemba@ext.mpsa.com
 *
 */
public interface SampleSimpleFactory extends GenericFactory<SampleSimpleJpaAggregateRoot> {
	SampleSimpleJpaAggregateRoot createSampleSimpleJpaAggregateRoot(Integer id, String f1, String f2, Date d3, String f4);
	
}
