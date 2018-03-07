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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
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
public class HandledItemLazyDataModel extends LazyDataModel<HandledItem>{
    private final HandledItemService service;

    public HandledItemLazyDataModel(HandledItemService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(HandledItem object) {
        return object.getId();
    }
    
    @Override
    public List<HandledItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String workerName = null;
        String supplierItemCode = null;
        String fromLocationName = null;
        String toLocationName = null;
        
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
                    if (filterProperty.equalsIgnoreCase("workerName"))
                        workerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("supplierItemCode"))
                        supplierItemCode = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("fromLocationName"))
                        fromLocationName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("toLocationName"))
                        toLocationName = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<HandledItem> result = service.listHandledItems(first, pageSize, sortField, isAscending, workerName, supplierItemCode, fromLocationName, toLocationName, null, null, null);
        this.setRowCount(service.getHandledItemsCount(workerName, supplierItemCode, fromLocationName, toLocationName, null, null, null).intValue());
        
        return result;
    }

    @Override
    public HandledItem getRowData(String rowKey) {
        try {
            return service.readHandledItem(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
