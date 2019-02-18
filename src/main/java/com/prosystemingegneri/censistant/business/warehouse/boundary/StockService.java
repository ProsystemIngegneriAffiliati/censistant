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
package com.prosystemingegneri.censistant.business.warehouse.boundary;

import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class StockService implements Serializable {
    @PersistenceContext
    EntityManager em;
    
    @Inject
    LocationService locationService;
    
    public Stock getStock(String id) {
        Stock result = null;
        String[] ids = id.split(Stock.SEPARATOR);
        if (ids[0] != null && !ids[0].isEmpty() && ids[1] != null && !ids[1].isEmpty() && ids[2] != null && !ids[2].isEmpty()) {
            Location location = locationService.readLocation(Long.parseLong(ids[0]));
            BoxedItem boxedItem = em.find(BoxedItem.class, Long.parseLong(ids[1]));
            result = new Stock(location, boxedItem, Integer.parseInt(ids[2]));
        }
        return result;
    }
    
    public List<Stock> listStock(int first, int pageSize, String sortField, Boolean isAscending, Long idLocation, String item, Long idItem) {
        List<Stock> result = new ArrayList<>();
        Query query = queryListStock(sortField, isAscending, idLocation, item, idItem, false);
        
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        
        List<Object[]> rowList = query.getResultList();
        if (rowList != null && !rowList.isEmpty())
            for (Object[] objects : rowList) {
                Location locationTemp = locationService.readLocation((Long) objects[0]);
                BoxedItem boxedItem = em.find(BoxedItem.class, (Long) objects[1]);
                result.add(new Stock(locationTemp, boxedItem, ((BigDecimal) objects[2]).intValue()));
            }
        
        return result;
    }
    
    public Long getStockCount(Long idLocation, String item, Long idItem) {
        Query query = queryListStock(null, null, idLocation, item, idItem, true);
        
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return new Long(0);
        }
        catch (NonUniqueResultException e) {
            List<Object> list = query.getResultList();
            return new Long(list.size());
        }
    }
    
    public Query queryListStock(String sortField, Boolean isAscending, Long idLocation, String item, Long idItem, boolean isCounting) {
        
        String queryString = "";
        String filterStringSupplier = "";
        
        if ((item != null && !item.isEmpty()) || idItem != null) {
            filterStringSupplier = "JOIN supplieritem ON boxeditem.item_id = supplieritem.id " +
                    "JOIN item ON supplieritem.item_id = item.id WHERE ";
            if (item != null && !item.isEmpty())
                filterStringSupplier += "LOWER(supplieritem.code) LIKE '%" + item.toLowerCase() + "%' " +
                        "OR LOWER(item.description) LIKE '%" + item.toLowerCase() +"%' ";
            if (idItem != null)
                filterStringSupplier += "item.id = " + idItem + " ";
        }
        
        if (isCounting)
            queryString = "SELECT COUNT(*) FROM (";
        
        queryString += "SELECT COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc) AS loc, COALESCE(fromSupplier.idboxeditem, fromLocation.idboxeditem, inLocation.idboxeditem) AS idboxeditem, SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) AS qty " +
                "FROM " +
                "( " +
                    "SELECT supplierplantlocation.id AS loc, boxeditem.id AS idboxeditem, SUM(box.quantity*purchaseorderrow.quantity) AS qty " +
                    "FROM purchaseorderrow " +
                    "JOIN purchaseorder ON purchaseorderrow.purchaseorder_id = purchaseorder.id " +
                    "JOIN supplierplantlocation ON purchaseorder.plant_id = supplierplantlocation.plant_id " +
                    "JOIN boxeditem ON purchaseorderrow.boxeditem_id = boxeditem.id " +
                    "JOIN box ON boxeditem.box_id = box.id " +
                    filterStringSupplier +
                    "GROUP BY supplierplantlocation.id, boxeditem.id " +
                ") AS fromSupplier " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiFrom.fromlocation_id as loc, hiFrom.boxeditem_id as idboxeditem, SUM(hiFrom.quantity) as qty " +
                    "FROM handleditem hiFrom " +
                    "JOIN boxeditem ON hiFrom.boxeditem_id = boxeditem.id " +
                    filterStringSupplier +
                    "GROUP BY hiFrom.fromlocation_id, hiFrom.boxeditem_id " +
                ") AS fromLocation " +
                "ON fromSupplier.loc = fromLocation.loc AND fromSupplier.idboxeditem = fromLocation.idboxeditem " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiTo.tolocation_id as loc, hiTo.boxeditem_id as idboxeditem, SUM(hiTo.quantity) as qty " +
                    "FROM handleditem hiTo " +
                    "JOIN boxeditem ON hiTo.boxeditem_id = boxeditem.id " +
                    filterStringSupplier +
                    "GROUP BY hiTo.tolocation_id, hiTo.boxeditem_id " +
                ") AS inLocation " +
                "ON fromLocation.loc = inLocation.loc AND fromSupplier.loc = inLocation.loc AND fromLocation.idboxeditem = inLocation.idboxeditem AND fromSupplier.idboxeditem = inLocation.idboxeditem " +
                "GROUP BY COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc), COALESCE(fromSupplier.idboxeditem, fromLocation.idboxeditem, inLocation.idboxeditem) " +
                "HAVING SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) > 0 ";
        if (idLocation != null)
            queryString += " AND COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc) = " + idLocation + " ";
        if (sortField != null && !sortField.isEmpty()) {
            if ("location".equals(sortField)) {
                queryString += "ORDER BY loc";
                if (!isAscending)
                    queryString += " DESC";
            }
        }
        
        if (isCounting)
            queryString += ") AS counting";
        
        Query query = em.createNativeQuery(queryString);
        
        return query;
    }
    
    public Long countPlacedQuantity(Long idLocation, Long idItem) {
        if (idLocation != null && idItem != null) {
            List<Stock> stocks = listStock(0, 0, null, Boolean.FALSE, idLocation, null, idItem);
            Long result = 0L;
            if (stocks != null && !stocks.isEmpty())
                for (Stock stock : stocks)
                    result += stock.getQuantity();
            return result;
        }
        
        return 0L;
    }
}
