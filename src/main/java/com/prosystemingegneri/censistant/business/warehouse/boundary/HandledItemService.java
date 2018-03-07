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
package com.prosystemingegneri.censistant.business.warehouse.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.production.boundary.DeviceService;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.production.entity.Item;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem_;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem_;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker_;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location_;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierLocation;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierLocation_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
public class HandledItemService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    WorkerService workerService;
    @Inject
    DeviceService deviceService;
    @Inject
    SystemService systemService;
    
    public List<HandledItem> createNewHandledItems(List<Stock> stockList, Location locationArrival, UserApp loggedUser) throws NoSuchFieldException {
        List<HandledItem> result = new ArrayList<>();
        if (stockList != null && !stockList.isEmpty() && locationArrival != null) {
            Worker worker = workerService.findWorker(null, false, loggedUser);
            if (worker != null) {
                for (Stock stock : stockList) {
                    if (!stock.getLocation().equals(locationArrival)) {
                        HandledItem newHandledItem = new HandledItem();

                        newHandledItem.setFromLocation(stock.getLocation());
                        newHandledItem.setHandlingTimestamp(new Date());
                        newHandledItem.setBoxedItem(stock.getBoxedItem());
                        newHandledItem.setToLocation(locationArrival);
                        newHandledItem.setWorker(worker);
                        
                        Integer quantityMoved;
                        if (stock.getUnitMeasure().equals(stock.getNakedUnitMeasure()))
                            quantityMoved = stock.getQuantity();
                        else
                            quantityMoved = stock.getQuantity() * stock.getBoxedItem().getBox().getQuantity();
                        
                        newHandledItem.setQuantity(quantityMoved);

                        em.persist(newHandledItem);

                        result.add(newHandledItem);
                        
                        if (locationArrival instanceof System) {
                            System system = (System) locationArrival;
                            if (deviceService.getDevicesCount(system, stock.getBoxedItem().getItem().getItem()) <= 0) {
                                Device device = new Device();
                                device.setItem(stock.getBoxedItem().getItem().getItem());
                                device.setQuantity(quantityMoved);
                                system.addDevice(device);
                                
                                systemService.saveSystem(system);
                            }
                        }
                    }
                }
            }
            else
                throw new NoSuchFieldException("User is not a valid worker. Associate the user to a worker and retry.");
        }
        
        return result;
    }
    
    public HandledItem updateHandledItem(HandledItem handledItem) {        
        if (handledItem.getId() != null)
            return em.merge(handledItem);
        
        return null;
    }
    
    public HandledItem readHandledItem(Long id) {
        return em.find(HandledItem.class, id);
    }
    
    public void deleteHandledItem(Long id) {
        em.remove(readHandledItem(id));
    }
    
    public List<HandledItem> listHandledItems(int first, int pageSize, String sortField, Boolean isAscending, String workerName, String supplierItemCode, String fromLocationName, String toLocationName, BoxedItem boxedItem, Location fromOrToLocation, Item item) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HandledItem> query = cb.createQuery(HandledItem.class);
        Root<HandledItem> root = query.from(HandledItem.class);
        CriteriaQuery<HandledItem> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, workerName, supplierItemCode, fromLocationName, toLocationName, boxedItem, fromOrToLocation, item);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(HandledItem_.handlingTimestamp));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path = null;
            Path<Warehouse> warehousePath;
            Path<SupplierLocation> supplierLocationPath;
            Path<System> systemPath;
            switch (sortField) {
                case "handlingTimestamp":
                    path = root.get(HandledItem_.handlingTimestamp);
                    break;
                case "workerName":
                    path = root.get(HandledItem_.worker).get(Worker_.name);
                    break;
                case "supplierItemCode":
                    path = root.get(HandledItem_.boxedItem).get(BoxedItem_.item).get(SupplierItem_.code);
                    break;
                case "fromLocationName":
                    warehousePath = cb.treat(root.get(HandledItem_.fromLocation), Warehouse.class);
                    supplierLocationPath = cb.treat(root.get(HandledItem_.fromLocation), SupplierLocation.class);
                    systemPath = cb.treat(root.get(HandledItem_.fromLocation), System.class);
                    
                    if (warehousePath != null)
                        path = warehousePath.get(Warehouse_.name);
                    else if (supplierLocationPath != null)
                        path = supplierLocationPath.get(SupplierLocation_.supplier).get(CustomerSupplier_.name);
                    else if (systemPath != null)
                        path = systemPath.get(System_.name);
                    break;
                case "toLocationName":
                    warehousePath = cb.treat(root.get(HandledItem_.toLocation), Warehouse.class);
                    supplierLocationPath = cb.treat(root.get(HandledItem_.toLocation), SupplierLocation.class);
                    systemPath = cb.treat(root.get(HandledItem_.toLocation), System.class);
                    
                    if (warehousePath != null)
                        path = warehousePath.get(Warehouse_.name);
                    else if (supplierLocationPath != null)
                        path = supplierLocationPath.get(SupplierLocation_.supplier).get(CustomerSupplier_.name);
                    else if (systemPath != null)
                        path = systemPath.get(System_.name);
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
        
        TypedQuery<HandledItem> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
            return typedQuery.getResultList();
        }
        else {
            if (boxedItem != null || fromOrToLocation != null || item != null)   //we don't want all the handled items!
                return typedQuery.getResultList();
            else
                return null;
        }
    }
    
    public Long getHandledItemsCount(String workerName, String supplierItemCode, String fromLocationName, String toLocationName, BoxedItem boxedItem, Location fromOrToLocation, Item item) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<HandledItem> root = query.from(HandledItem.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, workerName, supplierItemCode, fromLocationName, toLocationName, boxedItem, fromOrToLocation, item);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        try {
            return em.createQuery(select).getSingleResult();
        } catch (NoResultException e) {
            return new Long(0);
        }
        catch (NonUniqueResultException e) {
            return em.createQuery(select).getResultList().get(0);
        }
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<HandledItem> root, String workerName, String supplierItemCode, String fromLocationName, String toLocationName, BoxedItem boxedItem, Location fromOrToLocation, Item item) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Worker's name
        if (workerName != null && !workerName.isEmpty()) {
            Join<HandledItem, Worker> workerRoot = root.join(HandledItem_.worker);
            conditions.add(cb.like(cb.lower(workerRoot.get(Worker_.name)), "%" + workerName.toLowerCase() + "%"));
        }
        
        //Supplier's item code
        if (supplierItemCode != null && !supplierItemCode.isEmpty()) {
            Join<HandledItem, BoxedItem> boxedItemRoot = root.join(HandledItem_.boxedItem);
            Join<BoxedItem, SupplierItem> supplierItemRoot = boxedItemRoot.join(BoxedItem_.item);
            conditions.add(cb.like(cb.lower(supplierItemRoot.get(SupplierItem_.code)), "%" + supplierItemCode.toLowerCase() + "%"));
        }
        
        //From location's name
        if (fromLocationName != null && !fromLocationName.isEmpty()) {
            Join<HandledItem, Location> fromLocationRoot = root.join(HandledItem_.fromLocation);
            
            Join<HandledItem, Warehouse> warehouseRoot = cb.treat(fromLocationRoot, Warehouse.class);
            Join<HandledItem, SupplierLocation> supplierLocationRoot = cb.treat(fromLocationRoot, SupplierLocation.class);
            Join<HandledItem, System> systemRoot = cb.treat(fromLocationRoot, System.class);
            
            Join<System, JobOrder> jobOrdersRoot = systemRoot.join(System_.jobOrders, JoinType.LEFT);
            Join<JobOrder, Offer> offerRoot = jobOrdersRoot.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier);
            Join<SupplierLocation, CustomerSupplier> supplierRoot = supplierLocationRoot.join(SupplierLocation_.supplier, JoinType.LEFT);
            
            conditions.add(cb.or(
                    cb.like(cb.lower(warehouseRoot.get(Warehouse_.name)), "%" + fromLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + fromLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(systemRoot.get(System_.description)), "%" + fromLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + fromLocationName.toLowerCase() + "%")));
        }
        
        //To location's name
        if (toLocationName != null && !toLocationName.isEmpty()) {
            Join<HandledItem, Location> toLocationRoot = root.join(HandledItem_.toLocation);
            
            Join<HandledItem, Warehouse> warehouseRoot = cb.treat(toLocationRoot, Warehouse.class);
            Join<HandledItem, SupplierLocation> supplierLocationRoot = cb.treat(toLocationRoot, SupplierLocation.class);
            Join<HandledItem, System> systemRoot = cb.treat(toLocationRoot, System.class);
            
            Join<System, JobOrder> jobOrdersRoot = systemRoot.join(System_.jobOrders, JoinType.LEFT);
            Join<JobOrder, Offer> offerRoot = jobOrdersRoot.join(JobOrder_.offer);
            Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offerRoot.join(Offer_.siteSurveyReport);
            Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
            Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier);
            Join<SupplierLocation, CustomerSupplier> supplierRoot = supplierLocationRoot.join(SupplierLocation_.supplier, JoinType.LEFT);
            
            conditions.add(cb.or(
                    cb.like(cb.lower(warehouseRoot.get(Warehouse_.name)), "%" + toLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + toLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(systemRoot.get(System_.description)), "%" + toLocationName.toLowerCase() + "%"),
                    cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + toLocationName.toLowerCase() + "%")));
        }
        
        //Boxed item
        if (boxedItem != null)
            conditions.add(cb.equal(root.get(HandledItem_.boxedItem), boxedItem));
        
        //From location or to location
        if (fromOrToLocation != null)
            conditions.add(cb.or(
                    cb.equal(root.get(HandledItem_.fromLocation), fromOrToLocation),
                    cb.equal(root.get(HandledItem_.toLocation), fromOrToLocation)));
        
        //Item
        if (item != null) {
            Join<HandledItem, BoxedItem> boxedItemRoot = root.join(HandledItem_.boxedItem);
            Join<BoxedItem, SupplierItem> supplierItemRoot = boxedItemRoot.join(BoxedItem_.item);
            conditions.add(cb.equal(supplierItemRoot.get(SupplierItem_.item), item));
        }
        
        return conditions;
    }
}
