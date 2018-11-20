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
package com.prosystemingegneri.censistant.presentation.siteSurvey;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyRequestService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SiteSurveyRequestPresenter implements Serializable{
    @Inject
    SiteSurveyRequestService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    
    private List<CustomerSupplier> customers;
    
    private SiteSurveyRequest siteSurveyRequest;
    private Long id;
    
    @PostConstruct
    public void init() {
        siteSurveyRequest = (SiteSurveyRequest) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyRequest");
        if (siteSurveyRequest != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0)
                siteSurveyRequest.setCustomer(customerSupplierService.readCustomerSupplier(idCustomer));
        }
    }
    
    public String saveSiteSurveyRequest() {
        try {
            service.saveSiteSurveyRequest(siteSurveyRequest);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/siteSurvey/siteSurveyRequests?faces-redirect=true";
    }
    
    public void detailSiteSurveyRequest() {
        if (siteSurveyRequest == null && id != null) {
            if (id == 0)
                siteSurveyRequest = service.createNewSiteSurveyRequest();
            else
                siteSurveyRequest = service.readSiteSurveyRequest(id);
        }
    }
    
    public String createPotentialCustomer() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyRequest", siteSurveyRequest);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplierService.createPotentialCustomer());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.TRUE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "siteSurvey/siteSurveyRequest");
        
        return "/secured/customerSupplier/customer?faces-redirect=true";
    }
    
    public List<CustomerSupplier> completeCustomer(String value) {
        customers = customerSupplierService.listCustomerSuppliers(0, 10, "name", Boolean.TRUE, null, null, Boolean.TRUE, null, null, value, null);
        return customers;
    }

    public List<CustomerSupplier> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
            customers.add(siteSurveyRequest.getCustomer());
        }
        return customers;
    }

    public SiteSurveyRequest getSiteSurveyRequest() {
        return siteSurveyRequest;
    }

    public void setSiteSurveyRequest(SiteSurveyRequest siteSurveyRequest) {
        this.siteSurveyRequest = siteSurveyRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}