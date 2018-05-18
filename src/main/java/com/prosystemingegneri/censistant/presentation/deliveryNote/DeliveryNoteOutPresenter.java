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
package com.prosystemingegneri.censistant.presentation.deliveryNote;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.deliveryNote.boundary.DeliveryNoteOutService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class DeliveryNoteOutPresenter implements Serializable{
    @Inject
    DeliveryNoteOutService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    
    private DeliveryNoteOut deliveryNoteOut;
    private Long id;
    
    private Integer activeIndex;    //useful for keep tab opened when reloading a page
    
    private CustomerSupplier customerSupplierTemp;
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        deliveryNoteOut = (DeliveryNoteOut) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
        
        if (activeIndex == null)
            activeIndex = 0;
        
        populateCustomerSupplierAndPlant();
    }
    
    public String saveDeliveryNoteOut() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<DeliveryNoteOut> constraintViolation : validator.validate(deliveryNoteOut, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return null;
            
            service.saveDeliveryNoteOut(deliveryNoteOut);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/deliveryNote/deliveryNoteOuts?faces-redirect=true";
    }
    
    public void detailDeliveryNoteOut() {
        if (deliveryNoteOut == null && id != null) {
            if (id == 0)
                deliveryNoteOut = service.createNewDeliveryNoteOut();
            else {
                deliveryNoteOut = service.readDeliveryNoteOut(id);
                populateCustomerSupplierAndPlant();
            }
        }
    }
    
    private void populateCustomerSupplierAndPlant() {
        customerSupplierTemp = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        if (customerSupplierTemp == null && deliveryNoteOut != null && deliveryNoteOut.getPlant() != null)
            customerSupplierTemp = deliveryNoteOut.getPlant().getCustomerSupplier();
    }
    
    public String createNewCarrier() {
        putExternalContext();
        
        return "/secured/deliveryNote/carrier?faces-redirect=true";
    }

    public String createNewCustomer() {
        return prepareForOpeningCustomerSupplier(customerSupplierService.createCustomer(), true);
    }
    
    public String createNewSupplier() {
        return prepareForOpeningCustomerSupplier(customerSupplierService.createSupplier(), false);
    }

    public String openCustomerSupplier() {
        if (!customerSupplierTemp.getIsCustomer())  //if it's not a customer, open supplier window
            return prepareForOpeningCustomerSupplier(customerSupplierTemp, false);
        if (!customerSupplierTemp.getIsSupplier())  //if it's not a supplier, open customer window
            return prepareForOpeningCustomerSupplier(customerSupplierTemp, true);
        
        return prepareForOpeningCustomerSupplier(customerSupplierTemp, true);   //if it's a customer and a supplier, open customer window
    }
    
    private String prepareForOpeningCustomerSupplier(CustomerSupplier customerSupplier, boolean isCustomer) {
        putExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", isCustomer);
        
        if (isCustomer)
            return "/secured/customerSupplier/customer?faces-redirect=true";
        else
            return "/secured/customerSupplier/supplier?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "deliveryNote/deliveryNoteOut");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNoteOut);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
    }
    
    public List<Plant> completePlant(String value) {
        List<Plant> result = new ArrayList<>();
        
        if (customerSupplierTemp != null)
            for (Plant plant : customerSupplierTemp.getPlants())
                if (plant.getName().toLowerCase().contains(value.toLowerCase()))
                    result.add(plant);
        
        return result;
    }
    
    public String creteNewRow() {
        if (deliveryNoteOut.getPlant() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNoteOut);
            return "/secured/deliveryNote/deliveryNoteOutRowCreation?faces-redirect=true";
        }
        
        return null;
    }
    
    public String detailRow(DeliveryNoteRow row) {
        if (row != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteRow", row);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNoteOut);
        
        return "/secured/deliveryNote/deliveryNoteOutRow?faces-redirect=true";
    }
    
    public void deleteRow(DeliveryNoteRow row) {
        if (row != null)
            deliveryNoteOut.removeRow(row);
    }

    public DeliveryNoteOut getDeliveryNoteOut() {
        return deliveryNoteOut;
    }

    public void setDeliveryNoteOut(DeliveryNoteOut deliveryNoteOut) {
        this.deliveryNoteOut = deliveryNoteOut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getCustomerSupplierTemp() {
        return customerSupplierTemp;
    }

    public void setCustomerSupplierTemp(CustomerSupplier customerSupplierTemp) {
        this.customerSupplierTemp = customerSupplierTemp;
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }
    
}