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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.sales.entity.BusinessCommunication;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.presentation.customerSupplier.CustomerSupplierPresenter;
import com.prosystemingegneri.censistant.presentation.siteSurvey.SiteSurveyReportPresenter;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@Dependent
public class BusinessCommunicationPresenter implements Serializable {
    @Inject
    private CustomerSupplierPresenter customerSupplierPresenter;
    @Inject
    private SiteSurveyReportPresenter siteSurveyReportPresenter;
    
    private BusinessCommunication businessCommunication;
    
    @PostConstruct
    public void init() {
        if (customerSupplierPresenter.getCustomerSupplier() != null)
            businessCommunication = customerSupplierPresenter.getCustomerSupplier().getLastBusinessCommunication();
    }
    

    public BusinessCommunication getBusinessCommunication() {
        return businessCommunication;
    }

    public void setBusinessCommunication(BusinessCommunication businessCommunication) {
        this.businessCommunication = businessCommunication;
    }
    
}