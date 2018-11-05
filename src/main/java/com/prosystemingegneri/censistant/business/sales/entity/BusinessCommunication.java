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
package com.prosystemingegneri.censistant.business.sales.entity;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import java.util.Date;
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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class BusinessCommunication extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.DATE)
    private Date emailSent;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isOfferAccepted;
    
    @NotNull
    @ManyToOne(optional = false)
    private CustomerSupplier customer;
    
    @OneToOne
    private SiteSurveyReport report;
    
    @Version
    private int version;

    public BusinessCommunication() {
        isOfferAccepted = Boolean.FALSE;
    }

    public BusinessCommunication(CustomerSupplier customer) {
        this();
        this.customer = customer;
    }

    public BusinessCommunication(CustomerSupplier customer, SiteSurveyReport report) {
        this();
        this.customer = customer;
        this.report = report;
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
        if (this.isOfferAccepted)
            customer.setIsPotentialCustomer(Boolean.FALSE);
    }

    public CustomerSupplier getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSupplier customer) {
        this.customer = customer;
    }

    public SiteSurveyReport getReport() {
        return report;
    }

    public void setReport(SiteSurveyReport report) {
        this.report = report;
    }

    @Override
    public Long getId() {
        return id;
    }
    
}
