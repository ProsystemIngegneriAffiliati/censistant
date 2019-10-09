/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.TimeSpent;
import com.prosystemingegneri.censistant.business.sales.entity.TimeSpent_;
import com.prosystemingegneri.censistant.business.sales.entity.WorkingOperation_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker_;
import com.prosystemingegneri.censistant.business.user.entity.UserApp_;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
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
public class TimeSpentService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Resource
    SessionContext ctx;
    
    public TimeSpent create() {
        return new TimeSpent();
    }
    
    public TimeSpent save(TimeSpent timeSpent) {
        if (timeSpent.getId() == null)
            em.persist(timeSpent);
        else
            return em.merge(timeSpent);
        
        return timeSpent;
    }
    
    public TimeSpent find(Long id) {
        return em.find(TimeSpent.class, id);
    }
    
    public void delete(Long id) {
        em.remove(find(id));
    }

    public List<TimeSpent> list(
            int first, int pageSize,
            String sortField, Boolean isAscending,
            JobOrder jobOrder,
            Worker worker, String workerName,
            String workingOperationName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimeSpent> query = cb.createQuery(TimeSpent.class);
        Root<TimeSpent> root = query.from(TimeSpent.class);
        CriteriaQuery<TimeSpent> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, jobOrder, worker, workerName, workingOperationName);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(TimeSpent_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "creation":
                    path = root.get(TimeSpent_.creation);
                    break;
                case "workerName":
                    path = root.get(TimeSpent_.worker).get(Worker_.name);
                    break;
                case "workingOperationName":
                    path = root.get(TimeSpent_.workingOperation).get(WorkingOperation_.name);
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
        
        TypedQuery<TimeSpent> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);   
        }

        return typedQuery.getResultList();
    }
    
    public Long getCount(
            JobOrder jobOrder,
            Worker worker, String workerName,
            String workingOperationName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TimeSpent> root = query.from(TimeSpent.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, jobOrder, worker, workerName, workingOperationName);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(
            CriteriaBuilder cb, Root<TimeSpent> root,
            JobOrder jobOrder,
            Worker worker, String workerName,
            String workingOperationName) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Job order entity
        if (jobOrder != null)
            conditions.add(cb.equal(root.get(TimeSpent_.jobOrder), jobOrder));
        
        //Worker's entity
        if (worker != null)
            conditions.add(cb.equal(root.get(TimeSpent_.worker), worker));
        
        //Worker's name
        if (workerName != null && !workerName.isEmpty())
            conditions.add(
                    cb.like(
                            cb.lower(
                                    root
                                            .join(TimeSpent_.worker)
                                            .get(Worker_.name)
                            )
                            , "%" + workerName.toLowerCase() + "%"
                    )
            );
        
        //Working operation name
        if (workingOperationName != null && !workingOperationName.isEmpty())
            conditions.add(
                    cb.like(
                            cb.lower(
                                    root
                                            .join(TimeSpent_.workingOperation)
                                            .get(WorkingOperation_.name)
                            )
                            , "%" + workingOperationName.toLowerCase() + "%"
                    )
            );
        
        //Results are limited for not-admin users
        if (ctx.getCallerPrincipal() != null)
            if (!ctx.isCallerInRole("admin"))
                conditions.add(
                        cb.and(
                                cb.like(
                                        root
                                                .join(TimeSpent_.worker)
                                                .join(Worker_.userApp)
                                                .get(UserApp_.userName),
                                        ctx.getCallerPrincipal().getName()
                                ),
                                cb.greaterThanOrEqualTo(root.<Date>get(TimeSpent_.creation), new Date())
                        )
                );
        
        return conditions;
    }
    
    public BigDecimal getTotalHoursSpent(
            JobOrder jobOrder,
            Worker worker, String workerName,
            String workingOperationName) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<TimeSpent> root = query.from(TimeSpent.class);
        CriteriaQuery<BigDecimal> select = query.select(cb.sum(root.get(TimeSpent_.hoursSpent)));

        List<Predicate> conditions = calculateConditions(cb, root, jobOrder, worker, workerName, workingOperationName);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
}
