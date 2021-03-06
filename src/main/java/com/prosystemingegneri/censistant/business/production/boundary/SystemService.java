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
package com.prosystemingegneri.censistant.business.production.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class SystemService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    /**
     * Compose system description, so it can be hid from user
     * @param system System whos description must be composed
     * @return System with, as description, customer's name and job order's (string) number
     */
    public System composeSystemDescription(System system) {
        system.setDescription(
                system.getOffers().get(system.getOffers().size() - 1).getSiteSurveyReport().getRequest().getCustomer().getName()
                +
                " - "
                +
                system.getOffers().get(system.getOffers().size() - 1).getJobOrder().getJobOrderNumberAddress());
        
        return system;
    }
    
    public System saveSystem(System system) {
        system = composeSystemDescription(system);
        
        if (system.getId() == null)
            em.persist(system);
        else
            return em.merge(system);
        
        return system;
    }
    
    public System readSystem(Long id) {
        return em.find(System.class, id);
    }
    
    public void deleteSystem(Long id) {
        em.remove(readSystem(id));
    }

    public List<System> listSystems(int first, int pageSize, String sortField, Boolean isAscending, String description, CustomerSupplier customer, Plant plant) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<System> query = cb.createQuery(System.class);
        Root<System> root = query.from(System.class);
        CriteriaQuery<System> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, description, customer, plant);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(System_.description));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "description":
                    path = root.get(System_.description);
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
        
        TypedQuery<System> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);   
        }

        return typedQuery.getResultList();
    }
    
    public Long getSystemsCount(String description, CustomerSupplier customer, Plant plant) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<System> root = query.from(System.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, description, customer, plant);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<System> root, String description, CustomerSupplier customer, Plant plant) {
        List<Predicate> conditions = new ArrayList<>();

        //description
        if (description != null && !description.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(System_.description)), "%" + description.toLowerCase() + "%"));
        
        //Customer
        if (customer != null) {
            ListJoin<System, Offer> offersRoot = root.join(System_.offers);
            conditions.add(cb.isNotNull(offersRoot.get(Offer_.jobOrder)));
            conditions.add(cb.equal(
                    offersRoot
                    .join(Offer_.siteSurveyReport)
                    .join(SiteSurveyReport_.plant)
                    .join(Plant_.customerSupplier), customer));
        }
        
        //Plant
        if (plant != null) {
            ListJoin<System, Offer> offersRoot = root.join(System_.offers);
            conditions.add(cb.isNotNull(offersRoot.get(Offer_.jobOrder)));
            conditions.add(cb.equal(
                    offersRoot
                    .join(Offer_.siteSurveyReport)
                    .get(SiteSurveyReport_.plant), plant));
        }
        
        return conditions;
    }
}
