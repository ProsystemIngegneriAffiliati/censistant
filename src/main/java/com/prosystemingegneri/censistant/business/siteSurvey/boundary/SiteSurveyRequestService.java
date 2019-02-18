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
package com.prosystemingegneri.censistant.business.siteSurvey.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class SiteSurveyRequestService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    private SystemTypeService systemTypeService;
    
    public SiteSurveyRequest createNewSiteSurveyRequest() {
        return new SiteSurveyRequest(getNextNumber());
    }
    
    public SiteSurveyRequest createNewSiteSurveyRequestOnlyInfo(CustomerSupplier customer) {
        SiteSurveyRequest siteSurveyRequest = createNewSiteSurveyRequest();
        siteSurveyRequest.setCustomer(customer);
        siteSurveyRequest.setIsInfo(Boolean.TRUE);
        siteSurveyRequest.setSystemType(systemTypeService.listSystemTypes(null).get(0));    //TODO add default system type
        em.persist(siteSurveyRequest);
        
        return siteSurveyRequest;
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
    
    public Date getMostRecentSiteSurveyRequestOnlyInfoCreation(@NotNull CustomerSupplier customer) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        query.select(cb.greatest(root.get(SiteSurveyRequest_.creation)));
        query.where(cb.and(
                cb.equal(root.get(SiteSurveyRequest_.customer), customer),
                cb.equal(root.get(SiteSurveyRequest_.isInfo), Boolean.TRUE)));
        
        Date result = null;
        try {
            result = em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
        }
        
	return result;
    }

    public List<SiteSurveyRequest> listSiteSurveyRequests(int first, int pageSize, String sortField, Boolean isAscending, Integer number, Date start, Date end, String customer, String systemType, Boolean isInfo, Boolean isReportPresent) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SiteSurveyRequest> query = cb.createQuery(SiteSurveyRequest.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        CriteriaQuery<SiteSurveyRequest> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, query.subquery(SiteSurveyReport.class), root, number, start, end, customer, systemType, isInfo, isReportPresent);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(SiteSurveyRequest_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "customer":
                    path = root.get(SiteSurveyRequest_.customer).get(CustomerSupplier_.name);
                    break;
                case "systemType":
                    path = root.get(SiteSurveyRequest_.systemType).get(SystemType_.name);
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
        
        TypedQuery<SiteSurveyRequest> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getSiteSurveyRequestsCount(Integer number, Date start, Date end, String customer, String systemType, Boolean isInfo, Boolean isReportPresent) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SiteSurveyRequest> root = query.from(SiteSurveyRequest.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, query.subquery(SiteSurveyReport.class), root, number, start, end, customer, systemType, isInfo, isReportPresent);
        
        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Subquery<SiteSurveyReport> subquery, Root<SiteSurveyRequest> root, Integer number, Date start, Date end, String customer, String systemType, Boolean isInfo, Boolean isReportPresent) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(SiteSurveyRequest_.number), number));
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(SiteSurveyRequest_.creation), start, end));
        
        //customer
        if (customer != null && !customer.isEmpty()) {
            Join<SiteSurveyRequest, CustomerSupplier> customerRoot = root.join(SiteSurveyRequest_.customer);
            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + customer.toLowerCase() + "%"));
        }
        
        //system type
        if (systemType != null && !systemType.isEmpty()) {
            Join<SiteSurveyRequest, SystemType> systemTypeRoot = root.join(SiteSurveyRequest_.systemType);
            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + systemType.toLowerCase() + "%"));
        }
        
        //is information
        if (isInfo != null)
            conditions.add(cb.equal(root.get(SiteSurveyRequest_.isInfo), isInfo));
        
        //is associated with site survey report
        if (isReportPresent != null) {
            Path<Object> path = root.get(SiteSurveyRequest_.id.getName()); // field to map with sub-query
            
            Root<SiteSurveyReport> subRoot = subquery.from(SiteSurveyReport.class);
            subquery.select(subRoot.get(SiteSurveyReport_.request.getName()).get(SiteSurveyRequest_.id.getName())); // field to map with main-query
            
            if (isReportPresent)
                conditions.add(cb.in(path).value(subquery));
            else
                conditions.add(cb.not(cb.in(path).value(subquery)));
        }
        
        return conditions;
    }
}
