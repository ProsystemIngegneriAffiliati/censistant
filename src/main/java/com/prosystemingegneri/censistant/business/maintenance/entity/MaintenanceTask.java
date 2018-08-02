/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryNotesSignatureForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
@MandatoryNotesSignatureForClosedMaintenanceTask
public class MaintenanceTask extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private MaintenanceContract maintenanceContract;
    
    @NotNull
    @ManyToOne(optional = false)
    private System system;
    
    @Lob
    @NotNull
    @Column(nullable = false)
    private String description;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    
    @NotNull
    @ManyToOne(optional = false)
    private Worker inChargeWorker;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isGuaranteeValid;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiry;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date closed;
    
    @Lob
    private String closingNotes;
    
    @Lob
    private String customerSignature;
    
    @ManyToMany
    private final List<MaintenancePayment> maintenancePayments;
    
    private String paymentNotes;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true)
    private final List<InspectionDone> inspectionsDone;
    
    @Version
    private int version;

    public MaintenanceTask() {
        creation = new Date();
        isGuaranteeValid = Boolean.FALSE;
        maintenancePayments = new ArrayList<>();
        inspectionsDone = new ArrayList<>();
    }

    public MaintenanceTask(System system) {
        this();
        this.system = system;
    }

    public MaintenanceContract getMaintenanceContract() {
        return maintenanceContract;
    }

    public void setMaintenanceContract(MaintenanceContract maintenanceContract) {
        this.maintenanceContract = maintenanceContract;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Worker getInChargeWorker() {
        return inChargeWorker;
    }

    public void setInChargeWorker(Worker inChargeWorker) {
        this.inChargeWorker = inChargeWorker;
    }

    public Boolean getIsGuaranteeValid() {
        return isGuaranteeValid;
    }

    public void setIsGuaranteeValid(Boolean isGuaranteeValid) {
        this.isGuaranteeValid = isGuaranteeValid;
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

    public String getClosingNotes() {
        return closingNotes;
    }

    public void setClosingNotes(String closingNotes) {
        this.closingNotes = closingNotes;
    }

    public String getCustomerSignature() {
        return customerSignature;
    }

    public void setCustomerSignature(String customerSignature) {
        this.customerSignature = customerSignature;
    }

    public String getPaymentNotes() {
        return paymentNotes;
    }

    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = paymentNotes;
    }

    @Override
    public Long getId() {
        return id;
    }

    public List<MaintenancePayment> getMaintenancePayments() {
        return maintenancePayments;
    }

    public void addInspectionDone(InspectionDone inspectionDone) {
        if (!inspectionsDone.contains(inspectionDone)) {
            inspectionsDone.add(inspectionDone);
            inspectionDone.setMaintenanceTask(this);
        }
    }
    
    public void removeInspectionDone(InspectionDone inspectionDone) {
        if (inspectionsDone.contains(inspectionDone)) {
            inspectionsDone.remove(inspectionDone);
            inspectionDone.setMaintenanceTask(null);
        }
    }
    
    public List<InspectionDone> getInspectionsDone() {
        return inspectionsDone;
    }

}
