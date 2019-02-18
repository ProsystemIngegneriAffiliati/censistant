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
package com.prosystemingegneri.censistant.business.purchasing.control;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class PurchaseOrderRowToBeDelivered {
    private Long id;

    private Integer purchaseOrderNumber;
    
    private Date purchaseOrderCreation;
    
    private String supplierItemCode;
    
    private String supplierItemDescription;
    
    private Long quantityToBeDelivered;  //quantity still in supplier's warehouse
    
    private Long quantityPrepared;
    
    private String boxUnitMeasureName;
    
    private Integer boxQuantity;
    
    private String itemUnitMeasureSymbol;

    public PurchaseOrderRowToBeDelivered(Long id, Integer purchaseOrderNumber, Date purchaseOrderCreation, String supplierItemCode, String supplierItemDescription, Long quantityToBeDelivered, String boxUnitMeasureName, Integer boxQuantity, String itemUnitMeasureSymbol) {
        this.id = id;
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.purchaseOrderCreation = purchaseOrderCreation;
        this.supplierItemCode = supplierItemCode;
        this.supplierItemDescription = supplierItemDescription;
        this.quantityToBeDelivered = quantityToBeDelivered;
        this.quantityPrepared = quantityToBeDelivered;
        this.boxUnitMeasureName = boxUnitMeasureName;
        this.boxQuantity = boxQuantity;
        this.itemUnitMeasureSymbol = itemUnitMeasureSymbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseOrderNumberAndCreation() {
        return new StringBuilder(String.valueOf(purchaseOrderNumber))
                .append(" - ")
                .append(new SimpleDateFormat("dd/MM/yyyy").format(purchaseOrderCreation))
                .toString();
    }

    public String getSupplierItemCode() {
        return supplierItemCode;
    }

    public void setSupplierItemCode(String supplierItemCode) {
        this.supplierItemCode = supplierItemCode;
    }

    public String getSupplierItemDescription() {
        return supplierItemDescription;
    }

    public void setSupplierItemDescription(String supplierItemDescription) {
        this.supplierItemDescription = supplierItemDescription;
    }

    public Long getQuantityToBeDelivered() {
        return quantityToBeDelivered;
    }

    public void setQuantityToBeDelivered(Long quantityToBeDelivered) {
        this.quantityToBeDelivered = quantityToBeDelivered;
    }

    public Long getQuantityPrepared() {
        return quantityPrepared;
    }

    public void setQuantityPrepared(Long quantityPrepared) {
        this.quantityPrepared = quantityPrepared;
    }

    public String getFullUnitMeasure() {
        return new StringBuilder(boxUnitMeasureName)
                .append(" ")
                .append(String.valueOf(boxQuantity))
                .append(" ")
                .append(itemUnitMeasureSymbol)
                .toString();
    }

    public Integer getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(Integer purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public Date getPurchaseOrderCreation() {
        return purchaseOrderCreation;
    }

    public void setPurchaseOrderCreation(Date purchaseOrderCreation) {
        this.purchaseOrderCreation = purchaseOrderCreation;
    }

    public String getBoxUnitMeasureName() {
        return boxUnitMeasureName;
    }

    public void setBoxUnitMeasureName(String boxUnitMeasureName) {
        this.boxUnitMeasureName = boxUnitMeasureName;
    }

    public Integer getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(Integer boxQuantity) {
        this.boxQuantity = boxQuantity;
    }

    public String getItemUnitMeasureSymbol() {
        return itemUnitMeasureSymbol;
    }

    public void setItemUnitMeasureSymbol(String itemUnitMeasureSymbol) {
        this.itemUnitMeasureSymbol = itemUnitMeasureSymbol;
    }
    
}
