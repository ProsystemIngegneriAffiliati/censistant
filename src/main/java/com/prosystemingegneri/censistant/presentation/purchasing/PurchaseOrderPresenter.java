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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.purchasing.boundary.PurchaseOrderService;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class PurchaseOrderPresenter implements Serializable{
    @Inject
    PurchaseOrderService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    
    private PurchaseOrder purchaseOrder;
    private Long id;
    
    private CustomerSupplier supplier;
    
    @PostConstruct
    public void init() {
        purchaseOrder = (PurchaseOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("purchaseOrder");
        populateSupplier();
    }
    
    public String savePurchaseOrder() {
        try {
            service.savePurchaseOrder(purchaseOrder);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/purchasing/purchaseOrders?faces-redirect=true";
    }
    
    public void detailPurchaseOrder() {
        if (purchaseOrder == null && id != null) {
            if (id == 0)
                purchaseOrder = service.createNewPurchaseOrder();
            else {
                purchaseOrder = service.readPurchaseOrder(id);
                populateSupplier();
            }
        }
    }
    
    private void populateSupplier() {
        if (purchaseOrder != null && purchaseOrder.getPlant() != null)
            supplier = purchaseOrder.getPlant().getCustomerSupplier();
    }
    
    public String detailRow(PurchaseOrderRow row) {
        if (row != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("purchaseOrderRow", row);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("purchaseOrder", purchaseOrder);
        
        return "/secured/purchasing/purchaseOrderRow?faces-redirect=true";
    }
    
    public void deleteRow(PurchaseOrderRow row) {
        if (row != null)
            purchaseOrder.removeRow(row);
    }
    
    public List<Plant> completeSupplierPlant(String value) {
        return customerSupplierService.listPlants(0, 10, null, null, supplier, value, null);
    }
    
    public void onSupplierSelect(SelectEvent event) {
        if (event != null && event.getObject() != null) {
            CustomerSupplier tempSupplier = (CustomerSupplier) event.getObject();
            purchaseOrder.setPlant(tempSupplier.getHeadOffice());
        }
    }
    
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(CustomerSupplier supplier) {
        this.supplier = supplier;
    }
    
}