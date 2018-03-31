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
import com.prosystemingegneri.censistant.business.purchasing.boundary.PurchaseOrderRowService;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import com.prosystemingegneri.censistant.presentation.purchasing.PurchaseOrderRowLazyDataModel;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

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
    HandledItemService handledItemService;
    
    private HandledItemLazyDataModel lazyHandledItems;
    private List<HandledItem> selectedHandledItems;
    
    @Inject
    PurchaseOrderRowService purchaseOrderRowService;
    
    private PurchaseOrderRowLazyDataModel lazyPurchaseOrderRows;
    private List<PurchaseOrderRow> selectedPurchaseOrderRows;
    private Map<Long, Integer> preparedQuantities;
    
    private Location locationDestination;
    
    @Inject
    Authenticator authenticator;
    @Inject
    WorkerService workerService;
    
    @PostConstruct
    public void init() {
        deliveryNote = (DeliveryNoteIn) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNoteIn");
        plant = (Plant) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("plant");
        lazyHandledItems = new HandledItemLazyDataModel(handledItemService, plant.getLocation(), null, Boolean.FALSE);
        lazyPurchaseOrderRows = new PurchaseOrderRowLazyDataModel(purchaseOrderRowService, plant, Boolean.TRUE);
        preparedQuantities = new HashMap<>();
    }
    
    public String createDeliveryNoteInRow() {
        if (selectedHandledItems != null && !selectedHandledItems.isEmpty()) {
            for (HandledItem selectedHandledItem : selectedHandledItems) {
                DeliveryNoteRow row = new DeliveryNoteRow();
                row.addHandledItem(selectedHandledItem);
                deliveryNote.addRow(row);
            }
        }
        
        if (selectedPurchaseOrderRows != null && !selectedPurchaseOrderRows.isEmpty()) {
            if (locationDestination != null) {
                for (PurchaseOrderRow selectedPurchaseOrderRow : selectedPurchaseOrderRows) {
                    HandledItem handledItem = new HandledItem();
                    handledItem.setBoxedItem(selectedPurchaseOrderRow.getBoxedItem());
                    handledItem.setFromLocation(plant.getLocation());
                    handledItem.setQuantity(preparedQuantities.getOrDefault(selectedPurchaseOrderRow.getId(), 0));
                    handledItem.setToLocation(locationDestination);
                    handledItem.setWorker(workerService.findWorker(null, false, authenticator.getLoggedUser()));

                    DeliveryNoteRow row = new DeliveryNoteRow();
                    row.addHandledItem(handledItem);

                    selectedPurchaseOrderRow.addDeliveryNoteRow(row);
                    
                    deliveryNote.addRow(row);
                }
            }
            else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select a destination"));
                return null;
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
    
    public void onTabChange(TabChangeEvent event) {
        if (selectedHandledItems != null)
            selectedHandledItems.clear();
        if (selectedPurchaseOrderRows != null)
            selectedPurchaseOrderRows.clear();
    }

    public void onPurchaseOrderRowsCellEdit(CellEditEvent event) {
        preparedQuantities.put(Long.parseLong(event.getRowKey()), (Integer) event.getNewValue());
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

    public PurchaseOrderRowLazyDataModel getLazyPurchaseOrderRows() {
        return lazyPurchaseOrderRows;
    }

    public void setLazyPurchaseOrderRows(PurchaseOrderRowLazyDataModel lazyPurchaseOrderRows) {
        this.lazyPurchaseOrderRows = lazyPurchaseOrderRows;
    }

    public List<PurchaseOrderRow> getSelectedPurchaseOrderRows() {
        return selectedPurchaseOrderRows;
    }

    public void setSelectedPurchaseOrderRows(List<PurchaseOrderRow> selectedPurchaseOrderRows) {
        this.selectedPurchaseOrderRows = selectedPurchaseOrderRows;
    }

    public Location getLocationDestination() {
        return locationDestination;
    }

    public void setLocationDestination(Location locationDestination) {
        this.locationDestination = locationDestination;
    }
    
}