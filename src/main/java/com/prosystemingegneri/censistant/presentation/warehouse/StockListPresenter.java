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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.warehouse.boundary.StockService;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
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
public class StockListPresenter implements Serializable{
    @Inject
    StockService service;
    
    private StockLazyDataModel lazyStock;
    private List<Stock> selectedStock;
    
    private List<Stock> preparedStockForMovement;
    private HashMap<String, Integer> preparedIdStockForMovement;    //only for checking before insertion in 'preparedStockForMovement' list
    
    @PostConstruct
    public void init() {
        lazyStock = new StockLazyDataModel(service);
        initPreparedStock();
    }
    
    private void initPreparedStock() {
        preparedStockForMovement = new ArrayList<>();
        preparedIdStockForMovement = new HashMap<>();
    }
    
    public void onWarehouseStockSelect(SelectEvent event) {
        Stock tempStock = (Stock) event.getObject();
        if (!preparedIdStockForMovement.containsKey(tempStock.getLocationIdPurchaseOrderRowId())) {
            preparedStockForMovement.add(tempStock);
            preparedIdStockForMovement.put(tempStock.getLocationIdPurchaseOrderRowId(), 0);
        }
    }

    public void onLocationSelect(SelectEvent event) {
        initPreparedStock();
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
    
}
