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
package com.prosystemingegneri.censistant.business.purchasing.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class PurchaseOrderRow extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Transient
    public static final int SCALE_COST = 2; //If zero or positive, the scale is the number of digits to the right of the decimal point.
    @Transient
    public static final int PRECISION_COST = 8;
    
    @ManyToOne(optional = false)
    private PurchaseOrder purchaseOrder;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)    
    private Integer quantity;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_COST, precision = PRECISION_COST)
    private BigDecimal cost;
    
    @ManyToOne(optional = false)
    private BoxedItem boxedItem;
    
    private String notes;
    
    @Version
    private int version;

    public PurchaseOrderRow() {
        quantity = 1;
        cost = BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalCost() {
        return cost.multiply(new BigDecimal(quantity));
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BoxedItem getBoxedItem() {
        return boxedItem;
    }

    public void setBoxedItem(BoxedItem boxedItem) {
        this.boxedItem = boxedItem;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public Long getId() {
        return id;
    }
    
}
