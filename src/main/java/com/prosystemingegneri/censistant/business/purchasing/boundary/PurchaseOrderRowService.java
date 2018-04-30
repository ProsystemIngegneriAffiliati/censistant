/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.purchasing.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.purchasing.control.PurchaseOrderRowToBeDelivered;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class PurchaseOrderRowService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public PurchaseOrderRow savePurchaseOrderRow(PurchaseOrderRow purchaseOrderRow) {
        if (purchaseOrderRow.getId() == null)
            em.persist(purchaseOrderRow);
        else
            return em.merge(purchaseOrderRow);
        
        return purchaseOrderRow;
    }
    
    public PurchaseOrderRow readPurchaseOrderRow(Long id) {
        return em.find(PurchaseOrderRow.class, id);
    }
    
    public List<PurchaseOrderRow> listPurchaseOrderRows(int first, int pageSize, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        Query query = queryPurchaseOrderRows(false, sortField, isAscending, supplierItemCode, supplierItemDescription, plant, isToBeDelivered);
        
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        
        return query.getResultList();
    }
    
    public Long getPurchaseOrderRowsCount(String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        Query query = queryPurchaseOrderRows(true, null, null, supplierItemCode, supplierItemDescription, plant, isToBeDelivered);
        
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        } catch (NonUniqueResultException e) {
            Long result = 0L;
            for (Object individualResult : query.getResultList()) {
                result += (Long) individualResult;
            }
            return result;
            //return em.createQuery(select).getResultList().stream().mapToLong(Long::sum);  //Does not compile
        }
    }
    
    private Query queryPurchaseOrderRows(boolean isCount, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        boolean filterAdded = false;
        
        StringBuilder queryString = new StringBuilder("SELECT ");
        if (isCount)
            queryString.append("COUNT(por) ");
        else
            queryString.append("por ");
        
        queryString.append("FROM PurchaseOrderRow por ");
        
        //JOIN PART
        //Supplier's item code or item description
        if ((supplierItemCode != null && !supplierItemCode.isEmpty()) || (supplierItemDescription != null && !supplierItemDescription.isEmpty()))
            queryString.append("JOIN por.boxedItem bi ")
                    .append("JOIN bi.item si ");
        //Supplier's plant
        if (plant != null)
            queryString.append("JOIN por.purchaseOrder po ");
        //Is the entire row to be delivered yet?
        if (isToBeDelivered != null)
            queryString.append("LEFT JOIN por.deliveryNoteRows dnr ")
                    .append("LEFT JOIN dnr.handledItem hi ");
        
        //WHERE PART
        if ((supplierItemCode != null && !supplierItemCode.isEmpty()) || (supplierItemDescription != null && !supplierItemDescription.isEmpty()) || plant != null)
            queryString.append("WHERE ");
        //Supplier's item code
        if (supplierItemCode != null && !supplierItemCode.isEmpty()) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("LOWER(si.code) LIKE '%").append(supplierItemCode.toLowerCase()).append("%' ");
        }
        //Supplier's item description
        if (supplierItemDescription != null && !supplierItemDescription.isEmpty()) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("LOWER(si.description) LIKE '%").append(supplierItemDescription.toLowerCase()).append("%' ");
        }
        //Supplier's plant
        if (plant != null) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("po.plant.id = ").append(plant.getId()).append(" ");
        }
        
        //AGGREGATION PART
        //Is the entire row to be delivered yet?
        if (isToBeDelivered != null) {
            queryString.append("GROUP BY por ")
                    .append("HAVING sum(coalesce(hi.quantity, 0))");
            if (isToBeDelivered)
                queryString.append(" < ");
            else
                queryString.append(" >= ");
            queryString.append("por.quantity ");
        }
        
        //SORTING PART
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            queryString.append("ORDER BY ");
            switch (sortField) {
                case "supplierItemCode":
                    queryString.append("si.code ");
                    break;
                case "supplierItemDescription":
                    queryString.append("si.description ");
                    break;
                case "quantity":
                    queryString.append("por.quantity ");
                    break;
                default:
                    queryString.append("por.id ");
            }
            if (!isAscending)
                queryString.append("DESC ");
        }
        
        return em.createQuery(queryString.toString());
    }
    
    public List<PurchaseOrderRowToBeDelivered> listPurchaseOrderRowsToBeDelivered(int first, int pageSize, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, boolean isToBeDelivered, Long idPurchaseOrderRow, Long idPurchaseOrder) {
        Query query = queryPurchaseOrderRowsToBeDelivered(false, sortField, isAscending, supplierItemCode, supplierItemDescription, plant, isToBeDelivered, idPurchaseOrderRow, idPurchaseOrder);
        
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        
        return query.getResultList();
    }
    
    public Long getPurchaseOrderRowsToBeDeliveredCount(String supplierItemCode, String supplierItemDescription, Plant plant, boolean isToBeDelivered, Long idPurchaseOrder) {
        Query query = queryPurchaseOrderRowsToBeDelivered(true, null, null, supplierItemCode, supplierItemDescription, plant, isToBeDelivered, null, idPurchaseOrder);
        
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        } catch (NonUniqueResultException e) {
            Long result = 0L;
            for (Object individualResult : query.getResultList()) {
                result += (Long) individualResult;
            }
            return result;
            //return em.createQuery(select).getResultList().stream().mapToLong(Long::sum);  //Does not compile
        }
    }
    
    private Query queryPurchaseOrderRowsToBeDelivered(boolean isCount, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, boolean isToBeDelivered, Long idPurchaseOrderRow, Long idPurchaseOrder) {
        boolean filterAdded = false;
        
        StringBuilder queryString = new StringBuilder("SELECT ");
        if (isCount)
            queryString.append("COUNT(por) ");
        else {
            queryString.append("new com.prosystemingegneri.censistant.business.purchasing.control.PurchaseOrderRowToBeDelivered(")
                    .append("por.id, ")
                    .append("po.number, ")
                    .append("po.creation, ")
                    .append("si.code, ")
                    .append("si.description, ")
                    .append("por.quantity - sum(coalesce(hi.quantity, 0)), ")
                    .append("bum.name, ")
                    .append("b.quantity, ")
                    .append("ium.symbol) ");
        }
        
        queryString.append("FROM PurchaseOrderRow por ");
        
        //JOIN PART
        queryString.append("JOIN por.boxedItem bi ")
                .append("JOIN bi.item si ")
                .append("JOIN por.purchaseOrder po ")
                .append("JOIN bi.box b ")
                .append("JOIN b.unitMeasure bum ")
                .append("JOIN si.item i ")
                .append("JOIN i.unitMeasure ium ")
                .append("LEFT JOIN por.deliveryNoteRows dnr ")
                .append("LEFT JOIN dnr.handledItem hi ");
        
        //WHERE PART
        if ((supplierItemCode != null && !supplierItemCode.isEmpty()) || (supplierItemDescription != null && !supplierItemDescription.isEmpty()) || plant != null || idPurchaseOrderRow != null)
            queryString.append("WHERE ");
        //Supplier's item code
        if (supplierItemCode != null && !supplierItemCode.isEmpty()) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("LOWER(si.code) LIKE '%").append(supplierItemCode.toLowerCase()).append("%' ");
        }
        //Supplier's item description
        if (supplierItemDescription != null && !supplierItemDescription.isEmpty()) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("LOWER(si.description) LIKE '%").append(supplierItemDescription.toLowerCase()).append("%' ");
        }
        //Supplier's plant
        if (plant != null) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("po.plant.id = ").append(plant.getId()).append(" ");
        }
        //Purchase order row's ID
        if (idPurchaseOrderRow != null) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("por.id = ").append(idPurchaseOrderRow).append(" ");
        }
        
        //Purchase order ID
        if (idPurchaseOrder != null) {
            if (filterAdded)
                queryString.append("AND ");
            else
                filterAdded = true;
            queryString.append("po.id = ").append(idPurchaseOrder).append(" ");
        }
        
        //AGGREGATION PART
        //Is the entire row to be delivered yet?
        queryString.append("GROUP BY por.id, si.code, si.description, po.number, po.creation, bum.name, b.quantity, ium.symbol ")
                .append("HAVING sum(coalesce(hi.quantity, 0))");
        if (isToBeDelivered)
            queryString.append(" < ");
        else
            queryString.append(" >= ");
        queryString.append("por.quantity ");
        
        //SORTING PART
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            queryString.append("ORDER BY ");
            switch (sortField) {
                case "supplierItemCode":
                    queryString.append("si.code ");
                    break;
                case "supplierItemDescription":
                    queryString.append("si.description ");
                    break;
                case "quantity":
                    queryString.append("por.quantity ");
                    break;
                default:
                    queryString.append("por.id ");
            }
            if (!isAscending)
                queryString.append("DESC ");
        }
        
        return em.createQuery(queryString.toString());
    }
}
