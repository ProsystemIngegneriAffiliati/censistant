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

import com.prosystemingegneri.censistant.business.production.boundary.DeviceService;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
    
    public List<HandledItem> listHandledItems(int first, int pageSize, String sortField, Boolean isAscending, BoxedItem boxedItem) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HandledItem> query = cb.createQuery(HandledItem.class);
        Root<HandledItem> root = query.from(HandledItem.class);
        CriteriaQuery<HandledItem> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, boxedItem);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(HandledItem_.handlingTimestamp));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
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
        }

        if (boxedItem != null)   //we don't want all the handled items!
            return typedQuery.getResultList();
        else
            return null;
    }
    
    public Long getLocationsCount(BoxedItem boxedItem) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<HandledItem> root = query.from(HandledItem.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, boxedItem);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        if (boxedItem != null)   //we don't want all the handled items!
            return em.createQuery(select).getSingleResult();
        else
            return Long.valueOf(0);
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<HandledItem> root, BoxedItem boxedItem) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Boxed item
        if (boxedItem != null)
            conditions.add(cb.equal(root.get(HandledItem_.boxedItem), boxedItem));
        
        return conditions;
    }
}
