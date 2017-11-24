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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class PurchaseOrderRowPresenter implements Serializable {
    private PurchaseOrderRow row;
    private PurchaseOrder purchaseOrder;
    
    @Inject
    private CustomerSupplierService service;
    @Inject
    private HandledItemService handledItemService;
    
    private CustomerSupplier supplier;
    private List<BoxedItem> items;
    
    @PostConstruct
    public void init() {
        purchaseOrder = (PurchaseOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("purchaseOrder");
        row = (PurchaseOrderRow) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("purchaseOrderRow");
        if (row == null)
            row = new PurchaseOrderRow();
        supplier = purchaseOrder.getSupplier();
    }
    
    public String savePurchaseOrderRow() {
        if (row.getPurchaseOrder() == null)
            purchaseOrder.addRow(row);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("purchaseOrder", purchaseOrder);
        
        return "/secured/purchasing/purchaseOrder?faces-redirect=true";
    }
    
    public List<HandledItem> listHandledItems() {
        return handledItemService.listHandledItems(0, 0, null, Boolean.FALSE, row);
    }
    
    public List<BoxedItem> completeItems(String filter) {
        return service.listSupplierBoxedItems(0, 10, supplier, filter);
    }

    public List<BoxedItem> getItems() {
        if (items == null || items.isEmpty())
            items = service.listSupplierBoxedItems(0, 0, supplier, null);
        
        return items;
    }
    
    public void updateSupplier(AjaxBehaviorEvent event) {
        items = null;
    }
    
    public String cancel() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("purchaseOrder", purchaseOrder);
        
        return "/secured/purchasing/purchaseOrder?faces-redirect=true";
    }

    public PurchaseOrderRow getRow() {
        return row;
    }

    public void setRow(PurchaseOrderRow row) {
        this.row = row;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public CustomerSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(CustomerSupplier supplier) {
        this.supplier = supplier;
    }
    
}