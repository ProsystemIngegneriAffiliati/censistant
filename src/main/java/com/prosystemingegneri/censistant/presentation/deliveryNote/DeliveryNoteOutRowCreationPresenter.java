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

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class DeliveryNoteOutRowCreationPresenter implements Serializable {
    private DeliveryNoteOut deliveryNote;
    
    @Inject
    HandledItemService handledItemService;
    
    private HandledItemLazyDataModel lazyHandledItems;
    private List<HandledItem> selectedHandledItems;
    
    private Location locationDestination;
    
    @Inject
    private CustomerSupplierService customerSupplierService;
    private List<BoxedItem> boxedItems;
    
    @Inject
    Authenticator authenticator;
    @Inject
    WorkerService workerService;
    
    @PostConstruct
    public void init() {
        deliveryNote = (DeliveryNoteOut) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        lazyHandledItems = new HandledItemLazyDataModel(handledItemService, null, deliveryNote.getLocation(), Boolean.FALSE);
    }
    
    public String createDeliveryNoteOutRow() {
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
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNote);
        
        return "/secured/deliveryNote/deliveryNoteOut?faces-redirect=true";
    }
    
    public void onTabChange(TabChangeEvent event) {
        if (selectedHandledItems != null)
            selectedHandledItems.clear();
        locationDestination = null;
    }
    
    public List<BoxedItem> completeBoxedItems(String filter) {
        return customerSupplierService.listSupplierBoxedItems(0, 10, deliveryNote.getLocation().getCustomerSupplier(), filter);
    }

    public List<BoxedItem> getBoxedItems() {
        if (boxedItems == null || boxedItems.isEmpty())
            boxedItems = customerSupplierService.listSupplierBoxedItems(0, 0, deliveryNote.getLocation().getCustomerSupplier(), null);
        
        return boxedItems;
    }
    
    public DeliveryNoteOut getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNoteOut deliveryNote) {
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

    public Location getLocationDestination() {
        return locationDestination;
    }

    public void setLocationDestination(Location locationDestination) {
        this.locationDestination = locationDestination;
    }
    
}