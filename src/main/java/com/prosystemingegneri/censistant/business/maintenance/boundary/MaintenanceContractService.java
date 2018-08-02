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
package com.prosystemingegneri.censistant.business.maintenance.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract_;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask_;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.criteria.Subquery;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class MaintenanceContractService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public MaintenanceContract saveMaintenanceContract(MaintenanceContract maintenanceContract) {
        if (maintenanceContract.getId() == null)
            em.persist(maintenanceContract);
        else
            return em.merge(maintenanceContract);

        return maintenanceContract;
    }
    
    public MaintenanceContract readMaintenanceContract(Long id) {
        return em.find(MaintenanceContract.class, id);
    }
    
    public void deleteMaintenanceContract(Long id) {
        em.remove(readMaintenanceContract(id));
    }

    public List<MaintenanceContract> listMaintenanceContracts(int first, int pageSize, String sortField, Boolean isAscending, Boolean isFullService, Boolean isOnCall, String customerName, Boolean isExpired, Boolean isCompleted) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenanceContract> query = cb.createQuery(MaintenanceContract.class);
        Root<MaintenanceContract> root = query.from(MaintenanceContract.class);
        CriteriaQuery<MaintenanceContract> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, query.subquery(MaintenanceTask.class), isFullService, isOnCall, customerName, isExpired, isCompleted);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(MaintenanceContract_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "creation":
                    path = root.get(MaintenanceContract_.creation);
                    break;
                case "expiry":
                    path = root.get(MaintenanceContract_.creation);
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
        
        TypedQuery<MaintenanceContract> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getMaintenanceContractsCount(Boolean isFullService, Boolean isOnCall, String customerName, Boolean isExpired, Boolean isCompleted) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MaintenanceContract> root = query.from(MaintenanceContract.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, query.subquery(MaintenanceTask.class), isFullService, isOnCall, customerName, isExpired, isCompleted);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<MaintenanceContract> root, Subquery<MaintenanceTask> subquery, Boolean isFullService, Boolean isOnCall, String customerName, Boolean isExpired, Boolean isCompleted) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Is full service
        if (isFullService != null)
            conditions.add(cb.equal(root.get(MaintenanceContract_.isFullService), isFullService));
        
        //Is on call
        if (isOnCall != null)
            conditions.add(cb.equal(root.get(MaintenanceContract_.isOnCall), isOnCall));
        
        //Customer name
        if (customerName != null && !customerName.isEmpty())
            conditions.add(cb.like(cb.lower(root
                    .join(MaintenanceContract_.systems)
                    .join(System_.offers)
                    .join(Offer_.siteSurveyReport)
                    .join(SiteSurveyReport_.plant)
                    .join(Plant_.customerSupplier)
                    .get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"));
        
        //Is expired
        if (isExpired != null) {
            Calendar createdOn = new GregorianCalendar();
            createdOn.add(Calendar.MONTH, MaintenanceContract.DURATION_MONTHS * -1);
            if (isExpired)
                conditions.add(cb.lessThan(root.<Date>get(MaintenanceContract_.creation), createdOn.getTime()));
            else
                conditions.add(cb.greaterThanOrEqualTo(root.<Date>get(MaintenanceContract_.creation), createdOn.getTime()));
        }
        
        //Is completed
        if (isCompleted != null) {
            Path<Object> path = root.get(MaintenanceTask_.id.getName()); // field to map with sub-query
            
            Root<MaintenanceTask> subRoot = subquery.from(MaintenanceTask.class);
            subquery.select(subRoot.get(MaintenanceTask_.maintenanceContract.getName()).get(MaintenanceTask_.id.getName())); // field to map with main-query
            
            if (isCompleted)
                subquery.where(cb.isNull(subRoot.get(MaintenanceTask_.closed)));
            else
                subquery.where(cb.isNotNull(subRoot.get(MaintenanceTask_.closed)));
            
            conditions.add(cb.not(cb.in(path).value(subquery)));
        }
        
        return conditions;
    }
}
