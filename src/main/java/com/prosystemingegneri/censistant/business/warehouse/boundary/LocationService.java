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
package com.prosystemingegneri.censistant.business.warehouse.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.warehouse.control.LocationType;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location_;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class LocationService implements Serializable {
    @PersistenceContext
    EntityManager em;
    
    public Location readLocation(Long id) {
        return em.find(Location.class, id);
    }
    
    public List<Location> listLocations(int first, int pageSize, String sortField, Boolean isAscending, List<LocationType> locationTypes, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<Location> root = query.from(Location.class);
        CriteriaQuery<Location> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, locationTypes, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        /*
        Order order = cb.asc(root.get(Warehouse_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(Warehouse_.name);
                    break;
                default:
                    path = root.get(sortField);
            }
            if (isAscending)
                order = cb.asc(path);
            else
                order = cb.desc(path);
        }
        query.orderBy(order);*/
        
        TypedQuery<Location> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getLocationsCount(List<LocationType> locationTypes, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Location> root = query.from(Location.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, locationTypes, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<Location> root, List<LocationType> locationTypes, String name) {
        List<Predicate> conditions = new ArrayList<>();

        //name
        if (name != null && !name.isEmpty()) {
            Root<Warehouse> warehouseRoot = cb.treat(root, Warehouse.class);
            Root<SupplierPlantLocation> supplierLocationRoot = cb.treat(root, SupplierPlantLocation.class);
            Root<System> systemRoot = cb.treat(root, System.class);
            Join<System, Offer> offersRoot = systemRoot.join(System_.offers, JoinType.LEFT);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offersRoot.join(Offer_.siteSurveyReport, JoinType.LEFT);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant, JoinType.LEFT);
            Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier, JoinType.LEFT);
            Join<SupplierPlantLocation, Plant> supplierPlantRoot = supplierLocationRoot.join(SupplierPlantLocation_.plant, JoinType.LEFT);
            Join<Plant, CustomerSupplier> supplierRoot = supplierPlantRoot.join(Plant_.customerSupplier, JoinType.LEFT);
            
            conditions.add(cb.or(
                    cb.like(cb.lower(warehouseRoot.get(Warehouse_.name)), "%" + name.toLowerCase() + "%"),
                    cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + name.toLowerCase() + "%"),
                    cb.like(cb.lower(systemRoot.get(System_.description)), "%" + name.toLowerCase() + "%"),
                    cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + name.toLowerCase() + "%")));
        }
        
        //location type
        if (locationTypes != null && !locationTypes.isEmpty()) {
            List<Predicate> locationConditions = new ArrayList<>();
            for (LocationType locationType : locationTypes) {
                locationConditions.add(cb.equal(root.get(Location_.type), locationType));
            }
            conditions.add(cb.or(locationConditions.toArray(new Predicate[] {})));
        }
        
        return conditions;
    }
    
    public List<Location> listCustomerSupplierLocations(CustomerSupplier customerSupplier, String name) {
        if (customerSupplier == null)
            return new ArrayList<>();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<Location> root = query.from(Location.class);
        CriteriaQuery<Location> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        if (customerSupplier.getIsCustomer()) {
            Root<System> systemRoot = cb.treat(root, System.class);
            if (systemRoot != null) {
                Join<System, Offer> offersRoot = systemRoot.join(System_.offers, JoinType.LEFT);
                Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offersRoot.join(Offer_.siteSurveyReport, JoinType.LEFT);
                Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant, JoinType.LEFT);
                
                Predicate restriction = cb.equal(plantRoot.get(Plant_.customerSupplier), customerSupplier);
                
                if (name != null && !name.isEmpty()) {
                    conditions.add(
                            cb.and(
                                    restriction,
                                    cb.or(
                                            cb.like(cb.lower(systemRoot.get(System_.description)), "%" + name.toLowerCase() + "%"),
                                            cb.like(cb.lower(plantRoot.get(Plant_.address)), "%" + name.toLowerCase() + "%"),
                                            cb.like(cb.lower(plantRoot.get(Plant_.name)), "%" + name.toLowerCase() + "%")
                                    )
                            ));
                }
                else
                    conditions.add(restriction);
            }
        }
        
        if (customerSupplier.getIsSupplier()) {
            Root<SupplierPlantLocation> supplierPlantLocationRoot = cb.treat(root, SupplierPlantLocation.class);
            if (supplierPlantLocationRoot != null) {
                Join<SupplierPlantLocation, Plant> supplierPlantRoot = supplierPlantLocationRoot.join(SupplierPlantLocation_.plant, JoinType.LEFT);
                
                Predicate restriction = cb.equal(supplierPlantRoot.get(Plant_.customerSupplier), customerSupplier);
                
                if (name != null && !name.isEmpty()) {
                    conditions.add(
                            cb.and(
                                    restriction,
                                    cb.or(
                                            cb.like(cb.lower(supplierPlantRoot.get(Plant_.address)), "%" + name.toLowerCase() + "%"),
                                            cb.like(cb.lower(supplierPlantRoot.get(Plant_.name)), "%" + name.toLowerCase() + "%")
                                    )
                            ));
                }
                else
                    conditions.add(restriction);
            }
        }

        if (!conditions.isEmpty())
            query.where(cb.or(conditions.toArray(new Predicate[conditions.size()])));
        
        query.orderBy(cb.asc(root.get(Location_.type)));

        return em.createQuery(select).getResultList();
    }
}
