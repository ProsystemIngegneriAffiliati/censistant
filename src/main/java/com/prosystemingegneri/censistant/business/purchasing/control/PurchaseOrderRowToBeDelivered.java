/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati
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

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class PurchaseOrderRowToBeDelivered {
    private Long id;

    private String purchaseOrderNumberAndCreation;
    
    private String supplierItemCode;
    
    private String supplierItemDescription;
    
    private Long quantityToBeDelivered;  //quantity still in supplier's warehouse
    
    private Long quantityPrepared;
    
    private String fullUnitMeasure;

    public PurchaseOrderRowToBeDelivered(Long id, String purchaseOrderNumberAndCreation, String supplierItemCode, String supplierItemDescription, Long quantityToBeDelivered, String fullUnitMeasure) {
        this.id = id;
        this.purchaseOrderNumberAndCreation = purchaseOrderNumberAndCreation;
        this.supplierItemCode = supplierItemCode;
        this.supplierItemDescription = supplierItemDescription;
        this.quantityToBeDelivered = quantityToBeDelivered;
        this.quantityPrepared = quantityToBeDelivered;
        this.fullUnitMeasure = fullUnitMeasure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseOrderNumberAndCreation() {
        return purchaseOrderNumberAndCreation;
    }

    public void setPurchaseOrderNumberAndCreation(String purchaseOrderNumberAndCreation) {
        this.purchaseOrderNumberAndCreation = purchaseOrderNumberAndCreation;
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
        return fullUnitMeasure;
    }

    public void setFullUnitMeasure(String fullUnitMeasure) {
        this.fullUnitMeasure = fullUnitMeasure;
    }
    
}
