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
package com.prosystemingegneri.censistant.business.deliveryNote.boundary;

import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class ShippingPaymentService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public ShippingPayment saveShippingPayment(ShippingPayment shippingPayment) {        
        if (shippingPayment.getId() == null)
            em.persist(shippingPayment);
        else
            return em.merge(shippingPayment);
        
        return shippingPayment;
    }
    
    public ShippingPayment readShippingPayment(Long id) {
        return em.find(ShippingPayment.class, id);
    }
    
    public void deleteShippingPayment(Long id) {
        em.remove(readShippingPayment(id));
    }

    public List<ShippingPayment> listShippingPayments(int first, int pageSize, String sortField, Boolean isAscending, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ShippingPayment> query = cb.createQuery(ShippingPayment.class);
        Root<ShippingPayment> root = query.from(ShippingPayment.class);
        CriteriaQuery<ShippingPayment> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(ShippingPayment_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(ShippingPayment_.name);
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
        
        TypedQuery<ShippingPayment> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getShippingPaymentsCount(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ShippingPayment> root = query.from(ShippingPayment.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<ShippingPayment> root, String name) {
        List<Predicate> conditions = new ArrayList<>();

        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(ShippingPayment_.name)), "%" + name.toLowerCase() + "%"));
        
        return conditions;
    }
}
