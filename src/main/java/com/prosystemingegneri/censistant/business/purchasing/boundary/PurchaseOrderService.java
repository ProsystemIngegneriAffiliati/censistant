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
package com.prosystemingegneri.censistant.business.purchasing.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder_;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class PurchaseOrderService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public PurchaseOrder createNewPurchaseOrder() {
        return new PurchaseOrder(getNextNumber());
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<PurchaseOrder> root = query.from(PurchaseOrder.class);
        query.select(cb.greatest(root.get(PurchaseOrder_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(PurchaseOrder_.creation), dateStart.getTime(), dateEnd.getTime()));

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        Integer result;
        try {
            result = em.createQuery(query).getSingleResult();
            if (result != null)
                result++;
            else
                result = 1;
        } catch (NoResultException e) {
            result = 1;
        }
        
	return result;
    }
    
    public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder) {        
        if (purchaseOrder.getId() == null)
            em.persist(purchaseOrder);
        else
            return em.merge(purchaseOrder);
        
        return null;
    }
    
    public PurchaseOrder readPurchaseOrder(Long id) {
        return em.find(PurchaseOrder.class, id);
    }
    
    public void deletePurchaseOrder(Long id) {
        em.remove(readPurchaseOrder(id));
    }

    public List<PurchaseOrder> listPurchaseOrders(int first, int pageSize, String sortField, Boolean isAscending, Integer number, String supplier, String supplierItemDescription) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseOrder> query = cb.createQuery(PurchaseOrder.class);
        Root<PurchaseOrder> root = query.from(PurchaseOrder.class);
        CriteriaQuery<PurchaseOrder> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, supplier, supplierItemDescription);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(PurchaseOrder_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "number":
                    path = root.get(PurchaseOrder_.number);
                    break;
                case "supplier":
                    path = root.get(PurchaseOrder_.supplier).get(CustomerSupplier_.name);
                    break;
                case "creation":
                    path = root.get(PurchaseOrder_.creation);
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
        
        TypedQuery<PurchaseOrder> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getPurchaseOrdersCount(Integer number, String supplier, String supplierItemDescription) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PurchaseOrder> root = query.from(PurchaseOrder.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, supplier, supplierItemDescription);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<PurchaseOrder> root, Integer number, String supplier, String supplierItemDescription) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(PurchaseOrder_.number), number));
        
        //supplier's name
        if (supplier != null && !supplier.isEmpty()) {
            Join<PurchaseOrder, CustomerSupplier> supplierRoot = root.join(PurchaseOrder_.supplier);
            conditions.add(cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + supplier.toLowerCase() + "%"));
        }
        
        //supplier item's description
        if (supplierItemDescription != null && !supplierItemDescription.isEmpty()) {
            
            
            Join<BoxedItem, SupplierItem> supplierItemRoot = root.join(PurchaseOrder_.rows)
                    .join(PurchaseOrderRow_.boxedItem)
                    .join(BoxedItem_.item);
            conditions.add(cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.description)), "%" + supplierItemDescription.toLowerCase() + "%"));
        }
        
        return conditions;
    }
}
