/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class PlantCreation {
    
    public static Plant createCustomerPlant() {
        return createPlant();
    }
    
    public static Plant createSupplierPlant() {
        Plant plant = createPlant();
        plant.setLocation(new SupplierPlantLocation());
        
        return plant;
    }
    
    private static Plant createPlant() {
        return new Plant();
    }
}
