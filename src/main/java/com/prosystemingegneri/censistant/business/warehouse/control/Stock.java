/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.warehouse.control;

import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
public class Stock implements Serializable {
    public static final String SEPARATOR = "-";
    
    private Location location;
    private BoxedItem boxedItem;
    
    private Integer quantity;           //quantity chosen by the user for doing the item movement
    private UnitMeasure unitMeasure;   //unit of measure chosen by the user for doing the item movement
    
    private Integer nakedQuantity;  //stock (maxmimum) "naked" (item without the box) quantity
    private Integer boxedQuantity;  //stock (maxmimum) "boxed" (item with the box) quantity
    private UnitMeasure nakedUnitMeasure;   //item unit of measure
    private UnitMeasure boxedUnitMeasure;   //box unit of measure

    public Stock(Location location, BoxedItem boxedItem, Integer quantity) {
        this.location = location;
        this.boxedItem = boxedItem;
        
        this.quantity = quantity;
        this.unitMeasure = boxedItem.getItem().getItem().getUnitMeasure();
        
        this.nakedQuantity = quantity;
        this.boxedQuantity = quantity / boxedItem.getBox().getQuantity();
        this.nakedUnitMeasure = boxedItem.getItem().getItem().getUnitMeasure();
        this.boxedUnitMeasure = boxedItem.getBox().getUnitMeasure();
    }
    
    public String getId() {
        if (location != null && boxedItem != null && quantity != null)
            return location.getId().toString() + SEPARATOR + boxedItem.getId().toString() + SEPARATOR + quantity.toString();
        
        return "";
    }
    
    public String getLocationIdBoxedItemId() {
        if (location != null && boxedItem != null)
            return location.getId().toString() + SEPARATOR + boxedItem.getId().toString();
        
        return "";
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BoxedItem getBoxedItem() {
        return boxedItem;
    }

    public void setBoxedItem(BoxedItem boxedItem) {
        this.boxedItem = boxedItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getNakedQuantity() {
        return nakedQuantity;
    }

    public void setNakedQuantity(Integer nakedQuantity) {
        this.nakedQuantity = nakedQuantity;
    }

    public Integer getBoxedQuantity() {
        return boxedQuantity;
    }

    public void setBoxedQuantity(Integer boxedQuantity) {
        this.boxedQuantity = boxedQuantity;
    }

    public UnitMeasure getNakedUnitMeasure() {
        return nakedUnitMeasure;
    }

    public void setNakedUnitMeasure(UnitMeasure nakedUnitMeasure) {
        this.nakedUnitMeasure = nakedUnitMeasure;
    }

    public UnitMeasure getBoxedUnitMeasure() {
        return boxedUnitMeasure;
    }

    public void setBoxedUnitMeasure(UnitMeasure boxedUnitMeasure) {
        this.boxedUnitMeasure = boxedUnitMeasure;
    }

    public UnitMeasure getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(UnitMeasure unitMeasure) {
        this.unitMeasure = unitMeasure;
    }
    
}
