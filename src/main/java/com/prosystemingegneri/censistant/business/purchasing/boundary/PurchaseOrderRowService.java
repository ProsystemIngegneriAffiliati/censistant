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
    
    public Query queryPurchaseOrderRows(boolean isCount, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
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

    /*public List<PurchaseOrderRow> listPurchaseOrderRows(int first, int pageSize, String sortField, Boolean isAscending, String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseOrderRow> query = cb.createQuery(PurchaseOrderRow.class);
        Root<PurchaseOrderRow> root = query.from(PurchaseOrderRow.class);
        CriteriaQuery<PurchaseOrderRow> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(query, cb, root, supplierItemCode, supplierItemDescription, plant, isToBeDelivered);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(PurchaseOrderRow_.id));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "supplierItemCode":
                    path = root.get(PurchaseOrderRow_.boxedItem).get(BoxedItem_.item).get(SupplierItem_.code);
                    break;
                case "supplierItemDescription":
                    path = root.get(PurchaseOrderRow_.boxedItem).get(BoxedItem_.item).get(SupplierItem_.description);
                    break;
                case "quantity":
                    path = root.get(PurchaseOrderRow_.quantity);
                    break;
                default:
                    path = root.get(sortField);
            }
            if (isAscending)
                order = cb.asc(path);
            else
                order = cb.desc(path);
        }
        query.orderBy(order);
        
        TypedQuery<PurchaseOrderRow> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getPurchaseOrderRowsCount(String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PurchaseOrderRow> root = query.from(PurchaseOrderRow.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(query, cb, root, supplierItemCode, supplierItemDescription, plant, isToBeDelivered);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        try {
            return em.createQuery(select).getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        } catch (NonUniqueResultException e) {
            Long result = 0L;
            for (Long individualResult : em.createQuery(select).getResultList()) {
                result += individualResult;
            }
            return result;
            //return em.createQuery(select).getResultList().stream().mapToLong(Long::sum);  //Does not compile
        }
    }
    
    private List<Predicate> calculateConditions(CriteriaQuery query, CriteriaBuilder cb, Root<PurchaseOrderRow> root, String supplierItemCode, String supplierItemDescription, Plant plant, Boolean isToBeDelivered) {
        List<Predicate> conditions = new ArrayList<>();
        
        //(Supplier) item's code
        if (supplierItemCode != null && !supplierItemCode.isEmpty()) {
            Join<BoxedItem, SupplierItem> supplierItemRoot = root.join(PurchaseOrderRow_.boxedItem)
                    .join(BoxedItem_.item);
            conditions.add(cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.code)), "%" + supplierItemCode.toLowerCase() + "%"));
        }
        
        //(Supplier) item's description
        if (supplierItemDescription != null && !supplierItemDescription.isEmpty()) {
            Join<BoxedItem, SupplierItem> supplierItemRoot = root.join(PurchaseOrderRow_.boxedItem)
                    .join(BoxedItem_.item);
            conditions.add(cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.description)), "%" + supplierItemDescription.toLowerCase() + "%"));
        }
        
        //Supplier's plant
        if (plant != null) {
            Join<PurchaseOrderRow, PurchaseOrder> purchaseOrderRoot = root.join(PurchaseOrderRow_.purchaseOrder);
            conditions.add(cb.equal(purchaseOrderRoot.get(PurchaseOrder_.plant), plant));
        }
        
        //Is the entire row to be delivered yet?
        if (isToBeDelivered != null) {
            query.groupBy(root);
            
            Expression<Long> quantityDelivered = cb.sumAsLong(cb.coalesce(root.join(PurchaseOrderRow_.deliveryNoteRows, JoinType.LEFT)
                    .join(DeliveryNoteRow_.handledItem, JoinType.LEFT)
                    .get(HandledItem_.quantity), cb.literal(0)));
            Expression<Long> quantityToBeDelivered = cb.toLong(root.get(PurchaseOrderRow_.quantity));
            Predicate predicate;
            if (isToBeDelivered)
                predicate = cb.lessThan(quantityDelivered, quantityToBeDelivered);
            else
                predicate = cb.greaterThanOrEqualTo(quantityDelivered, quantityToBeDelivered);
            
            query.having(predicate);
        }
        
        return conditions;
    }*/
}
