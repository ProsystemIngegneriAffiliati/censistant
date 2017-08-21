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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
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

    public CustomerSupplierLazyDataModel(CustomerSupplierService service) {
        this.service = service;
    }

    @Override
    public Object getRowKey(CustomerSupplier object) {
        return object.getId();
    }

    @Override
    public List<CustomerSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        
        switch (sortOrder) {
            case ASCENDING:
                isAscending = Boolean.TRUE;
                break;
            case DESCENDING:
                isAscending = Boolean.FALSE;
                break;
            default:
        }
        List<CustomerSupplier> result = service.listCustomerSuppliers(first, pageSize, filters, sortField, isAscending);
        this.setRowCount(service.getCustomerSuppliersCount(filters).intValue());
        
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
