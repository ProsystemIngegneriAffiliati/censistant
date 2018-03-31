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
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow_;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder_;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem_;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
    }
}
