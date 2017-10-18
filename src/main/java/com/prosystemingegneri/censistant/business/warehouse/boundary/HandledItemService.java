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

import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import java.io.Serializable;
import javax.ejb.Stateless;
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
