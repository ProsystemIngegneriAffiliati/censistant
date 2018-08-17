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
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
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
    
    private Location fromLocation;
    private Location toLocation;
    private Boolean isAssociatedToDeliveryNoteRow;
    private Boolean isAssociatedToReplacement;

    public HandledItemLazyDataModel(HandledItemService service) {
        this.service = service;
    }

    public HandledItemLazyDataModel(HandledItemService service, Location fromLocation, Location toLocation, Boolean isLinkedToDeliveryNoteRow, Boolean isAssociatedToReplacement) {
        this.service = service;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.isAssociatedToDeliveryNoteRow = isLinkedToDeliveryNoteRow;
        this.isAssociatedToReplacement = isAssociatedToReplacement;
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
        String supplierItemDescription = null;
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
                    if (filterProperty.equalsIgnoreCase("supplierItemDescription"))
                        supplierItemDescription = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("fromLocationName"))
                        fromLocationName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("toLocationName"))
                        toLocationName = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<HandledItem> result = service.listHandledItems(first, pageSize, sortField, isAscending, workerName, supplierItemCode, supplierItemDescription, fromLocation, fromLocationName, toLocation, toLocationName, null, null, null, isAssociatedToDeliveryNoteRow, isAssociatedToReplacement);
        this.setRowCount(service.getHandledItemsCount(workerName, supplierItemCode, supplierItemDescription, fromLocation, fromLocationName, toLocation, toLocationName, null, null, null, isAssociatedToDeliveryNoteRow, isAssociatedToReplacement).intValue());
        
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

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public Boolean getIsAssociatedToDeliveryNoteRow() {
        return isAssociatedToDeliveryNoteRow;
    }

    public void setIsAssociatedToDeliveryNoteRow(Boolean isAssociatedToDeliveryNoteRow) {
        this.isAssociatedToDeliveryNoteRow = isAssociatedToDeliveryNoteRow;
    }

    public Boolean getIsAssociatedToReplacement() {
        return isAssociatedToReplacement;
    }

    public void setIsAssociatedToReplacement(Boolean isAssociatedToReplacement) {
        this.isAssociatedToReplacement = isAssociatedToReplacement;
    }
    
}
