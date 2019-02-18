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
package com.prosystemingegneri.censistant.business.production.boundary;

import com.prosystemingegneri.censistant.business.production.entity.DeviceProgrammingType;
import com.prosystemingegneri.censistant.business.production.entity.DeviceProgrammingType_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class DeviceProgrammingTypeService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public DeviceProgrammingType saveDeviceProgrammingType(DeviceProgrammingType deviceProgrammingType) {        
        if (deviceProgrammingType.getId() == null)
            em.persist(deviceProgrammingType);
        else
            return em.merge(deviceProgrammingType);
        
        return null;
    }
    
    public DeviceProgrammingType readDeviceProgrammingType(Long id) {
        return em.find(DeviceProgrammingType.class, id);
    }
    
    public void deleteDeviceProgrammingType(Long id) {
        em.remove(readDeviceProgrammingType(id));
    }

    public List<DeviceProgrammingType> listDeviceProgrammingTypes(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeviceProgrammingType> query = cb.createQuery(DeviceProgrammingType.class);
        Root<DeviceProgrammingType> root = query.from(DeviceProgrammingType.class);
        CriteriaQuery<DeviceProgrammingType> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(DeviceProgrammingType_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        query.orderBy(cb.asc(root.get(DeviceProgrammingType_.name)));

        return em.createQuery(select).getResultList();
    }
}
