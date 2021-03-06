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
package com.prosystemingegneri.censistant.presentation.deliveryNote;

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.DeliveryNoteOutService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
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
public class DeliveryNoteOutLazyDataModel extends LazyDataModel<DeliveryNoteOut>{
    private final DeliveryNoteOutService service;
    
    private Date start;
    private Date end;

    public DeliveryNoteOutLazyDataModel(DeliveryNoteOutService service) {
        this.service = service;
    }
    
    @Override
    public Object getRowKey(DeliveryNoteOut object) {
        return object.getId();
    }
    
    @Override
    public List<DeliveryNoteOut> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Boolean isAscending = null;
        Integer number = null;
        String goodsDescription = null;
        String shipmentReason = null;
        String shippingPayment = null;
        String customerSupplierNamePlantNameAddress = null;
        
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
                    if (filterProperty.equalsIgnoreCase("goodsDescription"))
                        goodsDescription = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("shipmentReason"))
                        shipmentReason = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("shippingPayment"))
                        shippingPayment = String.valueOf(filters.get(filterProperty));
                    if (filterProperty.equalsIgnoreCase("customerSupplierNamePlantNameAddress"))
                        customerSupplierNamePlantNameAddress = String.valueOf(filters.get(filterProperty));
                }
            }
        }
        
        List<DeliveryNoteOut> result = service.listDeliveryNoteOuts(first, pageSize, sortField, isAscending, number, start, end, goodsDescription, shipmentReason, shippingPayment, customerSupplierNamePlantNameAddress);
        this.setRowCount(service.getDeliveryNoteOutsCount(number, start, end, goodsDescription, shipmentReason, shippingPayment, customerSupplierNamePlantNameAddress).intValue());
        
        return result;
    }

    @Override
    public DeliveryNoteOut getRowData(String rowKey) {
        try {
            return service.readDeliveryNoteOut(Long.parseLong(rowKey));
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
