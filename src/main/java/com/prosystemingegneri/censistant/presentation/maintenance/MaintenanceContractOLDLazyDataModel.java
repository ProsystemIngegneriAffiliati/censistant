/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class MaintenanceContractOLDLazyDataModel extends LazyDataModel<MaintenanceContract>{
    /*private final MaintenanceContractOLDService service;
    
    private System system;

    public MaintenanceContractOLDLazyDataModel(MaintenanceContractOLDService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(MaintenanceContract object) {
        return object.getId();
    }
    
    @Override
    public List<MaintenanceContract> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        Boolean isFullService = null;
        Boolean isOnCall = null;
        String customerName = null;
        Boolean isExpired = null;
        Boolean isCompleted = null;
        
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
                    if (filterProperty.equalsIgnoreCase("isFullService"))
                        isFullService = Boolean.parseBoolean(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("isOnCall"))
                        isOnCall = Boolean.parseBoolean(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("customerName"))
                        customerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("isExpired"))
                        isExpired = Boolean.parseBoolean(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("isCompleted"))
                        isCompleted = Boolean.parseBoolean(String.valueOf(filters.get(filterProperty)));
                }
            }
        }
        
        List<MaintenanceContract> result = service.listMaintenanceContracts(first, pageSize, sortField, isAscending, isFullService, isOnCall, customerName, isExpired, isCompleted);
        this.setRowCount(service.getMaintenanceContractsCount(isFullService, isOnCall, customerName, isExpired, isCompleted).intValue());
        
        return result;
    }

    @Override
    public MaintenanceContract getRowData(String rowKey) {
        try {
            return service.readMaintenanceContract(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }
    */
}
