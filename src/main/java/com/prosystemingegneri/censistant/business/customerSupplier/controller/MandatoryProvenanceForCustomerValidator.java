/*
 * Copyright (C) 2017-2019 Prosystem Ingegneri Affiliati
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.prosystemingegneri.censistant.business.customerSupplier.controller;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
public class MandatoryProvenanceForCustomerValidator implements ConstraintValidator<MandatoryProvenanceForCustomer, CustomerSupplier> {
    @Override
    public void initialize(MandatoryProvenanceForCustomer constraintAnnotation) {
    }

    @Override
    public boolean isValid(CustomerSupplier customerSupplier, ConstraintValidatorContext context) {
        return !customerSupplier.getIsCustomer() || customerSupplier.getProvenance() != null;
    }
}
