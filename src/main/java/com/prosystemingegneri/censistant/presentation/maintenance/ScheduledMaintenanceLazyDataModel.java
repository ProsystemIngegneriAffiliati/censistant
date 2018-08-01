/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.maintenance.boundary.ScheduledMaintenanceService;
import com.prosystemingegneri.censistant.business.maintenance.entity.ScheduledMaintenance;
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
public class ScheduledMaintenanceLazyDataModel extends LazyDataModel<ScheduledMaintenance>{
    private final ScheduledMaintenanceService service;

    public ScheduledMaintenanceLazyDataModel(ScheduledMaintenanceService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(ScheduledMaintenance object) {
        return object.getId();
    }
    
    @Override
    public List<ScheduledMaintenance> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String name = null;
        String description = null;
        
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
                    if (filterProperty.equalsIgnoreCase("description"))
                        description = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<ScheduledMaintenance> result = service.listScheduledMaintenances(first, pageSize, sortField, isAscending, name, description);
        this.setRowCount(service.getScheduledMaintenancesCount(name, description).intValue());
        
        return result;
    }

    @Override
    public ScheduledMaintenance getRowData(String rowKey) {
        try {
            return service.readScheduledMaintenance(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
