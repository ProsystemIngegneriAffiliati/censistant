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

import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.sales.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.sales.entity.MaintenanceTask_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
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
public class MaintenanceTaskService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public MaintenanceTask saveMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (maintenanceTask.getId() == null)
            em.persist(maintenanceTask);
        else
            return em.merge(maintenanceTask);

        return maintenanceTask;
    }
    
    public MaintenanceTask readMaintenanceTask(Long id) {
        return em.find(MaintenanceTask.class, id);
    }
    
    public void deleteMaintenanceTask(Long id) {
        em.remove(readMaintenanceTask(id));
    }

    public List<MaintenanceTask> listMaintenanceTasks(int first, int pageSize, String sortField, Boolean isAscending, System system, String description, Boolean isClosed, Worker worker) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenanceTask> query = cb.createQuery(MaintenanceTask.class);
        Root<MaintenanceTask> root = query.from(MaintenanceTask.class);
        CriteriaQuery<MaintenanceTask> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, system, description, isClosed, worker);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(MaintenanceTask_.expiry));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "description":
                    path = root.get(MaintenanceTask_.description);
                    break;
                case "created":
                    path = root.get(MaintenanceTask_.created);
                    break;
                case "expiry":
                    path = root.get(MaintenanceTask_.expiry);
                    break;
                case "closed":
                    path = root.get(MaintenanceTask_.closed);
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
        
        TypedQuery<MaintenanceTask> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getMaintenanceTasksCount(System system, String description, Boolean isClosed, Worker worker) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MaintenanceTask> root = query.from(MaintenanceTask.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, system, description, isClosed, worker);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<MaintenanceTask> root, System system, String description, Boolean isClosed, Worker worker) {
        List<Predicate> conditions = new ArrayList<>();
        
        //system
        if (system != null)
            conditions.add(cb.equal(root.get(MaintenanceTask_.system), system));
        
        //description
        if (description != null && !description.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(MaintenanceTask_.description)), "%" + description.toLowerCase() + "%"));
        
        //isClosed
        if (isClosed != null) {
            if (isClosed)
                conditions.add(cb.isNotNull(root.get(MaintenanceTask_.closed)));
            else
                conditions.add(cb.isNull(root.get(MaintenanceTask_.closed)));
        }
        
        //worker
        if (worker != null)
            conditions.add(cb.isMember(worker, root.get(MaintenanceTask_.workers)));
        
        return conditions;
    }
}
