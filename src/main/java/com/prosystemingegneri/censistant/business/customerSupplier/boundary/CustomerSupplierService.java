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
package com.prosystemingegneri.censistant.business.customerSupplier.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem_;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem_;
import com.prosystemingegneri.censistant.business.sales.boundary.BusinessCommunicationService;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class CustomerSupplierService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    JobOrderService jobOrderService;
    
    @Inject
    private BusinessCommunicationService businessCommunicationService;
    
    public CustomerSupplier createCustomer() {
        CustomerSupplier customer = new CustomerSupplier(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        customer.addPlant(new Plant(Boolean.TRUE, "Sede", null));
        
        return customer;
    }
    
    public CustomerSupplier createPotentialCustomer() {
        CustomerSupplier potentialCustomer = new CustomerSupplier(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        potentialCustomer.addPlant(new Plant(Boolean.TRUE, "Sede", null));
        
        return potentialCustomer;
    }
    
    
    
    public CustomerSupplier createSupplier() {
        CustomerSupplier supplier = new CustomerSupplier(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        supplier.addPlant(new Plant(Boolean.TRUE, "Sede", "To be defined"));
        
        return supplier;
    }
    
    public CustomerSupplier saveCustomerSupplier(CustomerSupplier customerSupplier) {
        for (Plant plant : customerSupplier.getPlants())
            plant.checkSupplierPlantLocation();

        if (customerSupplier.getId() == null)
            em.persist(customerSupplier);
        else
            return em.merge(customerSupplier);
        
        return customerSupplier;
    }
    
    public CustomerSupplier readCustomerSupplier(Long id) {
        return em.find(CustomerSupplier.class, id);
    }
    
    public void deleteCustomerSupplier(Long id) {
        em.remove(readCustomerSupplier(id));
    }

    public List<CustomerSupplier> listCustomerSuppliers(int first, int pageSize, String sortField, Boolean isAscending, Boolean isPotentialCustomer, Boolean isOnlyInfo, Boolean isCustomer, Boolean isSupplier, String businessName, String name, String address) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CustomerSupplier> query = cb.createQuery(CustomerSupplier.class);
        Root<CustomerSupplier> root = query.from(CustomerSupplier.class);
        CriteriaQuery<CustomerSupplier> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, query.subquery(CustomerSupplier.class), root, isPotentialCustomer, isOnlyInfo, isCustomer, isSupplier, businessName, name, address);

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        query.orderBy(cb.desc(root.get(CustomerSupplier_.id)));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            if (isAscending)
                query.orderBy(cb.asc(root.get(sortField)));
            else
                query.orderBy(cb.desc(root.get(sortField)));
        }
        
        TypedQuery<CustomerSupplier> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);   
        }
        
        List<CustomerSupplier> result = typedQuery.getResultList();
        for (CustomerSupplier customerSupplier : result)
            customerSupplier.setLastEmailSent(businessCommunicationService.getLastEmailSent(customerSupplier));

        return result;
    }
    
    public Long getCustomerSuppliersCount(Boolean isPotentialCustomer, Boolean isOnlyInfo, Boolean isCustomer, Boolean isSupplier, String businessName, String name, String address) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CustomerSupplier> root = query.from(CustomerSupplier.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, query.subquery(CustomerSupplier.class), root, isPotentialCustomer, isOnlyInfo, isCustomer, isSupplier, businessName, name, address);

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Subquery<CustomerSupplier> subquery, Root<CustomerSupplier> root, Boolean isPotentialCustomer, Boolean isOnlyInfo, Boolean isCustomer, Boolean isSupplier, String businessName, String name, String address) {
        List<Predicate> conditions = new ArrayList<>();

        //is potential customer
        if (isPotentialCustomer != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isPotentialCustomer), isPotentialCustomer));
        
        //is the customer an only info one
        if (isOnlyInfo != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isOnlyInfo), isOnlyInfo));
        
        /*
        //has (the customer) an only-info site survey request
        if (isOnlyInfo != null) {
            Path<Object> path = root.get(CustomerSupplier_.id.getName()); // field to map with sub-query
            Root<SiteSurveyRequest> subRoot = subquery.from(SiteSurveyRequest.class);
            subquery.select(subRoot.get(SiteSurveyRequest_.customer.getName()).get(CustomerSupplier_.id.getName())); // field to map with main-query
            subquery.where(cb.equal(subRoot.get(SiteSurveyRequest_.isInfo), isOnlyInfo));

            conditions.add(cb.in(path).value(subquery));
        }*/
        
        //is customer
        if (isCustomer != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isCustomer), isCustomer));
        
        //is supplier
        if (isSupplier != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isSupplier), isSupplier));

        //business name
        if (businessName != null && !businessName.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(CustomerSupplier_.businessName)), "%" + String.valueOf(businessName).toLowerCase() + "%"));
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(CustomerSupplier_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        //plant's address
        if (address != null && !address.isEmpty()) {
            ListJoin<CustomerSupplier, Plant> plants = root.join(CustomerSupplier_.plants);
            conditions.add(cb.like(cb.lower(plants.get(Plant_.address)), "%" + String.valueOf(address).toLowerCase() + "%"));
    	}
        
        return conditions;
    }
    
    public List<Plant> listPlants(int first, int pageSize, String sortField, Boolean isAscending, CustomerSupplier customerSupplier, String address, String nameAddress) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Plant> query = cb.createQuery(Plant.class);
        Root<Plant> root = query.from(Plant.class);
        CriteriaQuery<Plant> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();

        //Customer or supplier
        if (customerSupplier != null)
            conditions.add(cb.equal(root.get(Plant_.customerSupplier), customerSupplier));
        
        //Address
        if (address != null)
            conditions.add(cb.like(cb.lower(root.get(Plant_.address)), "%" + String.valueOf(address).toLowerCase() + "%"));
        
        //Name or address
        if (nameAddress != null)
            conditions.add(
                    cb.or(
                            cb.like(cb.lower(root.get(Plant_.address)), "%" + String.valueOf(nameAddress).toLowerCase() + "%"),
                            cb.like(cb.lower(root.get(Plant_.name)), "%" + String.valueOf(nameAddress).toLowerCase() + "%")));

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            if (isAscending)
                query.orderBy(cb.asc(root.get(sortField)));
            else
                query.orderBy(cb.desc(root.get(sortField)));
        }
        
        TypedQuery<Plant> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public List<BoxedItem> listSupplierBoxedItems(int first, int pageSize, CustomerSupplier supplier, String filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BoxedItem> query = cb.createQuery(BoxedItem.class);
        Root<BoxedItem> root = query.from(BoxedItem.class);
        CriteriaQuery<BoxedItem> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();

        //Supplier
        if (supplier != null) {
            Join<BoxedItem, SupplierItem> supplierItemRoot = root.join(BoxedItem_.item);
            conditions.add(cb.equal(supplierItemRoot.get(SupplierItem_.supplier), supplier));
        }
        
        //Filter for supplierItem's code or supplierItem's description
        if (filter != null && !filter.isEmpty()) {
            Join<BoxedItem, SupplierItem> supplierItemRoot = root.join(BoxedItem_.item);
            conditions.add(cb.or(
                    cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.code)), "%" + filter.toLowerCase() + "%"), 
                    cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.description)), "%" + filter.toLowerCase() + "%")));
        }

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        query.orderBy(cb.asc(root.get(BoxedItem_.item).get(SupplierItem_.description)));
        
        TypedQuery<BoxedItem> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
}
