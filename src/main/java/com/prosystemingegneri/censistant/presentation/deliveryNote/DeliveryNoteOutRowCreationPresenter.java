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
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.StockService;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import com.prosystemingegneri.censistant.presentation.warehouse.StockLazyDataModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
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
    
    @Inject
    private CustomerSupplierService customerSupplierService;
    private List<BoxedItem> boxedItems;
    private List<HandledItem> tempHandledItems;
    
    @Inject
    StockService stockService;
    private StockLazyDataModel lazyStock;
    private List<Stock> selectedStock;
    
    private List<Stock> preparedStockForMovement;
    private HashMap<String, Integer> preparedIdStockForMovement;    //only for checking before insertion in 'preparedStockForMovement' list
    
    @Inject
    Authenticator authenticator;
    @Inject
    WorkerService workerService;
    
    @PostConstruct
    public void init() {
        deliveryNote = (DeliveryNoteOut) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        lazyHandledItems = new HandledItemLazyDataModel(handledItemService, null, deliveryNote.getLocation(), Boolean.FALSE);
        tempHandledItems = new ArrayList<>();
        lazyStock = new StockLazyDataModel(stockService);
        initPreparedStock();
    }
    
    public String createDeliveryNoteOutRow() {
        if (selectedHandledItems != null && !selectedHandledItems.isEmpty()) {
            for (HandledItem selectedHandledItem : selectedHandledItems) {
                DeliveryNoteRow row = new DeliveryNoteRow();
                row.addHandledItem(selectedHandledItem);
                deliveryNote.addRow(row);
            }
        }
        if (preparedStockForMovement != null && !preparedStockForMovement.isEmpty()) {
            for (Stock stock : preparedStockForMovement) {
                HandledItem tempHandledItem = new HandledItem();
                tempHandledItem.setBoxedItem(stock.getBoxedItem());
                tempHandledItem.setFromLocation(stock.getLocation());
                tempHandledItem.setToLocation(deliveryNote.getLocation());
                tempHandledItem.setWorker(workerService.findWorker(null, false, authenticator.getLoggedUser()));
                
                Integer quantityMoved;
                if (stock.getUnitMeasure().equals(stock.getNakedUnitMeasure()))
                    quantityMoved = stock.getQuantity();
                else
                    quantityMoved = stock.getQuantity() * stock.getBoxedItem().getBox().getQuantity();
                tempHandledItem.setQuantity(quantityMoved);
                
                DeliveryNoteRow row = new DeliveryNoteRow();
                row.addHandledItem(tempHandledItem);
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
        setLocationOrigin(null);
        initPreparedStock();
    }
    
    public List<BoxedItem> completeBoxedItems(String filter) {
        return customerSupplierService.listSupplierBoxedItems(0, 10, null, filter);
    }

    public List<BoxedItem> getBoxedItems() {
        if (boxedItems == null || boxedItems.isEmpty())
            boxedItems = customerSupplierService.listSupplierBoxedItems(0, 0, null, null);
        
        return boxedItems;
    }
    
    public void createNewHandledItem() {
        tempHandledItems.add(new HandledItem());
    }
    
    private void initPreparedStock() {
        preparedStockForMovement = new ArrayList<>();
        preparedIdStockForMovement = new HashMap<>();
    }
    
    private void initStockAfterSuccessfulMovement() {
        lazyStock.setLocation(null);
        selectedStock = null;
        initPreparedStock();
    }
    
    public void onWarehouseStockSelect(SelectEvent event) {
        Stock tempStock = (Stock) event.getObject();
        if (!preparedIdStockForMovement.containsKey(tempStock.getLocationIdBoxedItemId())) {
            preparedStockForMovement.add(tempStock);
            preparedIdStockForMovement.put(tempStock.getLocationIdBoxedItemId(), 0);
        }
    }
    
    public void onCellEdit(CellEditEvent event) {
        if (event.getOldValue() instanceof UnitMeasure) {
            Stock stock = preparedStockForMovement.get(event.getRowIndex());
            if (!stock.getUnitMeasure().equals(event.getOldValue())) {
                if (stock.getNakedUnitMeasure().equals(event.getOldValue()))
                    stock.setQuantity(stock.getBoxedQuantity());
                else
                    stock.setQuantity(stock.getNakedQuantity());
            }
        }
    }
    
    public void onLocationOriginSelect(SelectEvent event) {
        initPreparedStock();
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

    public Location getLocationOrigin() {
        return lazyStock.getLocation();
    }

    public void setLocationOrigin(Location locationOrigin) {
        lazyStock.setLocation(locationOrigin);
    }

    public List<HandledItem> getTempHandledItems() {
        return tempHandledItems;
    }

    public StockLazyDataModel getLazyStock() {
        return lazyStock;
    }

    public List<Stock> getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(List<Stock> selectedStock) {
        this.selectedStock = selectedStock;
    }

    public List<Stock> getPreparedStockForMovement() {
        return preparedStockForMovement;
    }
    
}