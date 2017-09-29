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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class BoxedItem extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Transient
    public static final int SCALE_COST = 4;
    @Transient
    public static final int PRECISION_COST = 2;

    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_COST, precision = SCALE_COST + PRECISION_COST)
    private BigDecimal cost;
    
    @NotNull
    @ManyToOne(optional = false)
    private SupplierItem item;
    
    @NotNull
    @ManyToOne(optional = false)
    private Box box;
    
    private String notes;
    
    @Version
    private int version;

    public BoxedItem() {
        cost = BigDecimal.ZERO;
    }

    public BigDecimal getCost() {
        return cost;
    }
    
    public BigDecimal getTotalCost() {
        if (box != null)
            return cost.multiply(BigDecimal.valueOf(box.getQuantity()));
        
        return BigDecimal.ZERO;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public SupplierItem getItem() {
        return item;
    }

    public void setItem(SupplierItem item) {
        this.item = item;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
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
