/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class OfferService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    SiteSurveyReportService siteSurveyReportService;
    
    @Inject
    SystemService systemService;
    
    public Offer createNewOffer() {
        Offer offer = new Offer(getNextNumber());
        SiteSurveyReport siteSurveyReport = siteSurveyReportService.createNewSiteSurveyReport();
        siteSurveyReport.setActual(new Date());
        siteSurveyReport.setExpected(new Date());
        offer.addSiteSurveyReport(siteSurveyReport);
        
        System system = new System();
        system.addOffer(offer);
        
        return offer;
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<Offer> root = query.from(Offer.class);
        query.select(cb.greatest(root.get(Offer_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(Offer_.creation), dateStart.getTime(), dateEnd.getTime()));

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
    
    public Offer saveOffer(Offer offer) {
        if (offer.getSiteSurveyReport().getId() == null)
            siteSurveyReportService.saveSiteSurveyReport(offer.getSiteSurveyReport());
        
        if (offer.getId() == null && offer.getSystem().getId() != null) {
            em.persist(offer);
            systemService.saveSystem(offer.getSystem());
        }
        else {
            systemService.saveSystem(offer.getSystem());

            if (offer.getId() == null)
                em.persist(offer);
            else
                return em.merge(offer);
        }
        
        return offer;
    }
    
    public Offer readOffer(Long id) {
        return em.find(Offer.class, id);
    }
    
    public void deleteOffer(Long id) {
        em.remove(readOffer(id));
    }

    public List<Offer> listOffers(int first, int pageSize, String sortField, Boolean isAscending, Integer number, String customerName, String plantAddress, String systemType, Boolean isAssociatedToJobOrder) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Offer> query = cb.createQuery(Offer.class);
        Root<Offer> root = query.from(Offer.class);
        CriteriaQuery<Offer> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, customerName, plantAddress, systemType, isAssociatedToJobOrder);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(Offer_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "creation":
                    path = root.get(Offer_.creation);
                    break;
                case "number":
                    path = root.get(Offer_.number);
                    break;
                case "customerName":
                    path = root.get(Offer_.siteSurveyReport).get(SiteSurveyReport_.plant).get(Plant_.customerSupplier).get(CustomerSupplier_.name);
                    break;
                case "plantAddress":
                    path = root.get(Offer_.siteSurveyReport).get(SiteSurveyReport_.plant).get(Plant_.address);
                    break;
                case "systemType":
                    path = root.get(Offer_.siteSurveyReport).get(SiteSurveyReport_.request).get(SiteSurveyRequest_.systemType).get(SystemType_.name);
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
        
        TypedQuery<Offer> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getOffersCount(Integer number, String customerName, String plantAddress, String systemType, Boolean isAssociatedToJobOrder) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Offer> root = query.from(Offer.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, customerName, plantAddress, systemType, isAssociatedToJobOrder);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<Offer> root, Integer number, String customerName, String plantAddress, String systemType, Boolean isAssociatedToJobOrder) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(Offer_.number), number));
        
        //customer's name
        if (customerName != null && !customerName.isEmpty()) {
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = root.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier);
            
            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"));
        }
        
        //plant's address
        if (plantAddress != null && !plantAddress.isEmpty()) {
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = root.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            
            conditions.add(cb.like(cb.lower(plantRoot.get(Plant_.address)), "%" + plantAddress.toLowerCase() + "%"));
        }
        
        //system type's name
        if (systemType != null && !systemType.isEmpty()) {
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = root.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, SiteSurveyRequest> siteSurveyRequestRoot = siteSurveyReportRoot.join(SiteSurveyReport_.request);
            Join<SiteSurveyRequest, SystemType> systemTypeRoot = siteSurveyRequestRoot.join(SiteSurveyRequest_.systemType);
            
            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + systemType.toLowerCase() + "%"));
        }
        
        //is associated to job order
        if (isAssociatedToJobOrder != null) {
            Join<Offer, JobOrder> jobOrderRoot = root.join(Offer_.jobOrder, JoinType.LEFT);
            if (isAssociatedToJobOrder)
                conditions.add(cb.isNotNull(jobOrderRoot));
            else
                conditions.add(cb.isNull(jobOrderRoot));
        }
        
        return conditions;
    }
}
