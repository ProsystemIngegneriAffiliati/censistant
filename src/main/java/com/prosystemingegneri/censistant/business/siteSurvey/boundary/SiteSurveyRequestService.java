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
package com.prosystemingegneri.censistant.business.siteSurvey.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class SiteSurveyRequestService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public SiteSurveyRequest createNewSiteSurveyRequest() {
        return new SiteSurveyRequest(getNextNumber());
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        query.select(cb.greatest(root.get(SiteSurveyRequest_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(SiteSurveyRequest_.creation), dateStart.getTime(), dateEnd.getTime()));

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
    
    public SiteSurveyRequest saveSiteSurveyRequest(SiteSurveyRequest siteSurveyRequest) {        
        if (siteSurveyRequest.getId() == null)
            em.persist(siteSurveyRequest);
        else
            return em.merge(siteSurveyRequest);
        
        return null;
    }
    
    public SiteSurveyRequest readSiteSurveyRequest(Long id) {
        return em.find(SiteSurveyRequest.class, id);
    }
    
    public void deleteSiteSurveyRequest(Long id) {
        em.remove(readSiteSurveyRequest(id));
    }

    public List<SiteSurveyRequest> listSiteSurveyRequests(int first, int pageSize, Map<String, Object> filters, String sortField, Boolean isAscending, Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SiteSurveyRequest> query = cb.createQuery(SiteSurveyRequest.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        CriteriaQuery<SiteSurveyRequest> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(SiteSurveyRequest_.creation), start, end));
        
        if (filters != null && !filters.isEmpty()) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();

                if (filterProperty.equalsIgnoreCase("customer") || filterProperty.equalsIgnoreCase("systemType") || filterProperty.equalsIgnoreCase("isInfo")) {
                    if (!filterProperty.isEmpty()) {
                        if (filterProperty.equalsIgnoreCase("customer")) {
                            Join<SiteSurveyRequest, CustomerSupplier> customerRoot = root.join(SiteSurveyRequest_.customer);
                            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
                        }
                        if (filterProperty.equalsIgnoreCase("systemType")) {
                            Join<SiteSurveyRequest, SystemType> systemTypeRoot = root.join(SiteSurveyRequest_.systemType);
                            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
                        }
                        if (filterProperty.equalsIgnoreCase("isInfo")) {
                            Boolean isInfo = Boolean.valueOf(String.valueOf(filters.get(filterProperty)));
                            if (isInfo != null)
                                conditions.add(cb.equal(root.get(SiteSurveyRequest_.isInfo), isInfo));
                        }
                    }
                }
                else
                    conditions.add(cb.like(cb.lower(root.get(filterProperty)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
            }
        }

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            if (sortField.equalsIgnoreCase("customer") || sortField.equalsIgnoreCase("systemType")) {
                if (sortField.equalsIgnoreCase("customer")) {
                    Join<SiteSurveyRequest, CustomerSupplier> customerRoot = root.join(SiteSurveyRequest_.customer);
                    if (isAscending)
                        query.orderBy(cb.asc(customerRoot.get(CustomerSupplier_.name)));
                    else
                        query.orderBy(cb.desc(customerRoot.get(CustomerSupplier_.name)));
                }
                if (sortField.equalsIgnoreCase("systemType")) {
                    Join<SiteSurveyRequest, SystemType> systemTypeRoot = root.join(SiteSurveyRequest_.systemType);
                    if (isAscending)
                        query.orderBy(cb.asc(systemTypeRoot.get(SystemType_.name)));
                    else
                        query.orderBy(cb.desc(systemTypeRoot.get(SystemType_.name)));
                }
            }
            else {
                if (isAscending)
                    query.orderBy(cb.asc(root.get(sortField)));
                else
                    query.orderBy(cb.desc(root.get(sortField)));
            }
        }
        
        TypedQuery<SiteSurveyRequest> typedQuery = em.createQuery(select);
        typedQuery.setMaxResults(pageSize);
        typedQuery.setFirstResult(first);

        return typedQuery.getResultList();
    }
    
    public Long getSiteSurveyRequestsCount(Map<String, Object> filters, Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = new ArrayList<>();
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(SiteSurveyRequest_.creation), start, end));
        
        if (filters != null && !filters.isEmpty()) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();
                if (filterProperty.equalsIgnoreCase("customer") || filterProperty.equalsIgnoreCase("systemType") || filterProperty.equalsIgnoreCase("isInfo")) {
                    if (!filterProperty.isEmpty()) {
                        if (filterProperty.equalsIgnoreCase("customer")) {
                            Join<SiteSurveyRequest, CustomerSupplier> customerRoot = root.join(SiteSurveyRequest_.customer);
                            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
                        }
                        if (filterProperty.equalsIgnoreCase("systemType")) {
                            Join<SiteSurveyRequest, SystemType> systemTypeRoot = root.join(SiteSurveyRequest_.systemType);
                            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
                        }
                        if (filterProperty.equalsIgnoreCase("isInfo")) {
                            Boolean isInfo = Boolean.valueOf(String.valueOf(filters.get(filterProperty)));
                            if (isInfo != null)
                                conditions.add(cb.equal(root.get(SiteSurveyRequest_.isInfo), isInfo));
                        }
                    }
                }
                else
                    conditions.add(cb.like(cb.lower(root.get(filterProperty)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
            }
        }

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }

        return em.createQuery(select).getSingleResult();
    }
}
