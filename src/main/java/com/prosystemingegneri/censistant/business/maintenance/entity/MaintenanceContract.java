/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.maintenance.control.AtLeastOneScheduledMaintenance;
import com.prosystemingegneri.censistant.business.maintenance.control.AtLeastOneSystem;
import com.prosystemingegneri.censistant.business.production.entity.System;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
@AtLeastOneSystem
@AtLeastOneScheduledMaintenance
public class MaintenanceContract extends BaseEntity<Long> {
    @Transient
    public static final int SCALE = 2; //If zero or positive, the scale is the number of digits to the right of the decimal point.
    @Transient
    public static final int PRECISION = 7;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Transient
    public static final Integer DURATION_MONTHS = 12;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creation;
    
    private String payment;
    
    @NotNull
    @DecimalMin(value = "0")
    @Column(nullable = false, scale = SCALE, precision = PRECISION)   
    private BigDecimal price;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isFullService;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isOnCall;
    
    @ManyToMany
    private final List<System> systems;
    
    /*@OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceContract", orphanRemoval = true)
    private final List<MaintenanceTask> maintenanceTasks;*/
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceContract", orphanRemoval = true)
    private final List<ScheduledMaintenance> scheduledMaintenances;
    
    @Lob
    private String notes;
    
    @Version
    private int version;
    
    @Transient
    private final String SEPARATOR = " - ";

    public MaintenanceContract() {
        creation = new Date();
        price = BigDecimal.ZERO;
        isFullService = Boolean.FALSE;
        isOnCall = Boolean.FALSE;
        systems = new ArrayList<>();
        /*maintenanceTasks = new ArrayList<>();*/
        scheduledMaintenances = new ArrayList<>();
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Boolean getIsFullService() {
        return isFullService;
    }

    public void setIsFullService(Boolean isFullService) {
        this.isFullService = isFullService;
    }

    public Boolean getIsOnCall() {
        return isOnCall;
    }

    public void setIsOnCall(Boolean isOnCall) {
        this.isOnCall = isOnCall;
    }

    @Override
    public Long getId() {
        return id;
    }

    public List<System> getSystems() {
        return systems;
    }
    
    /*public void addMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (!maintenanceTasks.contains(maintenanceTask)) {
            maintenanceTasks.add(maintenanceTask);
            maintenanceTask.setMaintenanceContract(this);
        }
    }
    
    public void removeMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (maintenanceTasks.contains(maintenanceTask)) {
            maintenanceTasks.remove(maintenanceTask);
            maintenanceTask.setMaintenanceContract(null);
        }
    }

    public List<MaintenanceTask> getMaintenanceTasks() {
        return maintenanceTasks;
    }*/
    
    public void addScheduledMaintenance(ScheduledMaintenance scheduledMaintenance) {
        if (!scheduledMaintenances.contains(scheduledMaintenance)) {
            scheduledMaintenances.add(scheduledMaintenance);
            scheduledMaintenance.setMaintenanceContract(this);
        }
    }
    
    public void removeScheduledMaintenance(ScheduledMaintenance scheduledMaintenance) {
        if (scheduledMaintenances.contains(scheduledMaintenance)) {
            scheduledMaintenances.remove(scheduledMaintenance);
            scheduledMaintenance.setMaintenanceContract(null);
        }
    }
    
    public List<ScheduledMaintenance> getScheduledMaintenances() {
        return scheduledMaintenances;
    }

    public Date getExpiry() {
        Calendar created = new GregorianCalendar();
        created.setTime(creation);
        created.add(Calendar.MONTH, DURATION_MONTHS);
        
        return created.getTime();
    }
    
    public CustomerSupplier getCustomer() {
        if (systems != null && !systems.isEmpty())
            return systems.get(0).getCustomerSupplier();
        else
            return null;
    }
    
    public String getCustomerName() {
        if (systems != null && !systems.isEmpty() && systems.get(0).getCustomerSupplier() != null)
            return systems.get(0).getCustomerSupplier().getName();
        else
            return "";
    }
    
    public String getCustomerNameContractExpiry() {
                return new StringBuilder(getCustomerName())
                .append(SEPARATOR)
                .append(new SimpleDateFormat("dd/MM/yyyy").format(getExpiry()))
                .toString();
    }
    
    /*public boolean isCompleted() {
        for (MaintenanceTask maintenanceTask : maintenanceTasks)
            if (maintenanceTask.getClosed() == null)
                return false;
        
        return true;
    }*/

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
}
