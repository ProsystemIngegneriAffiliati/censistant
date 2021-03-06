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
package com.prosystemingegneri.censistant.business.siteSurvey.entity;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class SiteSurveyReport extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer number;

    @NotNull
    @Column(nullable = false)    
    @Temporal(TemporalType.DATE)
    private Date expected;
    
    @Temporal(TemporalType.DATE)
    private Date actual;
    
    @NotNull
    @OneToOne(optional = false, orphanRemoval = true, cascade = CascadeType.MERGE)
    private SiteSurveyRequest request;
    
    @Temporal(TemporalType.DATE)
    private Date emailSent;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isOfferAccepted;
    
    @NotNull
    @ManyToOne(optional = false)
    private Plant plant;
    
    @NotNull
    @ManyToOne(optional = false)
    private Worker seller;
    
    @OneToOne(mappedBy = "siteSurveyReport")
    private Offer offer;
    
    private String notes;
    
    @Version
    private int version;

    public SiteSurveyReport() {
        isOfferAccepted = Boolean.FALSE;
    }

    public SiteSurveyReport(Integer number) {
        this();
        this.number = number;
    }
    
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getExpected() {
        return expected;
    }

    public void setExpected(Date expected) {
        this.expected = expected;
    }

    public Date getActual() {
        return actual;
    }

    public void setActual(Date actual) {
        this.actual = actual;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Worker getSeller() {
        return seller;
    }

    public void setSeller(Worker seller) {
        this.seller = seller;
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

    public SiteSurveyRequest getRequest() {
        return request;
    }

    public void addRequest(SiteSurveyRequest request) {
        request.setReport(this);
        this.request = request;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Date getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Date emailSent) {
        this.emailSent = emailSent;
    }

    public Boolean getIsOfferAccepted() {
        return isOfferAccepted;
    }

    public void setIsOfferAccepted(Boolean isOfferAccepted) {
        this.isOfferAccepted = isOfferAccepted;
    }
    
    public String getCustomerNameDataSystemType() {
        return new StringBuilder()
                .append(plant.getCustomerSupplier().getName())
                .append(" - ")
                .append(new SimpleDateFormat("dd/MM/yyyy").format(expected))
                .append(" - ")
                .append(request.getSystemType().getName())
                .toString();
    }
}
