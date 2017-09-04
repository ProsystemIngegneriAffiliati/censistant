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
package com.prosystemingegneri.censistant.business.customerSupplier.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class CustomerSupplierService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public CustomerSupplier saveCustomerSupplier(CustomerSupplier customerSupplier) {        
        if (customerSupplier.getId() == null)
            em.persist(customerSupplier);
        else
            return em.merge(customerSupplier);
        
        return null;
    }
    
    public CustomerSupplier readCustomerSupplier(Long id) {
        return em.find(CustomerSupplier.class, id);
    }
    
    public void deleteCustomerSupplier(Long id) {
        em.remove(readCustomerSupplier(id));
    }

    public List<CustomerSupplier> listCustomerSuppliers(int first, int pageSize, Map<String, Object> filters, String sortField, Boolean isAscending, Boolean isCustomer, Boolean isSupplier, String address) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CustomerSupplier> query = cb.createQuery(CustomerSupplier.class);
        Root<CustomerSupplier> root = query.from(CustomerSupplier.class);
        CriteriaQuery<CustomerSupplier> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //is customer
        if (isCustomer != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isCustomer), isCustomer));
        
        //is supplier
        if (isSupplier != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isSupplier), isSupplier));
        
        //plant's address
        if (address != null && !address.isEmpty()) {
            ListJoin<CustomerSupplier, Plant> plants = root.join(CustomerSupplier_.plants);
            conditions.add(cb.like(cb.lower(plants.get(Plant_.address)), "%" + String.valueOf(address).toLowerCase() + "%"));
    	}
        
        if (filters != null && !filters.isEmpty()) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();
                conditions.add(cb.like(cb.lower(root.get(filterProperty)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
            }
        }

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            if (isAscending)
                query.orderBy(cb.asc(root.get(sortField)));
            else
                query.orderBy(cb.desc(root.get(sortField)));
        }
        
        TypedQuery<CustomerSupplier> typedQuery = em.createQuery(select);
        typedQuery.setMaxResults(pageSize);
        typedQuery.setFirstResult(first);

        return typedQuery.getResultList();
    }
    
    public Long getCustomerSuppliersCount(Map<String, Object> filters, Boolean isCustomer, Boolean isSupplier, String address) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CustomerSupplier> root = query.from(CustomerSupplier.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = new ArrayList<>();
        
        //is customer
        if (isCustomer != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isCustomer), isCustomer));
        
        //is supplier
        if (isSupplier != null)
            conditions.add(cb.equal(root.get(CustomerSupplier_.isSupplier), isSupplier));
        
        //plant's address
        if (address != null && !address.isEmpty()) {
            ListJoin<CustomerSupplier, Plant> plants = root.join(CustomerSupplier_.plants);
            conditions.add(cb.like(cb.lower(plants.get(Plant_.address)), "%" + String.valueOf(address).toLowerCase() + "%"));
    	}
        
        if (filters != null && !filters.isEmpty()) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();
                conditions.add(cb.like(cb.lower(root.get(filterProperty)), "%" + String.valueOf(filters.get(filterProperty)).toLowerCase() + "%"));
            }
        }

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }

        return em.createQuery(select).getSingleResult();
    }
}
