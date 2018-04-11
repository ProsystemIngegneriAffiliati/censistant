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
package com.prosystemingegneri.censistant.presentation.siteSurvey;

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
public class SiteSurveyReportLazyDataModel extends LazyDataModel<SiteSurveyReport>{
    private final SiteSurveyReportService service;
    private Date start;
    private Date end;

    public SiteSurveyReportLazyDataModel(SiteSurveyReportService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(SiteSurveyReport object) {
        return object.getId();
    }
    
    @Override
    public List<SiteSurveyReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        String customer = null;
        String systemType = null;
        String seller = null;
        Integer number = null;
        String nameAddressPlant = null;
        
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
                    if (filterProperty.equalsIgnoreCase("number")) {
                        try {
                            number = Integer.valueOf(String.valueOf(filters.get(filterProperty)));
                        } catch (NumberFormatException e) {
                        }
                    }
                    if (filterProperty.equalsIgnoreCase("customer"))
                        customer = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("systemType"))
                        systemType = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("seller"))
                        seller = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("nameAddressPlant"))
                        nameAddressPlant = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<SiteSurveyReport> result = service.listSiteSurveyReports(first, pageSize, sortField, isAscending, number, start, end, customer, systemType, seller, nameAddressPlant, null);
        this.setRowCount(service.getSiteSurveyReportsCount(number, start, end, customer, systemType, seller, nameAddressPlant, null).intValue());
        
        return result;
    }

    @Override
    public SiteSurveyReport getRowData(String rowKey) {
        try {
            return service.readSiteSurveyReport(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
    
}
