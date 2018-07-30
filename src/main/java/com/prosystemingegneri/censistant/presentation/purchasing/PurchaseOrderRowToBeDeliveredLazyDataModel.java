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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.purchasing.boundary.PurchaseOrderRowService;
import com.prosystemingegneri.censistant.business.purchasing.control.PurchaseOrderRowToBeDelivered;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import static org.primefaces.model.SortOrder.ASCENDING;
import static org.primefaces.model.SortOrder.DESCENDING;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class PurchaseOrderRowToBeDeliveredLazyDataModel extends LazyDataModel<PurchaseOrderRowToBeDelivered>{
    private final PurchaseOrderRowService service;
    
    private Plant plant;
    private Boolean isToBeDelivered;

    public PurchaseOrderRowToBeDeliveredLazyDataModel(PurchaseOrderRowService service) {
        this.service = service;
    }

    public PurchaseOrderRowToBeDeliveredLazyDataModel(PurchaseOrderRowService service, Plant plant, Boolean isToBeDelivered) {
        this.service = service;
        this.plant = plant;
        this.isToBeDelivered = isToBeDelivered;
    }
    
    @Override
    public Object getRowKey(PurchaseOrderRowToBeDelivered object) {
        return object.getId();
    }
    
    @Override
    public List<PurchaseOrderRowToBeDelivered> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String supplierItemCode = null;
        String supplierItemDescription = null;
        
        switch (sortOrder) {
            case ASCENDING:
                isAscending = Boolean.TRUE;
                break;
            case DESCENDING:
                isAscending = Boolean.FALSE;
                break;
            default:
        }
        
        if (filters != null && !filters.isEmpty()) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();

                if (!filterProperty.isEmpty()) {
                    if (filterProperty.equalsIgnoreCase("supplierItemCode"))
                        supplierItemCode = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("supplierItemDescription"))
                        supplierItemDescription = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<PurchaseOrderRowToBeDelivered> result = service.listPurchaseOrderRowsToBeDelivered(first, pageSize, sortField, isAscending, supplierItemCode, supplierItemDescription, plant, isToBeDelivered, null, null);
        this.setRowCount(service.getPurchaseOrderRowsToBeDeliveredCount(supplierItemCode, supplierItemDescription, plant, isToBeDelivered, null).intValue());
        
        return result;
    }

    @Override
    public PurchaseOrderRowToBeDelivered getRowData(String rowKey) {
        try {
            return service.listPurchaseOrderRowsToBeDelivered(0, 0, null, false, null, null, null, true, Long.parseLong(rowKey), null).get(0);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Boolean getIsToBeDelivered() {
        return isToBeDelivered;
    }

    public void setIsToBeDelivered(Boolean isToBeDelivered) {
        this.isToBeDelivered = isToBeDelivered;
    }
    
}
