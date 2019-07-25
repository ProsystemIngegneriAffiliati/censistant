/*
 * Copyright (C) 2017-2019 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.sales.entity;

import com.prosystemingegneri.censistant.business.control.SignatureImageConverter;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Referee;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.awt.Image;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class JobOrder extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creation;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer number;
    
    @NotNull
    @OneToOne(optional = false)
    private Offer offer;
    
    private String customerOrderNumber;
    
    @NotNull
    @ManyToOne(optional = false)
    private PlaceType placeType;
    
    @ManyToMany
    private List<Referee> referees;
    
    @ManyToMany
    private List<Worker> workers;
    
    @Temporal(TemporalType.DATE)
    private Date installation;
    
    @Temporal(TemporalType.DATE)
    private Date expectedInstallation;
    
    private Integer expectedManHours;
    
    private Integer expectedWorkers;
    
    @Lob
    private String workerSignature;
    
    @Transient
    private java.awt.Image workerSignatureImg;
    
    @Lob
    private String customerSignature;
    
    @Transient
    private java.awt.Image customerSignatureImg;
    
    @Lob
    private String notes;
    
    @Version
    private int version;

    public JobOrder() {
        creation = new Date();
        installation = new Date();
        workers = new ArrayList<>();
        referees = new ArrayList<>();
    }

    public JobOrder(Integer number) {
        this();
        this.number = number;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCustomerOrderNumber() {
        return customerOrderNumber;
    }

    public void setCustomerOrderNumber(String customerOrderNumber) {
        this.customerOrderNumber = customerOrderNumber;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
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

    public Offer getOffer() {
        return offer;
    }

    public void addOffer(Offer offer) {
        offer.setJobOrder(this);
        this.offer = offer;
    }
    
    public String getNumberStr() {
        return new StringBuilder(offer.getSiteSurveyReport().getRequest().getSystemType().getSymbol())
                .append(" ")
                .append(new SimpleDateFormat("yy").format(creation))
                .append("/")
                .append(new SimpleDateFormat("MM").format(creation))
                .append("/")
                .append(new DecimalFormat("0000").format(number))
                .toString();
    }
    
    public String getJobOrderNumberAddress() {
        return new StringBuilder(getNumberStr())
                .append(" - ")
                .append(offer.getSiteSurveyReport().getPlant().getNameAddress(false))
                .toString();
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public void setReferees(List<Referee> referees) {
        this.referees = referees;
    }

    public Date getInstallation() {
        return installation;
    }

    public void setInstallation(Date installation) {
        this.installation = installation;
    }

    public String getWorkerSignature() {
        return workerSignature;
    }

    public void setWorkerSignature(String workerSignature) {
        this.workerSignature = workerSignature;
    }

    public Image getWorkerSignatureImg() {
        workerSignatureImg = SignatureImageConverter.convertoToImage(workerSignature);
        return workerSignatureImg;
    }

    public void setWorkerSignatureImg(Image workerSignatureImg) {
        this.workerSignatureImg = workerSignatureImg;
    }

    public String getCustomerSignature() {
        return customerSignature;
    }

    public void setCustomerSignature(String customerSignature) {
        this.customerSignature = customerSignature;
    }

    public Image getCustomerSignatureImg() {
        customerSignatureImg = SignatureImageConverter.convertoToImage(customerSignature);
        return customerSignatureImg;
    }

    public void setCustomerSignatureImg(Image customerSignatureImg) {
        this.customerSignatureImg = customerSignatureImg;
    }
    
    public String getWorkersStr() {
        StringBuilder result = new StringBuilder();
        String separator = " - ";
        
        for (Worker worker : workers)
            result = result
                    .append(worker.getInitials())
                    .append(separator);
        if (result.length() > 0)
            result = result.delete(result.length() - separator.length(), result.length());
        
        return result.toString();
    }

    public Date getExpectedInstallation() {
        return expectedInstallation;
    }

    public void setExpectedInstallation(Date expectedInstallation) {
        this.expectedInstallation = expectedInstallation;
    }

    public Integer getExpectedManHours() {
        return expectedManHours;
    }

    public void setExpectedManHours(Integer expectedManHours) {
        this.expectedManHours = expectedManHours;
    }

    public Integer getExpectedWorkers() {
        return expectedWorkers;
    }

    public void setExpectedWorkers(Integer expectedWorkers) {
        this.expectedWorkers = expectedWorkers;
    }
    
}
