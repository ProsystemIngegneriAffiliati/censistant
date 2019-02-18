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

import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason_;
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
public class ShipmentReasonService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public ShipmentReason saveShipmentReason(ShipmentReason shipmentReason) {        
        if (shipmentReason.getId() == null)
            em.persist(shipmentReason);
        else
            return em.merge(shipmentReason);
        
        return shipmentReason;
    }
    
    public ShipmentReason readShipmentReason(Long id) {
        return em.find(ShipmentReason.class, id);
    }
    
    public void deleteShipmentReason(Long id) {
        em.remove(readShipmentReason(id));
    }

    public List<ShipmentReason> listShipmentReasons(int first, int pageSize, String sortField, Boolean isAscending, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ShipmentReason> query = cb.createQuery(ShipmentReason.class);
        Root<ShipmentReason> root = query.from(ShipmentReason.class);
        CriteriaQuery<ShipmentReason> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(ShipmentReason_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(ShipmentReason_.name);
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
        
        TypedQuery<ShipmentReason> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getShipmentReasonsCount(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ShipmentReason> root = query.from(ShipmentReason.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<ShipmentReason> root, String name) {
        List<Predicate> conditions = new ArrayList<>();

        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(ShipmentReason_.name)), "%" + name.toLowerCase() + "%"));
        
        return conditions;
    }
}
