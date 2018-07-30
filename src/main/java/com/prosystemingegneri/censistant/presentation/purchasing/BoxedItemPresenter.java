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

import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class BoxedItemPresenter implements Serializable {
    private BoxedItem boxedItem;
    private SupplierItem supplierItem;
    
    @PostConstruct
    public void init() {
        supplierItem = (SupplierItem) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("supplierItem");
        boxedItem = (BoxedItem) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("boxedItem");
        if (boxedItem == null)
            boxedItem = new BoxedItem();
    }
    
    public String saveBoxedItem() {
        if (boxedItem.getItem() == null)
            supplierItem.addBoxedItem(boxedItem, boxedItem.getBox());
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("supplierItem", supplierItem);
        
        return "/secured/purchasing/supplierItem?faces-redirect=true";
    }
    
    public String cancel() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("supplierItem", supplierItem);
        
        return "/secured/purchasing/supplierItem?faces-redirect=true";
    }

    public BoxedItem getBoxedItem() {
        return boxedItem;
    }

    public void setBoxedItem(BoxedItem boxedItem) {
        this.boxedItem = boxedItem;
    }

    public SupplierItem getSupplierItem() {
        return supplierItem;
    }

    public void setSupplierItem(SupplierItem supplierItem) {
        this.supplierItem = supplierItem;
    }
    
}