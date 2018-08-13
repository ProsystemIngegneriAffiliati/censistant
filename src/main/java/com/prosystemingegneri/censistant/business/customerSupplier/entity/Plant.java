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
package com.prosystemingegneri.censistant.business.customerSupplier.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class Plant extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isHeadOffice;
    
    @ManyToOne(optional = false)
    private CustomerSupplier customerSupplier;
    
    @NotNull
    @Column(nullable = false)
    private String name;
    
    @NotNull
    @Column(nullable = false)
    private String address;
    
    private String phone;
    
    private String fax;
    
    private String email;
    
    private String notes;
    
    @OneToOne(mappedBy = "plant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private SupplierPlantLocation location;
    
    @Version
    private int version;

    public Plant() {
        isHeadOffice = Boolean.FALSE;
    }

    public Plant(Boolean isHeadOffice, String name, String address) {
        this.isHeadOffice = isHeadOffice;
        this.name = name;
        this.address = address;
    }
    
    public String getNameAddress(boolean isNewLine) {
        String delim = " - ";
        
        if (isNewLine)
            delim = System.lineSeparator();
        
        return new StringBuilder(name)
                .append(delim)
                .append(address)
                .toString();
    }

    public Boolean getIsHeadOffice() {
        return isHeadOffice;
    }

    public void setIsHeadOffice(Boolean isHeadOffice) {
        this.isHeadOffice = isHeadOffice;
    }

    public CustomerSupplier getCustomerSupplier() {
        return customerSupplier;
    }

    public void setCustomerSupplier(CustomerSupplier customerSupplier) {
        this.customerSupplier = customerSupplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    
    public SupplierPlantLocation getLocation() {
        return location;
    }

    public void setLocation(SupplierPlantLocation location) {
        this.location = location;
        location.setPlant(this);
    }
    
    public void removeLocation() {
        this.location.setPlant(null);
        this.location = null;
    }
    
    public void checkSupplierPlantLocation() {
        if (customerSupplier.getIsSupplier() && location == null)
            setLocation(new SupplierPlantLocation());

        if (!customerSupplier.getIsSupplier() && location != null)
            removeLocation();
    }
}
