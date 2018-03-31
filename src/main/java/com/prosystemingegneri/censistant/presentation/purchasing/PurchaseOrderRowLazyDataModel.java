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
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
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
public class PurchaseOrderRowLazyDataModel extends LazyDataModel<PurchaseOrderRow>{
    private final PurchaseOrderRowService service;
    
    private Plant plant;
    private Boolean isToBeDelivered;

    public PurchaseOrderRowLazyDataModel(PurchaseOrderRowService service) {
        this.service = service;
    }

    public PurchaseOrderRowLazyDataModel(PurchaseOrderRowService service, Plant plant, Boolean isToBeDelivered) {
        this.service = service;
        this.plant = plant;
        this.isToBeDelivered = isToBeDelivered;
    }
    
    @Override
    public Object getRowKey(PurchaseOrderRow object) {
        return object.getId();
    }
    
    @Override
    public List<PurchaseOrderRow> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
        
        List<PurchaseOrderRow> result = service.listPurchaseOrderRows(first, pageSize, sortField, isAscending, supplierItemCode, supplierItemDescription, plant, isToBeDelivered);
        this.setRowCount(service.getPurchaseOrderRowsCount(supplierItemCode, supplierItemDescription, plant, isToBeDelivered).intValue());
        
        return result;
    }

    @Override
    public PurchaseOrderRow getRowData(String rowKey) {
        try {
            return service.readPurchaseOrderRow(Long.parseLong(rowKey));
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
