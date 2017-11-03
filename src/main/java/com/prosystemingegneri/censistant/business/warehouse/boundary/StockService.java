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
package com.prosystemingegneri.censistant.business.warehouse.boundary;

import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
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
            Location location = locationService.readLocation(Long.getLong(ids[0]));
            PurchaseOrderRow purchaseOrderRow = em.find(PurchaseOrderRow.class, Long.getLong(ids[1]));
            result = new Stock(location, purchaseOrderRow, Integer.getInteger(ids[2]));
        }
        return result;
    }
    
    public List<Stock> listStock(int first, int pageSize, String sortField, Boolean isAscending, String location, String item) {
        List<Stock> result = new ArrayList<>();
        Query query = queryListStock(location, item, false);
        
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        
        List<Object[]> rowList = query.getResultList();
        if (rowList != null && !rowList.isEmpty())
            for (Object[] objects : rowList) {
                Location locationTemp = locationService.readLocation((Long) objects[0]);
                PurchaseOrderRow purchaseOrderRow = em.find(PurchaseOrderRow.class, (Long) objects[1]);
                result.add(new Stock(locationTemp, purchaseOrderRow, ((BigDecimal) objects[2]).intValue()));
            }
        
        return result;
    }
    
    public Long getStockCount(String location, String item) {
        Query query = queryListStock(location, item, true);
        
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
    
    public Query queryListStock(String location, String item, boolean isCounting) {
        
        String queryString = "";
        
        if (isCounting)
            queryString = "SELECT COUNT(*) FROM (";
        
        queryString += "SELECT COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc) AS loc, COALESCE(fromSupplier.idpurchaseorderrow, fromLocation.idpurchaseorderrow, inLocation.idpurchaseorderrow) AS idpurchaseorderrow, SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) AS qty " +
                "FROM " +
                "( " +
                    "SELECT supplierlocation.id AS loc, purchaseorderrow.id AS idpurchaseorderrow, SUM(purchaseorderrow.quantity) AS qty " +
                    "FROM purchaseorderrow " +
                    "JOIN purchaseorder ON purchaseorderrow.purchaseorder_id = purchaseorder.id " +
                    "JOIN supplierlocation ON purchaseorder.supplier_id = supplierlocation.supplier_id " +
                    "GROUP BY supplierlocation.id, purchaseorderrow.id " +
                ") AS fromSupplier " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiFrom.fromlocation_id as loc, hiFrom.purchaseorderrow_id as idpurchaseorderrow, SUM(hiFrom.quantity) as qty " +
                    "FROM handleditem hiFrom " +
                    "GROUP BY hiFrom.fromlocation_id, hiFrom.purchaseorderrow_id " +
                ") AS fromLocation " +
                "ON fromSupplier.loc = fromLocation.loc AND fromSupplier.idpurchaseorderrow = fromLocation.idpurchaseorderrow " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiTo.tolocation_id as loc, hiTo.purchaseorderrow_id as idpurchaseorderrow, SUM(hiTo.quantity) as qty " +
                    "FROM handleditem hiTo " +
                    "GROUP BY hiTo.tolocation_id, hiTo.purchaseorderrow_id " +
                ") AS inLocation " +
                "ON fromLocation.loc = inLocation.loc AND fromSupplier.loc = inLocation.loc AND fromLocation.idpurchaseorderrow = inLocation.idpurchaseorderrow AND fromSupplier.idpurchaseorderrow = inLocation.idpurchaseorderrow " +
                "GROUP BY COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc), COALESCE(fromSupplier.idpurchaseorderrow, fromLocation.idpurchaseorderrow, inLocation.idpurchaseorderrow) " +
                "HAVING SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) > 0 " +
                "ORDER BY loc";
        
        if (isCounting)
            queryString += ") AS counting";
        
        Query query = em.createNativeQuery(queryString);
        
        return query;
    }
}
