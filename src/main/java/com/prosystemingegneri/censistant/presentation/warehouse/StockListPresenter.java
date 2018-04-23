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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.LocationService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.StockService;
import com.prosystemingegneri.censistant.business.warehouse.control.LocationType;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
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

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class StockListPresenter implements Serializable{
    @Inject
    StockService service;
    @Inject
    LocationService locationService;
    @Inject
    HandledItemService handledItemService;
    @Inject
    Authenticator authenticator;
    
    private StockLazyDataModel lazyStock;
    private List<Stock> selectedStock;
    
    private List<Stock> preparedStockForMovement;
    private HashMap<String, Integer> preparedIdStockForMovement;    //only for checking before insertion in 'preparedStockForMovement' list
    
    private LocationType locationTypeArrival;
    private Location locationArrival;
    
    private JobOrder jobOrder;
    private Integer activeIndex;
    
    private Device device;
    
    private String returnPage;
    
    @PostConstruct
    public void init() {
        lazyStock = new StockLazyDataModel(service);
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
        device = (Device) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("device");
        returnPage = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("returnPage");
        
        if (jobOrder != null) {
            locationTypeArrival = LocationType.SYSTEM;
            locationArrival = jobOrder.getOffer().getSystem();
        }
        
        initPreparedStock();
    }
    
    private void initPreparedStock() {
        preparedStockForMovement = new ArrayList<>();
        preparedIdStockForMovement = new HashMap<>();
    }
    
    private void initStockAfterSuccessfulMovement() {
        setLocation(null);
        selectedStock = null;
        initPreparedStock();
        locationTypeArrival = null;
        locationArrival = null;
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
    
    public List<Location> completeLocationsArrival(String name) {
        return locationService.listLocations(0, 10, null, null, locationTypeArrival, name);
    }
    
    public String move() {
        if (preparedStockForMovement == null || preparedStockForMovement.isEmpty() || locationArrival == null)
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select item and destination location"));
        else {
            try {
                List<HandledItem> created = handledItemService.createNewHandledItems(preparedStockForMovement, locationArrival, authenticator.getLoggedUser());
                if (created != null && !created.isEmpty()) {
                    int moved = created.size();
                    String itemStr = "item";
                    if (moved > 1)
                        itemStr += "s";
                    if (returnPage != null && !returnPage.isEmpty()) {
                        jobOrder.getOffer().setSystem((System) locationArrival);
                        putExternalContext();
                        return "/secured/" + returnPage + "?faces-redirect=true";
                    }
                    initStockAfterSuccessfulMovement();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Successfully moved " + moved + " " + itemStr ));
                }
                else
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Items movement not performed"));
            } catch (EJBException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            } catch (NoSuchFieldException e1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e1.getMessage()));
            }
        }
        
        return null;
    }
    
    public String cancel() {
        putExternalContext();
        
        return "/secured/" + returnPage + "?faces-redirect=true";
    }

    public void onLocationSelect(SelectEvent event) {
        initPreparedStock();
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("device", device);
    }

    public StockLazyDataModel getLazyStock() {
        return lazyStock;
    }

    public void setLazyStock(StockLazyDataModel lazyStock) {
        this.lazyStock = lazyStock;
    }

    public List<Stock> getSelectedStock() {
        return selectedStock;
    }

    public void setSelectedStock(List<Stock> selectedStock) {
        this.selectedStock = selectedStock;
    }
    
    public Location getLocation() {
        return lazyStock.getLocation();
    }

    public void setLocation(Location location) {
        lazyStock.setLocation(location);
    }

    public List<Stock> getPreparedStockForMovement() {
        return preparedStockForMovement;
    }

    public LocationType getLocationTypeArrival() {
        return locationTypeArrival;
    }

    public void setLocationTypeArrival(LocationType locationTypeArrival) {
        this.locationTypeArrival = locationTypeArrival;
    }

    public Location getLocationArrival() {
        return locationArrival;
    }

    public void setLocationArrival(Location locationArrival) {
        this.locationArrival = locationArrival;
    }

    public String getReturnPage() {
        return returnPage;
    }
    
}
