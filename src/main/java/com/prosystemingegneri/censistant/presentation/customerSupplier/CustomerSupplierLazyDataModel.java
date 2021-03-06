/*
 * Copyright (C) 2017-2019 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
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
public class CustomerSupplierLazyDataModel extends LazyDataModel<CustomerSupplier>{
    private final CustomerSupplierService service;
    private final Boolean isCustomer;
    private final Boolean isSupplier;
    private final Boolean isPotentialCustomerDefault;

    public CustomerSupplierLazyDataModel(CustomerSupplierService service, Boolean isCustomer, Boolean isSupplier, Boolean isPotentialCustomer) {
        this.service = service;
        this.isCustomer = isCustomer;
        this.isSupplier = isSupplier;
        this.isPotentialCustomerDefault = isPotentialCustomer;
    }

    @Override
    public Object getRowKey(CustomerSupplier object) {
        return object.getId();
    }

    @Override
    public List<CustomerSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        Boolean isPotentialCustomer = isPotentialCustomerDefault;
        Boolean isOnlyInfo = null;
        String businessName = null;
        String name = null;
        String address = null;
        
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
                    if (filterProperty.equalsIgnoreCase("isPotentialCustomer"))
                        isPotentialCustomer = (Boolean) filters.get(filterProperty);
                    if (filterProperty.equalsIgnoreCase("isOnlyInfo"))
                        isOnlyInfo = (Boolean) filters.get(filterProperty);
                    if (filterProperty.equalsIgnoreCase("businessName"))
                        businessName = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("name"))
                        name = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("address"))
                        address = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<CustomerSupplier> result = service.listCustomerSuppliers(first, pageSize, sortField, isAscending, isPotentialCustomer, isOnlyInfo, isCustomer, isSupplier, businessName, name, address);
        this.setRowCount(service.getCustomerSuppliersCount(isPotentialCustomer, isOnlyInfo, isCustomer, isSupplier, businessName, name, address).intValue());
        
        return result;
    }

    @Override
    public CustomerSupplier getRowData(String rowKey) {
        try {
            return service.readCustomerSupplier(Long.parseLong(rowKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
}
