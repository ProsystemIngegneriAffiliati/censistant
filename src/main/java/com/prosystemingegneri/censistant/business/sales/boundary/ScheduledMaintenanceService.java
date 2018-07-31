/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.sales.boundary;

import com.prosystemingegneri.censistant.business.sales.entity.ScheduledMaintenance;
import com.prosystemingegneri.censistant.business.sales.entity.ScheduledMaintenance_;
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
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class ScheduledMaintenanceService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public ScheduledMaintenance saveScheduledMaintenance(ScheduledMaintenance scheduledMaintenance) {
        if (scheduledMaintenance.getId() == null)
            em.persist(scheduledMaintenance);
        else
            return em.merge(scheduledMaintenance);

        return scheduledMaintenance;
    }
    
    public ScheduledMaintenance readScheduledMaintenance(Long id) {
        return em.find(ScheduledMaintenance.class, id);
    }
    
    public void deleteScheduledMaintenance(Long id) {
        em.remove(readScheduledMaintenance(id));
    }

    public List<ScheduledMaintenance> listScheduledMaintenances(int first, int pageSize, String sortField, Boolean isAscending, String name, String description) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduledMaintenance> query = cb.createQuery(ScheduledMaintenance.class);
        Root<ScheduledMaintenance> root = query.from(ScheduledMaintenance.class);
        CriteriaQuery<ScheduledMaintenance> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, name, description);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(ScheduledMaintenance_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(ScheduledMaintenance_.name);
                    break;
                case "description":
                    path = root.get(ScheduledMaintenance_.description);
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
        
        TypedQuery<ScheduledMaintenance> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getScheduledMaintenancesCount(String name, String description) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ScheduledMaintenance> root = query.from(ScheduledMaintenance.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, name, description);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<ScheduledMaintenance> root, String name, String description) {
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(ScheduledMaintenance_.name)), "%" + name.toLowerCase() + "%"));
        
        //description
        if (description != null && !description.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(ScheduledMaintenance_.description)), "%" + description.toLowerCase() + "%"));
        
        return conditions;
    }
}
