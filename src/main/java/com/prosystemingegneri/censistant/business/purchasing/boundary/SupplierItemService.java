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
import com.prosystemingegneri.censistant.business.production.entity.Item;
import com.prosystemingegneri.censistant.business.production.entity.Item_;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
public class SupplierItemService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public SupplierItem saveSupplierItem(SupplierItem supplierItem) {        
        if (supplierItem.getId() == null)
            em.persist(supplierItem);
        else
            return em.merge(supplierItem);
        
        return null;
    }
    
    public SupplierItem readSupplierItem(Long id) {
        return em.find(SupplierItem.class, id);
    }
    
    public void deleteSupplierItem(Long id) {
        em.remove(readSupplierItem(id));
    }
    
    public List<SupplierItem> listSupplierItemsAllField(int first, int pageSize, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SupplierItem> query = cb.createQuery(SupplierItem.class);
        Root<SupplierItem> root = query.from(SupplierItem.class);
        CriteriaQuery<SupplierItem> select = query.select(root).distinct(true);

        if (search != null && !search.isEmpty())
            query.where(cb.or(
                    cb.like(cb.lower(root.get(SupplierItem_.code)), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.join(SupplierItem_.item).get(Item_.description)), "%" + search.toLowerCase() + "%")));
        
        query.orderBy(cb.asc(root.get(SupplierItem_.code)));
        
        TypedQuery<SupplierItem> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }

    public List<SupplierItem> listSupplierItems(int first, int pageSize, String sortField, Boolean isAscending, String code, String supplier, String item) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SupplierItem> query = cb.createQuery(SupplierItem.class);
        Root<SupplierItem> root = query.from(SupplierItem.class);
        CriteriaQuery<SupplierItem> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, code, supplier, item);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(SupplierItem_.code));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "code":
                    path = root.get(SupplierItem_.code);
                    break;
                case "supplier":
                    path = root.get(SupplierItem_.supplier).get(CustomerSupplier_.name);
                    break;
                case "item":
                    path = root.get(SupplierItem_.item).get(Item_.description);
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
        
        TypedQuery<SupplierItem> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getSupplierItemsCount(String code, String supplier, String item) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SupplierItem> root = query.from(SupplierItem.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, code, supplier, item);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<SupplierItem> root, String code, String supplier, String item) {
        List<Predicate> conditions = new ArrayList<>();

        //code
        if (code != null && !code.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(SupplierItem_.code)), "%" + code.toLowerCase() + "%"));
        
        //supplier's name
        if (supplier != null && !supplier.isEmpty()) {
            Join<SupplierItem, CustomerSupplier> supplierRoot = root.join(SupplierItem_.supplier);
            conditions.add(cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + supplier.toLowerCase() + "%"));
        }
        
        //item's description
        if (item != null && !item.isEmpty()) {
            Join<SupplierItem, Item> itemRoot = root.join(SupplierItem_.item);
            conditions.add(cb.like(cb.lower(itemRoot.get(Item_.description)), "%" + item.toLowerCase() + "%"));
        }
        
        return conditions;
    }
}
