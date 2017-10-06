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

import com.prosystemingegneri.censistant.business.purchasing.boundary.SupplierItemService;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
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
public class SupplierItemPresenter implements Serializable{
    @Inject
    SupplierItemService service;
    
    private SupplierItem supplierItem;
    private Long id;
    
    @PostConstruct
    public void init() {
        supplierItem = (SupplierItem) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("supplierItem");
    }
    
    public String saveSupplierItem() {
        try {
            service.saveSupplierItem(supplierItem);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/purchasing/supplierItems?faces-redirect=true";
    }
    
    public void detailSupplierItem() {
        if (supplierItem == null && id != null) {
            if (id == 0)
                supplierItem = new SupplierItem();
            else
                supplierItem = service.readSupplierItem(id);
        }
    }
    
    public String detailBoxedItem(BoxedItem boxedItem) {
        if (boxedItem != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("boxedItem", boxedItem);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("supplierItem", supplierItem);
        
        return "/secured/purchasing/boxedItem?faces-redirect=true";
    }
    
    public void deleteBoxedItem(BoxedItem boxedItem) {
        if (boxedItem != null)
            supplierItem.removeBoxedItem(boxedItem);
    }
    
    public SupplierItem getSupplierItem() {
        return supplierItem;
    }

    public void setSupplierItem(SupplierItem supplierItem) {
        this.supplierItem = supplierItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}