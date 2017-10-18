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
package com.prosystemingegneri.censistant.business.customerSupplier.entity;

import com.prosystemingegneri.censistant.business.customerSupplier.controller.MandatoryHeadOffice;
import com.prosystemingegneri.censistant.business.customerSupplier.controller.MandatoryProvenanceForCustomer;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierLocation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@MandatoryHeadOffice
@MandatoryProvenanceForCustomer
public class CustomerSupplier extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isPotentialCustomer;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isCustomer;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isSupplier;
    
    @Column(nullable = false, unique = true)
    @NotNull
    private String businessName;
    
    @Column(nullable = false)
    @NotNull
    private String name;
    
    private String vatRegistrationNumber;
    
    private String taxCode;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerSupplier", orphanRemoval = true)
    private List<Plant> plants;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerSupplier", orphanRemoval = true)
    private List<Referee> referees;
    
    @ManyToOne
    private Provenance provenance;
    
    private String notes;
    
    @OneToOne(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private SupplierLocation location;
    
    @Version
    private int version;

    public CustomerSupplier() {
        plants = new ArrayList<>();
        referees = new ArrayList<>();
        isCustomer = Boolean.FALSE;
        isSupplier = Boolean.FALSE;
        isPotentialCustomer = Boolean.FALSE;
    }

    public CustomerSupplier(Boolean isPotentialCustomer, Boolean isCustomer, Boolean isSupplier) {
        this();
        this.isPotentialCustomer = isPotentialCustomer;
        this.isCustomer = isCustomer;
        this.isSupplier = isSupplier;
    }

    public Boolean getIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(Boolean isCustomer) {
        this.isCustomer = isCustomer;
    }

    public Boolean getIsSupplier() {
        return isSupplier;
    }

    public void setIsSupplier(Boolean isSupplier) {
        this.isSupplier = isSupplier;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatRegistrationNumber() {
        return vatRegistrationNumber;
    }

    public void setVatRegistrationNumber(String vatRegistrationNumber) {
        this.vatRegistrationNumber = vatRegistrationNumber;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
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
    
    public void addPlant(Plant plant) {
        if (!plants.contains(plant)) {
            plants.add(plant);
            plant.setCustomerSupplier(this);
        }
    }
    
    public void removePlant(Plant plant) {
        if (plants.contains(plant)) {
            plants.remove(plant);
            plant.setCustomerSupplier(null);
        }
    }

    public List<Plant> getPlants() {
        return plants;
    }
    
    public Plant getHeadOffice() {
        for (Plant plant : plants)
            if (plant.getIsHeadOffice())
                return plant;
        
        return null;
    }
    
    public void addReferee(Referee referee) {
        if (!referees.contains(referee)) {
            referees.add(referee);
            referee.setCustomerSupplier(this);
        }
    }
    
    public void removeReferee(Referee referee) {
        if (referees.contains(referee)) {
            referees.remove(referee);
            referee.setCustomerSupplier(null);
        }
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public Boolean getIsPotentialCustomer() {
        return isPotentialCustomer;
    }

    public void setIsPotentialCustomer(Boolean isPotentialCustomer) {
        this.isPotentialCustomer = isPotentialCustomer;
    }

    public SupplierLocation getLocation() {
        return location;
    }

    public void setLocation(SupplierLocation location) {
        this.location = location;
        location.setSupplier(this);
    }
    
    public void removeLocation() {
        this.location.setSupplier(null);
        this.location = null;
    }
    
}
