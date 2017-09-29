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
import com.prosystemingegneri.censistant.business.production.entity.Item;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class SupplierItem extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private String code;
    
    @NotNull
    @ManyToOne(optional = false)
    private CustomerSupplier supplier;
    
    @NotNull
    @ManyToOne(optional = false)
    private Item item;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item", orphanRemoval = true)
    private List<BoxedItem> boxedItems;
    
    private String notes;
    
    @Version
    private int version;

    public SupplierItem() {
        boxedItems = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CustomerSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(CustomerSupplier supplier) {
        this.supplier = supplier;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
    
    public void addBoxedItem(BoxedItem item, Box box) {
        if (!boxedItems.contains(item)) {
            box.addBoxedItem(item);
            
            boxedItems.add(item);
            item.setItem(this);
        }
    }
    
    public void removeBoxedItem(BoxedItem item) {
        if (boxedItems.contains(item)) {
            item.getBox().removeBoxedItem(item);
            
            boxedItems.remove(item);
            item.setItem(null);
        }
    }

    public List<BoxedItem> getBoxedItems() {
        return boxedItems;
    }
    
}
