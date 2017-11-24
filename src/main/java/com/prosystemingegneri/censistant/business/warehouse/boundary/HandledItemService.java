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

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
                        newHandledItem.setPurchaseOrderRow(stock.getPurchaseOrderRow());
                        newHandledItem.setToLocation(locationArrival);
                        newHandledItem.setWorker(worker);

                        if (stock.getUnitMeasure().equals(stock.getNakedUnitMeasure()))
                            newHandledItem.setQuantity(stock.getQuantity());
                        else
                            newHandledItem.setQuantity(stock.getQuantity() * stock.getPurchaseOrderRow().getBoxedItem().getBox().getQuantity());

                        em.persist(newHandledItem);

                        result.add(newHandledItem);
                    }
                }
            }
            else
                throw new NoSuchFieldException("User is not a valid worker. Associate the user to a worker and retry.");
        }
        
        return result;
    }
    
    public HandledItem saveHandledItem(HandledItem handledItem) {        
        if (handledItem.getId() == null)
            em.persist(handledItem);
        else
            return em.merge(handledItem);
        
        return null;
    }
    
    public HandledItem readHandledItem(Long id) {
        return em.find(HandledItem.class, id);
    }
    
    public void deleteHandledItem(Long id) {
        em.remove(readHandledItem(id));
    }
}
