/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
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
public class MaintenanceContractLazyDataModel extends LazyDataModel<MaintenanceContract>{
    private final MaintenanceContractService service;

    public MaintenanceContractLazyDataModel(MaintenanceContractService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(MaintenanceContract object) {
        return object.getId();
    }
    
    @Override
    public List<MaintenanceContract> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String customerName = null;
        Boolean isExpired = null;
        
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
            for (String filterProperty : filters.keySet()) {
                if (!filterProperty.isEmpty()) {
                    if (filterProperty.equalsIgnoreCase("customerName"))
                        customerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("isExpired"))
                        isExpired = (Boolean) filters.get(filterProperty);
                }
            }
        }
        
        List<MaintenanceContract> result = service.list(first, pageSize, sortField, isAscending, null, customerName, null, null, isExpired);
        this.setRowCount(service.getCount(null, customerName, null, null, isExpired).intValue());
        
        return result;
    }

    @Override
    public MaintenanceContract getRowData(String rowKey) {
        try {
            return service.find(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}