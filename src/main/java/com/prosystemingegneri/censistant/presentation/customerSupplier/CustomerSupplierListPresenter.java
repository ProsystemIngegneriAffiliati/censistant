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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyRequestService;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
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
public class CustomerSupplierListPresenter implements Serializable{
    @Inject
    CustomerSupplierService service;
    
    private CustomerSupplierLazyDataModel lazyCustomers;
    private CustomerSupplierLazyDataModel lazySuppliers;
    private List<CustomerSupplier> selectedCustomerSuppliers;
    
    private Boolean isPotentialCustomer;
    
    @Inject
    private SiteSurveyRequestService siteSurveyRequestService;
    
    @PostConstruct
    public void init() {
        lazyCustomers = new CustomerSupplierLazyDataModel(service, Boolean.TRUE, null, isPotentialCustomer);
        lazySuppliers = new CustomerSupplierLazyDataModel(service, null, Boolean.TRUE, null);
    }
    
    public void deleteCustomerSupplier() {
        if (selectedCustomerSuppliers != null && !selectedCustomerSuppliers.isEmpty()) {
            for (CustomerSupplier customerSupplierTemp : selectedCustomerSuppliers) {
                try {
                    service.deleteCustomerSupplier(customerSupplierTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public CustomerSupplierLazyDataModel getLazyCustomers() {
        return lazyCustomers;
    }

    public void setLazyCustomers(CustomerSupplierLazyDataModel lazyCustomers) {
        this.lazyCustomers = lazyCustomers;
    }

    public CustomerSupplierLazyDataModel getLazySuppliers() {
        return lazySuppliers;
    }

    public void setLazySuppliers(CustomerSupplierLazyDataModel lazySuppliers) {
        this.lazySuppliers = lazySuppliers;
    }

    public List<CustomerSupplier> getSelectedCustomerSuppliers() {
        return selectedCustomerSuppliers;
    }

    public void setSelectedCustomerSuppliers(List<CustomerSupplier> selectedCustomerSupplier) {
        this.selectedCustomerSuppliers = selectedCustomerSupplier;
    }

    public Boolean getIsPotentialCustomer() {
        return isPotentialCustomer;
    }

    public void setIsPotentialCustomer(Boolean isPotentialCustomer) {
        this.isPotentialCustomer = isPotentialCustomer;
    }
    
}
