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
package com.prosystemingegneri.censistant.business.sales.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
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

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class JobOrderService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    CustomerSupplierService customerSupplierService;
    
    @Inject
    OfferService offerService;
    
    @Inject
    SystemService systemService;
    
    public JobOrder createNewJobOrder(Offer offer) {
        JobOrder jobOrder = new JobOrder(getNextNumber());
        if (offer == null)
            offer = offerService.createNewOffer();
        jobOrder.addOffer(offer);
        
        return jobOrder;
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<JobOrder> root = query.from(JobOrder.class);
        query.select(cb.greatest(root.get(JobOrder_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(JobOrder_.creation), dateStart.getTime(), dateEnd.getTime()));

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
    
    public JobOrder saveJobOrder(JobOrder jobOrder) {
        if (jobOrder.getOffer().getId() == null)
            offerService.saveOffer(jobOrder.getOffer());
        
        if (jobOrder.getId() == null && jobOrder.getOffer().getSystem().getId() != null) {
            em.persist(jobOrder);
            systemService.saveSystem(jobOrder.getOffer().getSystem());
        }
        else {
            systemService.saveSystem(jobOrder.getOffer().getSystem());

            if (jobOrder.getId() == null)
                em.persist(jobOrder);
            else
                jobOrder = em.merge(jobOrder);
        }
        
        CustomerSupplier customer = jobOrder.getOffer().getSiteSurveyReport().getPlant().getCustomerSupplier();
        if (customer.getIsPotentialCustomer())
            customer.setIsPotentialCustomer(Boolean.FALSE);
        customerSupplierService.saveCustomerSupplier(customer);
        
        return jobOrder;
    }
    
    public JobOrder readJobOrder(Long id) {
        return em.find(JobOrder.class, id);
    }
    
    public void deleteJobOrder(Long id) {
        em.remove(readJobOrder(id));
    }

    public List<JobOrder> listJobOrders(int first, int pageSize, String sortField, Boolean isAscending, Integer number, String customerName, CustomerSupplier customer, String plantAddress, String systemType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JobOrder> query = cb.createQuery(JobOrder.class);
        Root<JobOrder> root = query.from(JobOrder.class);
        CriteriaQuery<JobOrder> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, customerName, customer, plantAddress, systemType);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(JobOrder_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "creation":
                    path = root.get(JobOrder_.creation);
                    break;
                case "number":
                    path = root.get(JobOrder_.number);
                    break;
                case "customerName":
                    path = root.get(JobOrder_.offer).get(Offer_.siteSurveyReport).get(SiteSurveyReport_.plant).get(Plant_.customerSupplier).get(CustomerSupplier_.name);
                    break;
                case "plantAddress":
                    path = root.get(JobOrder_.offer).get(Offer_.siteSurveyReport).get(SiteSurveyReport_.plant).get(Plant_.address);
                    break;
                case "systemType":
                    path = root.get(JobOrder_.offer).get(Offer_.siteSurveyReport).get(SiteSurveyReport_.request).get(SiteSurveyRequest_.systemType).get(SystemType_.name);
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
        
        TypedQuery<JobOrder> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getJobOrdersCount(Integer number, String customerName, CustomerSupplier customer, String plantAddress, String systemType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<JobOrder> root = query.from(JobOrder.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, customerName, customer, plantAddress, systemType);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        try {
            return em.createQuery(select).getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<JobOrder> root, Integer number, String customerName, CustomerSupplier customer, String plantAddress, String systemType) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(JobOrder_.number), number));
        
        //customer's name
        if (customerName != null && !customerName.isEmpty()) {
            Join<JobOrder, Offer> offerRoot = root.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier);
            
            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"));
        }
        
        //customer
        if (customer != null) {
            Join<JobOrder, Offer> offerRoot = root.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            
            conditions.add(cb.equal(plantRoot.get(Plant_.customerSupplier), customer));
        }
        
        //plant's address
        if (plantAddress != null && !plantAddress.isEmpty()) {
            Join<JobOrder, Offer> offerRoot = root.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            
            conditions.add(cb.like(cb.lower(plantRoot.get(Plant_.address)), "%" + plantAddress.toLowerCase() + "%"));
        }
        
        //system type's name
        if (systemType != null && !systemType.isEmpty()) {
            Join<JobOrder, Offer> offerRoot = root.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, SiteSurveyRequest> siteSurveyRequestRoot = siteSurveyReportRoot.join(SiteSurveyReport_.request);
            Join<SiteSurveyRequest, SystemType> systemTypeRoot = siteSurveyRequestRoot.join(SiteSurveyRequest_.systemType);
            
            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + systemType.toLowerCase() + "%"));
        }
        
        return conditions;
    }
}
