/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.customerSupplier.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@StaticMetamodel(CustomerSupplier.class)
public class CustomerSupplier_ {
    public static volatile SingularAttribute<CustomerSupplier, Long> id;
    public static volatile SingularAttribute<CustomerSupplier, Boolean> isPotentialCustomer;
    public static volatile SingularAttribute<CustomerSupplier, Boolean> isOnlyInfo;
    public static volatile SingularAttribute<CustomerSupplier, Boolean> isCustomer;
    public static volatile SingularAttribute<CustomerSupplier, Boolean> isSupplier;
    public static volatile SingularAttribute<CustomerSupplier, String> businessName;
    public static volatile SingularAttribute<CustomerSupplier, String> name;
    public static volatile ListAttribute<CustomerSupplier, Plant> plants;
}
