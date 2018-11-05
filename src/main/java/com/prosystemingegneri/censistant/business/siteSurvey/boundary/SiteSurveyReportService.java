/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.sales.entity.BusinessCommunication;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker_;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class SiteSurveyReportService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    SiteSurveyRequestService siteSurveyRequestService;
    
    @Inject
    private CustomerSupplierService customerSupplierService;
    
    public SiteSurveyReport createNewSiteSurveyReport() {
        SiteSurveyReport report = new SiteSurveyReport(getNextNumber());
        report.addRequest(siteSurveyRequestService.createNewSiteSurveyRequest());
        report.addBusinessCommunication(new BusinessCommunication());
        
        return report;
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<SiteSurveyReport> root = query.from(SiteSurveyReport.class);
        query.select(cb.greatest(root.get(SiteSurveyReport_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(SiteSurveyReport_.expected), dateStart.getTime(), dateEnd.getTime()));

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
    
    public SiteSurveyReport saveSiteSurveyReport(SiteSurveyReport siteSurveyReport) {
        CustomerSupplier customer = siteSurveyReport.getRequest().getCustomer();
        if (siteSurveyReport.getBusinessCommunication().getIsOfferAccepted() && customer.getIsPotentialCustomer()) {
            customer.setIsPotentialCustomer(Boolean.FALSE);
        }
        if (customer.getBusinessCommunications().contains(siteSurveyReport.getBusinessCommunication()))
            customer.removeBusinessCommunication(siteSurveyReport.getBusinessCommunication());
        customer.addBusinessCommunication(siteSurveyReport.getBusinessCommunication());
        customerSupplierService.saveCustomerSupplier(customer);
        
        if (siteSurveyReport.getId() == null) {
            if (siteSurveyReport.getRequest().getId() == null)
                em.persist(siteSurveyReport.getRequest());
            em.persist(siteSurveyReport);
        }
        else
            return em.merge(siteSurveyReport);
        
        return siteSurveyReport;
    }
    
    public SiteSurveyReport readSiteSurveyReport(Long id) {
        return em.find(SiteSurveyReport.class, id);
    }
    
    public void deleteSiteSurveyReport(Long id) {
        em.remove(readSiteSurveyReport(id));
    }

    public List<SiteSurveyReport> listSiteSurveyReports(int first, int pageSize, String sortField, Boolean isAscending, Integer number, Date start, Date end, String customerName, CustomerSupplier customer, String systemType, String seller, String nameAddressPlant, Boolean isAssociatedToOffer) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SiteSurveyReport> query = cb.createQuery(SiteSurveyReport.class);
        Root<SiteSurveyReport> root = query.from(SiteSurveyReport.class);
        CriteriaQuery<SiteSurveyReport> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, customerName, customer, systemType, seller, nameAddressPlant, isAssociatedToOffer);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(SiteSurveyReport_.expected));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            Join<SiteSurveyReport, SiteSurveyRequest> requestRoot;
            switch (sortField) {
                case "customerName":
                    path = root.get(SiteSurveyReport_.request).get(SiteSurveyRequest_.customer).get(CustomerSupplier_.name);
                    break;
                case "systemType":
                    path = root.get(SiteSurveyReport_.request).get(SiteSurveyRequest_.systemType).get(SystemType_.name);
                    break;
                case "seller":
                    path = root.get(SiteSurveyReport_.seller).get(Worker_.name);
                    break;
                case "nameAddressPlant":
                    path = root.get(SiteSurveyReport_.plant).get(Plant_.address);
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
        
        TypedQuery<SiteSurveyReport> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getSiteSurveyReportsCount(Integer number, Date start, Date end, String customerName, CustomerSupplier customer, String systemType, String seller, String nameAddressPlant, Boolean isAssociatedToOffer) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SiteSurveyReport> root = query.from(SiteSurveyReport.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, customerName, customer, systemType, seller, nameAddressPlant, isAssociatedToOffer);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<SiteSurveyReport> root, Integer number, Date start, Date end, String customerName, CustomerSupplier customer, String systemType, String seller, String nameAddressPlant, Boolean isAssociatedToOffer) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(SiteSurveyReport_.number), number));
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(SiteSurveyReport_.expected), start, end));
        
        //customer name
        if (customerName != null && !customerName.isEmpty()) {
            Join<SiteSurveyReport, SiteSurveyRequest> requestRoot = root.join(SiteSurveyReport_.request);
            Join<SiteSurveyRequest, CustomerSupplier> customerRoot = requestRoot.join(SiteSurveyRequest_.customer);
            conditions.add(cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"));
        }
        
        //customer entity
        if (customer != null)
            conditions.add(cb.equal(root
                    .join(SiteSurveyReport_.request)
                    .get(SiteSurveyRequest_.customer), customer));
        
        //system type
        if (systemType != null && !systemType.isEmpty()) {
            Join<SiteSurveyReport, SiteSurveyRequest> requestRoot = root.join(SiteSurveyReport_.request);
            Join<SiteSurveyRequest, SystemType> systemTypeRoot = requestRoot.join(SiteSurveyRequest_.systemType);
            conditions.add(cb.like(cb.lower(systemTypeRoot.get(SystemType_.name)), "%" + systemType.toLowerCase() + "%"));
        }
        
        //seller
        if (seller != null && !seller.isEmpty()) {
            Join<SiteSurveyReport, Worker> workerRoot = root.join(SiteSurveyReport_.seller);
            conditions.add(cb.like(cb.lower(workerRoot.get(Worker_.name)), "%" + seller.toLowerCase() + "%"));
        }
        
        //plant's address and name
        if (nameAddressPlant != null && !nameAddressPlant.isEmpty()) {
            Join<SiteSurveyReport, Plant> plantRoot = root.join(SiteSurveyReport_.plant);
            conditions.add(
                    cb.or(
                            cb.like(cb.lower(plantRoot.get(Plant_.address)), "%" + String.valueOf(nameAddressPlant).toLowerCase() + "%"),
                            cb.like(cb.lower(plantRoot.get(Plant_.name)), "%" + String.valueOf(nameAddressPlant).toLowerCase() + "%")));
        }
        
        //is associated to offer
        if (isAssociatedToOffer != null) {
            Join<SiteSurveyReport, Offer> offerRoot = root.join(SiteSurveyReport_.offer, JoinType.LEFT);
            if (isAssociatedToOffer)
                conditions.add(cb.isNotNull(offerRoot));
            else
                conditions.add(cb.isNull(offerRoot));
        }
        
        return conditions;
    }
}
