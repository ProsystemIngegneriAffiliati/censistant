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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.sales.boundary.TimeSpentService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.TimeSpent;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
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
public class TimeSpentLazyDataModel extends LazyDataModel<TimeSpent>{
    private final TimeSpentService service;
    
    private JobOrder jobOrder;
    private Worker worker;

    public TimeSpentLazyDataModel(TimeSpentService service) {
        this.service = service;
    }

    public TimeSpentLazyDataModel(TimeSpentService service, JobOrder jobOrder) {
        this.service = service;
        this.jobOrder = jobOrder;
    }
    
    @Override
    public Object getRowKey(TimeSpent object) {
        return object.getId();
    }
    
    @Override
    public List<TimeSpent> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String workerName = null;
        String workingOperationName = null;
        
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
                    if (filterProperty.equalsIgnoreCase("workerName"))
                        workerName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("workingOperationName"))
                        workingOperationName = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<TimeSpent> result = service.list(first, pageSize, sortField, isAscending, jobOrder, worker, workerName, workingOperationName);
        this.setRowCount(service.getCount(jobOrder, worker, workerName, workingOperationName).intValue());
        
        return result;
    }

    @Override
    public TimeSpent getRowData(String rowKey) {
        try {
            return service.find(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public JobOrder getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(JobOrder jobOrder) {
        this.jobOrder = jobOrder;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
    
}