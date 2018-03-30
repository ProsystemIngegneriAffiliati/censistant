/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class DeliveryNoteInRowCreationPresenter implements Serializable {
    private DeliveryNoteIn deliveryNote;
    
    private Plant plant;
    
    @Inject
    HandledItemService service;
    
    private HandledItemLazyDataModel lazyHandledItems;
    private List<HandledItem> selectedHandledItems;
    
    @PostConstruct
    public void init() {
        deliveryNote = (DeliveryNoteIn) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNoteIn");
        plant = (Plant) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("plant");
        lazyHandledItems = new HandledItemLazyDataModel(service, plant.getLocation(), null, Boolean.FALSE);
    }
    
    public String createDeliveryNoteInRow() {
        if (selectedHandledItems != null && !selectedHandledItems.isEmpty()) {
            for (HandledItem selectedHandledItem : selectedHandledItems) {
                DeliveryNoteRow row = new DeliveryNoteRow();
                row.addHandledItem(selectedHandledItem);
                deliveryNote.addRow(row);
            }
        }
        
        return putExternalContextAndReturnToPage();
    }
    
    public String cancel() {
        return putExternalContextAndReturnToPage();
    }
    
    private String putExternalContextAndReturnToPage() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNoteIn", deliveryNote);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("plant", plant);
        
        return "/secured/deliveryNote/deliveryNoteIn?faces-redirect=true";
    }

    public DeliveryNoteIn getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNoteIn deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public HandledItemLazyDataModel getLazyHandledItems() {
        return lazyHandledItems;
    }

    public void setLazyHandledItems(HandledItemLazyDataModel lazyHandledItems) {
        this.lazyHandledItems = lazyHandledItems;
    }

    public List<HandledItem> getSelectedHandledItems() {
        return selectedHandledItems;
    }

    public void setSelectedHandledItems(List<HandledItem> selectedHandledItems) {
        this.selectedHandledItems = selectedHandledItems;
    }
    
}