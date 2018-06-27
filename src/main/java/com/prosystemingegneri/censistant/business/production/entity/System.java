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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@DiscriminatorValue(value = "2")
public class System extends Location {
    
    @Transient
    private String name;    //useful only to entity metadata
    
    @NotNull
    @Column(nullable = false)
    private String description;
    
    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system", orphanRemoval = true)
    private final List<Device> devices;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system", orphanRemoval = true)
    private final List<SystemAttachment> systemAttachments;
    
    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system", orphanRemoval = true)
    private final List<Area> areas;
    
    @OneToMany(mappedBy = "system")
    private final List<Offer> offers;
    
    private String userCode;
    
    private String controlPanelPhoneNumber;
    
    private String controlPanelPosition;
    
    private String powerSource;
    
    private String notes;

    public System() {
        offers = new ArrayList<>();
        devices = new ArrayList<>();
        systemAttachments = new ArrayList<>();
        areas = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getControlPanelPhoneNumber() {
        return controlPanelPhoneNumber;
    }

    public void setControlPanelPhoneNumber(String controlPanelPhoneNumber) {
        this.controlPanelPhoneNumber = controlPanelPhoneNumber;
    }

    public String getControlPanelPosition() {
        return controlPanelPosition;
    }

    public void setControlPanelPosition(String controlPanelPosition) {
        this.controlPanelPosition = controlPanelPosition;
    }

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void addOffer(Offer offer) {
        if (!offers.contains(offer)) {
            offer.setSystem(this);
            offers.add(offer);
        }
    }
    
    public void removeOffer(Offer offer) {
        if (offers.contains(offer)) {
            offer.setSystem(null);
            offers.remove(offer);
        }
    }

    public List<Offer> getOffers() {
        return offers;
    }
    
    public void addDevice(Device device) {
        if (!devices.contains(device)) {
            device.setSystem(this);
            devices.add(device);
        }
    }
    
    public void removeDevice(Device device) {
        if (devices.contains(device)) {
            device.setSystem(null);
            devices.remove(device);
        }
    }
    
    public List<Device> getDevices() {
        return devices;
    }
    
    public void addSystemAttachment(SystemAttachment systemAttachment) {
        if (!systemAttachments.contains(systemAttachment)) {
            systemAttachment.setSystem(this);
            systemAttachments.add(systemAttachment);
        }
    }
    
    public void removeSystemAttachment(SystemAttachment systemAttachment) {
        if (systemAttachments.contains(systemAttachment)) {
            systemAttachment.setSystem(null);
            systemAttachments.remove(systemAttachment);
        }
    }

    public List<SystemAttachment> getSystemAttachments() {
        return systemAttachments;
    }
    
    public void addArea(Area area) {
        if (!areas.contains(area)) {
            area.setSystem(this);
            areas.add(area);
        }
    }
    
    public void removeArea(Area area) {
        if (areas.contains(area)) {
            area.setSystem(null);
            areas.remove(area);
        }
    }

    public List<Area> getAreas() {
        return areas;
    }

    @Override
    public String getName() {
        String delimiter = DELIMITATOR;
        String offerNumber = "";
        String customerName = "";
        
        if (offers != null && !offers.isEmpty()) {
            offerNumber = offers.get(offers.size() - 1).getNumber().toString() + delimiter;
            customerName = offers.get(offers.size() - 1).getSiteSurveyReport().getPlant().getCustomerSupplier().getName() + delimiter;
        }
        
        return customerName + offerNumber + description;
    }

    @Override
    public CustomerSupplier getCustomerSupplier() {
        CustomerSupplier result = null;
        
        if (!offers.isEmpty())
            result = offers.get(offers.size() - 1).getSiteSurveyReport().getPlant().getCustomerSupplier();
        
        return result;
    }

    @Override
    public String getCustomerSupplierNamePlantNameAddress(boolean isNewLine) {
        String delim = DELIMITATOR;
    
        if (isNewLine)
            delim = java.lang.System.lineSeparator();
        
        String customerSupplierName = "customerSupplier";
        String plantNameAddress = "plantNameAddress";
        
        if (!offers.isEmpty()) {
            Plant plant = offers.get(offers.size() - 1).getSiteSurveyReport().getPlant();
            customerSupplierName = plant.getCustomerSupplier().getName();
            plantNameAddress = plant.getNameAddress(isNewLine);
        }
    
        return new StringBuilder(customerSupplierName)
                .append(delim)
                .append(plantNameAddress)
                .toString();
    }

    @Override
    public String getAddress() {
        if (!offers.isEmpty())
            return offers.get(offers.size() - 1).getJobOrder().getJobOrderNumberAddress();
        else
            return "";
    }
    
}
