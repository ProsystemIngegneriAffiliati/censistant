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
import com.prosystemingegneri.censistant.business.deliveryNote.boundary.DeliveryNoteInService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class DeliveryNoteInPresenter implements Serializable{
    @Inject
    DeliveryNoteInService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    
    private DeliveryNoteIn deliveryNoteIn;
    private Long id;
    
    private CustomerSupplier supplierTemp;
    private Plant plantTemp;
    
    @PostConstruct
    public void init() {
        deliveryNoteIn = (DeliveryNoteIn) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNoteIn");
        
        populateCustomerSupplierAndPlant();
    }
    
    public String saveDeliveryNoteIn() {
        try {
            service.saveDeliveryNoteIn(deliveryNoteIn);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/deliveryNote/deliveryNoteIns?faces-redirect=true";
    }
    
    public void detailDeliveryNoteIn() {
        if (deliveryNoteIn == null && id != null) {
            if (id == 0)
                deliveryNoteIn = service.createNewDeliveryNoteIn();
            else {
                deliveryNoteIn = service.readDeliveryNoteIn(id);
                populateCustomerSupplierAndPlant();
            }
        }
    }
    
    private void populateCustomerSupplierAndPlant() {
        supplierTemp = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        if (supplierTemp != null)
            plantTemp = supplierTemp.getPlants().get(supplierTemp.getPlants().size() - 1);
        else
            if (deliveryNoteIn != null && !deliveryNoteIn.getRows().isEmpty() && deliveryNoteIn.getRows().get(0).getHandledItem().getFromLocation() instanceof SupplierPlantLocation) {
                plantTemp = ((SupplierPlantLocation) deliveryNoteIn.getRows().get(0).getHandledItem().getFromLocation()).getPlant();
                supplierTemp = plantTemp.getCustomerSupplier();
            }
    }
    
    public String createNewSupplier() {
        return prepareForOpeningSupplier(customerSupplierService.createSupplier());
    }
    
    public String openSupplier() {
        return prepareForOpeningSupplier(supplierTemp);
    }
    
    private String prepareForOpeningSupplier(CustomerSupplier supplier) {
        putExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", supplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.FALSE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "deliveryNote/deliveryNoteIn");
        
        return "/secured/customerSupplier/supplier?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteIn", deliveryNoteIn);
    }
    
    public void onSupplierSelect(SelectEvent event) {
        if (event != null && event.getObject() != null)
            plantTemp = ((CustomerSupplier) event.getObject()).getHeadOffice();
    }
    
    public List<Plant> completePlant(String value) {
        List<Plant> result = new ArrayList<>();
        
        if (supplierTemp != null)
            for (Plant plant : supplierTemp.getPlants())
                if (plant.getName().toLowerCase().contains(value.toLowerCase()))
                    result.add(plant);
        
        return result;
    }
    
    public String creteNewRow() {
        if (plantTemp != null) {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteIn", deliveryNoteIn);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("plant", plantTemp);
            return "/secured/deliveryNote/deliveryNoteInRowCreation?faces-redirect=true";
        }
        
        return null;
    }
    
    public String detailRow(DeliveryNoteRow row) {
        if (row != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteRow", row);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteIn", deliveryNoteIn);
        
        return "/secured/deliveryNote/deliveryNoteInRow?faces-redirect=true";
    }
    
    public void deleteRow(DeliveryNoteRow row) {
        if (row != null)
            deliveryNoteIn.removeRow(row);
    }

    public DeliveryNoteIn getDeliveryNoteIn() {
        return deliveryNoteIn;
    }

    public void setDeliveryNoteIn(DeliveryNoteIn deliveryNoteIn) {
        this.deliveryNoteIn = deliveryNoteIn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getSupplierTemp() {
        return supplierTemp;
    }

    public void setSupplierTemp(CustomerSupplier supplierTemp) {
        this.supplierTemp = supplierTemp;
    }

    public Plant getPlantTemp() {
        return plantTemp;
    }

    public void setPlantTemp(Plant plantTemp) {
        this.plantTemp = plantTemp;
    }
    
}