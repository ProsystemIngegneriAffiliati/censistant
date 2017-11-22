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
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
public class Stock implements Serializable {
    public static final String SEPARATOR = "-";
    
    private Location location;
    private PurchaseOrderRow purchaseOrderRow;
    private Integer quantity;
    
    private Integer nakedQuantity;    //the quantity of "naked" item, without any box. It's the quantity with item's unit of measure
    private Integer maxQuantity;    //useful for limit the maximum movable amount
    private UnitMeasure unitMeasure;    //userful for choosing movement unit of measure

    public Stock(Location location, PurchaseOrderRow purchaseOrderRow, Integer quantity) {
        this.location = location;
        this.purchaseOrderRow = purchaseOrderRow;
        this.quantity = quantity;
        
        this.nakedQuantity = quantity;
        this.maxQuantity = quantity;
        this.unitMeasure = purchaseOrderRow.getBoxedItem().getItem().getItem().getUnitMeasure();
    }
    
    public String getId() {
        if (location != null && purchaseOrderRow != null && quantity != null)
            return location.getId().toString() + SEPARATOR + purchaseOrderRow.getId().toString() + SEPARATOR + quantity.toString();
        
        return "";
    }
    
    public String getLocationIdPurchaseOrderRowId() {
        if (location != null && purchaseOrderRow != null)
            return location.getId().toString() + SEPARATOR + purchaseOrderRow.getId().toString();
        
        return "";
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PurchaseOrderRow getPurchaseOrderRow() {
        return purchaseOrderRow;
    }

    public void setPurchaseOrderRow(PurchaseOrderRow purchaseOrderRow) {
        this.purchaseOrderRow = purchaseOrderRow;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public UnitMeasure getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(UnitMeasure unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public Integer getNakedQuantity() {
        return nakedQuantity;
    }

    public void setNakedQuantity(Integer nakedQuantity) {
        this.nakedQuantity = nakedQuantity;
    }
    
}
