/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.fixtures.business.domain.test;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.seedstack.business.domain.BaseValueObject;

@Embeddable
public class VehicleTypeId extends BaseValueObject {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The brand country code.
     */
    @Column(name = "BRANDCOUNTRY_CODE")
    private String brandCountryCode;

    /**
     * The code.
     */
    @Column(name = "CODE")
    private String code;

    /**
     * Instantiates a new vehicle type id.
     */
    private VehicleTypeId() {
        // For JPA
    }

    /**
     * Instantiates a new vehicle type id.
     *
     * @param brandCountryCode the brand country code
     * @param code             the code
     */
    public VehicleTypeId(String brandCountryCode, String code) {
        this.brandCountryCode = brandCountryCode;
        this.code = code;
    }

    public String getBrandCountryCode() {
        return brandCountryCode;
    }

    public String getCode() {
        return code;
    }
}