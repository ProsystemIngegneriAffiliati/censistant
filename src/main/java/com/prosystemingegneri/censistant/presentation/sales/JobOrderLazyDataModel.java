/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
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
public class JobOrderLazyDataModel extends LazyDataModel<JobOrder>{
    private final JobOrderService service;

    public JobOrderLazyDataModel(JobOrderService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(JobOrder object) {
        return object.getId();
    }
    
    @Override
    public List<JobOrder> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        Integer number = null;
        String customerName = null;
        String plantAddress = null;
        String systemType = null;
        
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
                    if (filterProperty.equalsIgnoreCase("number"))
                        number = Integer.parseInt(String.valueOf(filters.get(filterProperty)));
                    if (filterProperty.equalsIgnoreCase("customerName"))
                        customerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("plantAddress"))
                        plantAddress = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("systemType"))
                        systemType = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<JobOrder> result = service.listJobOrders(first, pageSize, sortField, isAscending, number, customerName, null, plantAddress, systemType);
        this.setRowCount(service.getJobOrdersCount(number, customerName, null, plantAddress, systemType).intValue());
        
        return result;
    }

    @Override
    public JobOrder getRowData(String rowKey) {
        try {
            return service.readJobOrder(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
