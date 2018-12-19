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
import com.prosystemingegneri.censistant.business.purchasing.boundary.BoxService;
import com.prosystemingegneri.censistant.business.purchasing.boundary.SupplierItemService;
import com.prosystemingegneri.censistant.business.purchasing.entity.Box;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
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

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SupplierItemPresenter implements Serializable{
    @Inject
    SupplierItemService service;
    @Inject
    private BoxService boxService;
    
    private SupplierItem supplierItem;
    private Long id;
    
    @Inject
    private CustomerSupplierService customerSupplierService;
    private List<CustomerSupplier> suppliers;
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        supplierItem = (SupplierItem) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("supplierItem");
    }
    
    public String saveSupplierItem() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<SupplierItem> constraintViolation : validator.validate(supplierItem, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return "";
            
            service.saveSupplierItem(supplierItem);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/purchasing/supplierItems?faces-redirect=true";
    }
    
    public void detailSupplierItem() {
        if (supplierItem == null && id != null) {
            if (id == 0) {
                supplierItem = new SupplierItem();
                
                //Create a new boxedItem with default box
                supplierItem.setDescription(".");
                Box box = boxService.readBox(1L);
                supplierItem.addBoxedItem(new BoxedItem(box), box);
            }
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
    
    public List<CustomerSupplier> completeSupplier(String value) {
        suppliers = customerSupplierService.listCustomerSuppliers(0, 10, "name", Boolean.TRUE, null, null, null, Boolean.TRUE, null, value, null);
        return suppliers;
    }

    public List<CustomerSupplier> getSuppliers() {
        if (suppliers == null) {
            suppliers = new ArrayList<>();
            suppliers.add(supplierItem.getSupplier());
        }
        return suppliers;
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