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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.maintenance.control.SuitableForOperation;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTaskDto;
import java.util.Date;
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
public class MaintenanceTaskLazyDataModel extends LazyDataModel<MaintenanceTaskDto>{
    private final MaintenanceTaskService service;
    
    private Date expiryStart;
    private Date expiryEnd;

    public MaintenanceTaskLazyDataModel(MaintenanceTaskService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(MaintenanceTaskDto object) {
        return object.getId();
    }
    
    @Override
    public List<MaintenanceTaskDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String customerName = null;
        String systemAddress = null;
        MaintenanceType maintenanceType = null;
        SuitableForOperation suitableForOperation = null;
        Boolean isClosed = null;
        
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
                    if (filterProperty.equalsIgnoreCase("customerName"))
                        customerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("systemAddress"))
                        systemAddress = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("maintenanceType"))
                        maintenanceType = MaintenanceType.valueOf(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("suitableForOperation"))
                        suitableForOperation = SuitableForOperation.valueOf(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("isClosed"))
                        isClosed = Boolean.parseBoolean(String.valueOf(filters.get(filterProperty)));
                }
            }
        }
        
        List<MaintenanceTaskDto> result = service.listMaintenanceTasks(first, pageSize, sortField, isAscending, customerName, systemAddress, maintenanceType, expiryStart, expiryEnd, suitableForOperation, isClosed);
        this.setRowCount(service.getMaintenanceTasksCount(customerName, systemAddress, maintenanceType, expiryStart, expiryEnd, suitableForOperation, isClosed).intValue());
        
        return result;
    }

    @Override
    public MaintenanceTaskDto getRowData(String rowKey) {
        try {
            return MaintenanceTaskDto.create(service.readMaintenanceTask(Long.parseLong(rowKey)));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}
