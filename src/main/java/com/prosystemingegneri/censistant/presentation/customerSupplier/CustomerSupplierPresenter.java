/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Referee;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
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
public class CustomerSupplierPresenter implements Serializable{
    @Inject
    CustomerSupplierService service;
    
    private CustomerSupplier customerSupplier;
    private Long id;
    private Boolean isCustomerView;
    
    @PostConstruct
    public void init() {
        customerSupplier = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        isCustomerView = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("isCustomerView");
    }
    
    public String saveCustomerSupplier() {
        try {
            service.saveCustomerSupplier(customerSupplier);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        String returnString;
        if (isCustomerView)
            returnString = "/secured/customerSupplier/customers";
        else
            returnString = "/secured/customerSupplier/suppliers";
        
        return returnString + "?faces-redirect=true";
    }
    
    public void detailCustomerSupplier() {
        if (customerSupplier == null && id != null) {
            if (id == 0) {
                customerSupplier = new CustomerSupplier();
                if (isCustomerView)
                    customerSupplier.setIsCustomer(Boolean.TRUE);
                else
                    customerSupplier.setIsSupplier(Boolean.TRUE);
            }
            else
                customerSupplier = service.readCustomerSupplier(id);
        }
    }
    
    public String detailPlant(Plant plant) {
        if (plant != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("plant", plant);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", isCustomerView);
        
        return "/secured/customerSupplier/plant?faces-redirect=true";
    }
    
    public void deletePlant(Plant plant) {
        if (plant != null)
            customerSupplier.removePlant(plant);
    }
    
    public String detailReferee(Referee referee) {
        if (referee != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("referee", referee);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", isCustomerView);
        
        return "/secured/customerSupplier/referee?faces-redirect=true";
    }
    
    public void deleteReferee(Referee referee) {
        if (referee != null)
            customerSupplier.removeReferee(referee);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getCustomerSupplier() {
        return customerSupplier;
    }

    public void setCustomerSupplier(CustomerSupplier customerSupplier) {
        this.customerSupplier = customerSupplier;
    }

    public Boolean getIsCustomerView() {
        return isCustomerView;
    }

    public void setIsCustomerView(Boolean isCustomerView) {
        this.isCustomerView = isCustomerView;
    }
    
}
