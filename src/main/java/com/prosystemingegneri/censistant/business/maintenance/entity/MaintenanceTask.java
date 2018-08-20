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
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryInspectionsDoneForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryMaintenanceWorkerForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryNotesSignatureForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatoryPaymentsForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.MandatorySuitableForOperationForClosedMaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.control.SigGen;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
@MandatoryNotesSignatureForClosedMaintenanceTask
@MandatoryInspectionsDoneForClosedMaintenanceTask
@MandatoryPaymentsForClosedMaintenanceTask
@MandatorySuitableForOperationForClosedMaintenanceTask
@MandatoryMaintenanceWorkerForClosedMaintenanceTask
public class MaintenanceTask extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer number;
    
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
    
    @ManyToOne
    private Worker maintenanceWorker;
    
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
    
    @ManyToOne
    private PreventiveMaintenance preventiveMaintenance;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true)
    private final List<InspectionDone> inspectionsDone;
    
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true, optional = false)
    private TaskPrice taskPrice;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceTask", orphanRemoval = true)
    private final List<Replacement> replacements;
    
    private Boolean isSuitableForOperation;
    
    @Version
    private int version;

    public MaintenanceTask() {
        creation = new Date();
        isGuaranteeValid = Boolean.FALSE;
        maintenancePayments = new ArrayList<>();
        inspectionsDone = new ArrayList<>();
        replacements = new ArrayList<>();
    }

    public MaintenanceTask(System system, Integer number) {
        this();
        this.system = system;
        this.number = number;
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

    public PreventiveMaintenance getPreventiveMaintenance() {
        return preventiveMaintenance;
    }

    public void setPreventiveMaintenance(PreventiveMaintenance preventiveMaintenance) {
        this.preventiveMaintenance = preventiveMaintenance;
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

    public Boolean getIsSuitableForOperation() {
        return isSuitableForOperation;
    }

    public void setIsSuitableForOperation(Boolean isSuitableForOperation) {
        this.isSuitableForOperation = isSuitableForOperation;
    }
    
    //only for p:triStateCheckbox
    public String getIsSuitableForOperationStr() {
        if (isSuitableForOperation == null)
            return "0";
        else {
            if (isSuitableForOperation)
                return "1";
            else
                return "2";
        }
    }

    //only for p:triStateCheckbox
    public void setIsSuitableForOperationStr(String isSuitableForOperationStr) {
        if ("0".equals(isSuitableForOperationStr))
            isSuitableForOperation = null;
        else {
            if ("1".equals(isSuitableForOperationStr))
                isSuitableForOperation = Boolean.TRUE;
            else
                isSuitableForOperation = Boolean.FALSE;
        }
    }

    public Worker getMaintenanceWorker() {
        return maintenanceWorker;
    }

    public void setMaintenanceWorker(Worker maintenanceWorker) {
        this.maintenanceWorker = maintenanceWorker;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public java.awt.Image getCustomerSignatureImg() {
        try {
            ByteArrayOutputStream customerSignatureOut = new ByteArrayOutputStream();
            SigGen.generateSignature(customerSignature, customerSignatureOut);
            
            // take the copy of the stream and re-write it to an InputStream
            PipedInputStream customerSignatureIn = new PipedInputStream();
            final PipedOutputStream out = new PipedOutputStream(customerSignatureIn);
            new Thread(new Runnable() {
                public void run () {
                    try {
                        // write the original OutputStream to the PipedOutputStream
                        customerSignatureOut.writeTo(out);
                    } catch (IOException e) {
                        // logging and exception handling should go here
                    }
                }
            }).start();
            
            customerSignatureImg = ImageIO.read(customerSignatureIn);
        } catch (IOException ex) {
            return null;
        }
        
        return customerSignatureImg;
    }

    public void setCustomerSignatureImg(java.awt.Image customerSignatureImg) {
        this.customerSignatureImg = customerSignatureImg;
    }
    
}
