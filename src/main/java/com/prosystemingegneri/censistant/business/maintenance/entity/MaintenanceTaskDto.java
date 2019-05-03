/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.maintenance.entity;

import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class MaintenanceTaskDto implements Serializable {
    private Long id;
    private String customerNameFromMaintenancePlan;
    private String systemAddressFromMaintenancePlan;
    private String customerNameFromSystem;
    private String systemAddressFromSystem;
    private MaintenanceType maintenanceType;
    private Date expiry;
    private Date closed;
    private String closingNotes;

    public MaintenanceTaskDto() {
    }
    
    public MaintenanceTaskDto(String maina) {
    }

    public MaintenanceTaskDto(
            Long id,
            String customerNameFromMaintenancePlan,
            String systemAddressFromMaintenancePlan,
            String customerNameFromSystem,
            String systemAddressFromSystem,
            MaintenanceType maintenanceType,
            Date expiry,
            Date closed,
            String closingNotes) {
        this.id = id;
        this.customerNameFromMaintenancePlan = customerNameFromMaintenancePlan;
        this.systemAddressFromMaintenancePlan = systemAddressFromMaintenancePlan;
        this.customerNameFromSystem = customerNameFromSystem;
        this.systemAddressFromSystem = systemAddressFromSystem;
        this.maintenanceType = maintenanceType;
        this.expiry = expiry;
        this.closed = closed;
        this.closingNotes = closingNotes;
    }
    
    public static MaintenanceTaskDto create(MaintenanceTask maintenanceTask) {
        MaintenanceTaskDto result = new MaintenanceTaskDto();
        
        if (maintenanceTask != null) {
            if (maintenanceTask.getMaintenancePlan() != null) {
                System system = maintenanceTask.getMaintenancePlan().getContractedSystem().getSystem();
                Offer lastOffer = system.getOffers().get(system.getOffers().size() - 1);
                result.setCustomerNameFromMaintenancePlan(system.getCustomerSupplier().getName());
                result.setSystemAddressFromMaintenancePlan(lastOffer.getSiteSurveyReport().getPlant().getAddress());
                result.setMaintenanceType(maintenanceTask.getMaintenancePlan().getMaintenanceType());
            }
            if (maintenanceTask.getSystem() != null) {
                result.setCustomerNameFromSystem(maintenanceTask.getSystem().getCustomerSupplier().getName());
                result.setSystemAddressFromSystem(maintenanceTask.getSystem().getOffers().get(maintenanceTask.getSystem().getOffers().size() - 1).getSiteSurveyReport().getPlant().getAddress());
            }
            
            result.setClosed(maintenanceTask.getClosed());
            result.setExpiry(maintenanceTask.getExpiry());
            result.setId(maintenanceTask.getId());
            result.setClosingNotes(maintenanceTask.getClosingNotes());
        }
        
        return result;
    }
    
    public String getCustomerName() {
        if (customerNameFromMaintenancePlan != null && !customerNameFromMaintenancePlan.isEmpty())
            return customerNameFromMaintenancePlan;
        if (customerNameFromSystem != null && !customerNameFromSystem.isEmpty())
            return customerNameFromMaintenancePlan;
        
        return "";
    }
    
    public String getSystemAddress() {
        if (systemAddressFromMaintenancePlan != null && !systemAddressFromMaintenancePlan.isEmpty())
            return systemAddressFromMaintenancePlan;
        if (systemAddressFromSystem != null && !systemAddressFromSystem.isEmpty())
            return systemAddressFromSystem;
        
        return "";
    }

    public String getCustomerNameFromMaintenancePlan() {
        return customerNameFromMaintenancePlan;
    }

    public void setCustomerNameFromMaintenancePlan(String customerNameFromMaintenancePlan) {
        this.customerNameFromMaintenancePlan = customerNameFromMaintenancePlan;
    }

    public String getSystemAddressFromMaintenancePlan() {
        return systemAddressFromMaintenancePlan;
    }

    public void setSystemAddressFromMaintenancePlan(String systemAddressFromMaintenancePlan) {
        this.systemAddressFromMaintenancePlan = systemAddressFromMaintenancePlan;
    }

    public String getCustomerNameFromSystem() {
        return customerNameFromSystem;
    }

    public void setCustomerNameFromSystem(String customerNameFromSystem) {
        this.customerNameFromSystem = customerNameFromSystem;
    }

    public String getSystemAddressFromSystem() {
        return systemAddressFromSystem;
    }

    public void setSystemAddressFromSystem(String systemAddressFromSystem) {
        this.systemAddressFromSystem = systemAddressFromSystem;
    }

    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClosingNotes() {
        return closingNotes;
    }

    public void setClosingNotes(String closingNotes) {
        this.closingNotes = closingNotes;
    }
    
    public boolean isSeeNotes() {
        return closed != null && maintenanceType == MaintenanceType.TELECONTROL && closingNotes != null && !closingNotes.isEmpty();
    }
}
