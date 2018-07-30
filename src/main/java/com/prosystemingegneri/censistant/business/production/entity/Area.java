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
package com.prosystemingegneri.censistant.business.production.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class Area extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(optional = false)
    private System system;
    
    @NotNull
    @Column(nullable = false)
    private String number;
    
    @NotNull
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    private SupplierItem supplierItem;
    
    private String deviceProgrammingTypeStr;
    
    private String field;
    
    private String notes;
    
    @Version
    private int version;

    public Area() {
    }
    
    public Area duplicate() {
        Area newArea = new Area();
        
        final String append = " copied";
        
        newArea.setDeviceProgrammingTypeStr(deviceProgrammingTypeStr);
        newArea.setName(name);
        newArea.setNumber(number + append);
        newArea.setSupplierItem(supplierItem);
        newArea.setField(field);
        newArea.setNotes(notes);
        
        return newArea;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupplierItem getSupplierItem() {
        return supplierItem;
    }

    public void setSupplierItem(SupplierItem supplierItem) {
        this.supplierItem = supplierItem;
    }

    public String getDeviceProgrammingTypeStr() {
        return deviceProgrammingTypeStr;
    }

    public void setDeviceProgrammingTypeStr(String deviceProgrammingTypeStr) {
        this.deviceProgrammingTypeStr = deviceProgrammingTypeStr;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    
}
