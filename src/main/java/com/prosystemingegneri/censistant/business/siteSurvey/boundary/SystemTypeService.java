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
package com.prosystemingegneri.censistant.business.siteSurvey.boundary;

import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
public class SystemTypeService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public SystemType saveSystemType(SystemType systemType) {
        if (systemType.getId() == null)
            em.persist(systemType);
        else
            return em.merge(systemType);
        
        return null;
    }
    
    public SystemType readSystemType(Long id) {
        return em.find(SystemType.class, id);
    }
    
    public void deleteSystemType(Long id) {
        em.remove(readSystemType(id));
    }
    
    public List<SystemType> listSystemTypes(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SystemType> query = cb.createQuery(SystemType.class);
        Root<SystemType> root = query.from(SystemType.class);
        CriteriaQuery<SystemType> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(SystemType_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        query.orderBy(cb.asc(root.get(SystemType_.name)));

        return em.createQuery(select).getResultList();
    }
    
    public SystemType findSystemType(String name, boolean isCaseSensitive) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SystemType> query = cb.createQuery(SystemType.class);
        Root<SystemType> root = query.from(SystemType.class);
        CriteriaQuery<SystemType> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty()) {
            if (isCaseSensitive)
                conditions.add(cb.like(root.get(SystemType_.name), name));
            else
                conditions.add(cb.like(cb.lower(root.get(SystemType_.name)), name.toLowerCase()));
        }
        
        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        try {
            return em.createQuery(select).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException ex) {
            return em.createQuery(select).getResultList().get(0);
        }
    }
}
