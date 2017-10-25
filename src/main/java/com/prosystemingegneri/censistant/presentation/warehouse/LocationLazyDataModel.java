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

import com.prosystemingegneri.censistant.business.warehouse.boundary.LocationService;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
public class LocationLazyDataModel extends LazyDataModel<Location>{
    private final LocationService service;

    public LocationLazyDataModel(LocationService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(Location object) {
        return object.getId();
    }
    
    @Override
    public List<Location> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String name = null;
        
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
                    if (filterProperty.equalsIgnoreCase("name"))
                        name = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<Location> result = service.listLocations(first, pageSize, sortField, isAscending, name);
        this.setRowCount(service.getLocationsCount(name).intValue());
        
        return result;
    }

    @Override
    public Location getRowData(String rowKey) {
        try {
            return service.readLocation(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
