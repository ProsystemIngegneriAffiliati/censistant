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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class PurchaseOrder extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creation;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer number;
    
    @NotNull
    @ManyToOne(optional = false)
    private CustomerSupplier supplier;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchaseOrder", orphanRemoval = true)
    private final List<PurchaseOrderRow> rows;
    
    private String notes;
    
    @Version
    private int version;

    public PurchaseOrder() {
        rows = new ArrayList<>();
    }

    public PurchaseOrder(Integer number) {
        this();
        this.number = number;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public CustomerSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(CustomerSupplier supplier) {
        this.supplier = supplier;
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
    
    public void addRow(PurchaseOrderRow row) {
        if (!rows.contains(row)) {
            row.setPurchaseOrder(this);
            rows.add(row);
        }
    }
    
    public void removeRow(PurchaseOrderRow row) {
        if (rows.contains(row)) {
            row.setPurchaseOrder(null);
            rows.remove(row);
        }
    }

    public List<PurchaseOrderRow> getRows() {
        return rows;
    }
    
}
