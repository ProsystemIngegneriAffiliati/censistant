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

import com.prosystemingegneri.censistant.business.control.SignatureImageConverter;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryMaintenanceWorkerForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryNotesSignatureForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryPaymentsForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatorySuitableForOperationForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatorySystemOrMaintenancePlan;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryVatForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.SuitableForOperation;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
@MandatorySystemOrMaintenancePlan
@MandatoryVatForClosedMaintenanceTask
@MandatoryNotesSignatureForClosedMaintenanceTask
@MandatoryPaymentsForClosedMaintenanceTask
@MandatorySuitableForOperationForClosedMaintenanceTask
@MandatoryMaintenanceWorkerForClosedMaintenanceTask
public class MaintenanceTask extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private MaintenancePlan maintenancePlan;
    
    @ManyToOne
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
    
    @ManyToMany
    private Set<Worker> maintenanceWorkers;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isGuaranteeValid;
    
    @Temporal(TemporalType.DATE)
    private Date expiry;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date closed;
    
    @Lob
    private String closingNotes;
    
    @Lob
    private String customerSignature;
    
    @Transient
    private java.awt.Image customerSignatureImg;
    
    @ManyToMany
    private List<MaintenancePayment> maintenancePayments;
    
    private String paymentNotes;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true)
    private final List<InspectionDone> inspectionsDone;
    
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true, optional = false)
    private TaskPrice taskPrice;
    
    @ManyToOne
    private Vat vat;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true)
    private final List<Replacement> replacements;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private SuitableForOperation suitableForOperation;
    
    @Version
    private int version;

    public MaintenanceTask() {
        creation = new Date();
        isGuaranteeValid = Boolean.FALSE;
        maintenancePayments = new ArrayList<>();
        inspectionsDone = new ArrayList<>();
        replacements = new ArrayList<>();
        maintenanceWorkers = new TreeSet<>();
    }

    public MaintenanceTask(System system) {
        this();
        this.system = system;
    }

    public MaintenanceTask(MaintenancePlan maintenancePlan) {
        this();
        this.maintenancePlan = maintenancePlan;
    }

    public MaintenancePlan getMaintenancePlan() {
        return maintenancePlan;
    }

    public void setMaintenancePlan(MaintenancePlan maintenancePlan) {
        this.maintenancePlan = maintenancePlan;
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

    public void setMaintenancePayments(List<MaintenancePayment> maintenancePayments) {
        this.maintenancePayments = maintenancePayments;
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
    
    public void addTaskPrice(TaskPrice taskPrice) {
        removeTaskPrice();  //so it reaplace with new task price
        this.taskPrice = taskPrice;
        taskPrice.setMaintenanceTask(this);
    }
    
    public void removeTaskPrice() {
        if (taskPrice != null) {
            taskPrice.setMaintenanceTask(null);
            taskPrice = null;
        }
    }

    public TaskPrice getTaskPrice() {
        return taskPrice;
    }
    
    public void addReplacement(Replacement replacement) {
        if (!replacements.contains(replacement)) {
            replacements.add(replacement);
            replacement.setMaintenanceTask(this);
        }
    }
    
    public void removeReplacement(Replacement replacement) {
        if (replacements.contains(replacement)) {
            replacements.remove(replacement);
            replacement.setMaintenanceTask(null);
        }
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }
    
    public BigDecimal getReplacementsPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (Replacement replacement : replacements)
            result = result.add(replacement.getTotalPrice());
        
        return result;
    }
    
    public BigDecimal getPrice() {
        return getReplacementsPrice().add(taskPrice.getPrice());
    }
    
    public BigDecimal getPriceWithVat() {
        if (vat != null)
            return getPrice().multiply(BigDecimal.ONE.add(vat.getPercent()));
        else
            return getPrice();
    }
    
    public List<HandledItem> getReplacementHandledItems() {
        List<HandledItem> result = new ArrayList<>();
        
        for (Replacement replacement : replacements)
            result.add(replacement.getHandledItem());
        
        return result;
    }
    
    public void setReplacementHandledItems(List<HandledItem> replacementHandledItems) {
        //just for handledItemsTable selection
    }

    public boolean isHandledItemPresent(HandledItem handledItem) {
        for (Replacement replacement : replacements)
            if (replacement.getHandledItem().getId().equals(handledItem.getId()))
                return true;
        
        return false;
    }

    public SuitableForOperation getSuitableForOperation() {
        return suitableForOperation;
    }

    public void setSuitableForOperation(SuitableForOperation suitableForOperation) {
        this.suitableForOperation = suitableForOperation;
    }

    public Set<Worker> getMaintenanceWorkers() {
        return maintenanceWorkers;
    }

    public void setMaintenanceWorkers(Set<Worker> maintenanceWorkers) {
        this.maintenanceWorkers = maintenanceWorkers;
    }

    public java.awt.Image getCustomerSignatureImg() {
        customerSignatureImg = SignatureImageConverter.convertoToImage(customerSignature);
        return customerSignatureImg;
    }

    public void setCustomerSignatureImg(java.awt.Image customerSignatureImg) {
        this.customerSignatureImg = customerSignatureImg;
    }

    public Vat getVat() {
        return vat;
    }

    public void setVat(Vat vat) {
        this.vat = vat;
    }
    
    public boolean isTaskClosed() {
        return suitableForOperation != null && suitableForOperation != SuitableForOperation.SUSPENDED && closed != null;
    }
    
    public boolean isFullService() {
        if (maintenancePlan != null)
            for (MaintenancePlan maintenancePlan1 : maintenancePlan.getContractedSystem().getMaintenancePlans())
                if (maintenancePlan1.getMaintenanceType() == MaintenanceType.FULL_SERVICE)
                    return true;
        
        return false;
    }
    
    public String getMaintenanceWorkersStr() {
        StringBuilder result = new StringBuilder();
        String separator = " - ";
        
        for (Worker maintenanceWorker : maintenanceWorkers)
            result = result
                    .append(maintenanceWorker.getInitials())
                    .append(separator);
        if (result.length() > 0)
            result = result.delete(result.length() - separator.length(), result.length());
        
        return result.toString();
    }
    
    public String getMaintenanceTypesStr() {
        StringBuilder result = new StringBuilder();
        
        if (maintenancePlan != null) {
            for (MaintenancePlan tempMaintenancePlan : maintenancePlan.getContractedSystem().getMaintenancePlans()) {
                switch (tempMaintenancePlan.getMaintenanceType()) {
                    case FULL_SERVICE:
                        result = result.append("G");
                        break;
                    case MAINTENANCE:
                        break;
                    case ON_CALL:
                        break;
                    case PREVENTIVE_MAINTENANCE:
                        result = result.append("M");
                        break;
                    case TELECONTROL:
                        result = result.append("T");
                        break;
                    default:
                }
            }
        }
        
        return result.toString();
    }
}
