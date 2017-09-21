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
package com.prosystemingegneri.censistant.business.production.boundary;

import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure_;
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
public class UnitMeasureService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public UnitMeasure saveUnitMeasure(UnitMeasure unitMeasure) {
        if (unitMeasure.getId() == null)
            em.persist(unitMeasure);
        else
            return em.merge(unitMeasure);
        
        return null;
    }
    
    public UnitMeasure readUnitMeasure(Long id) {
        return em.find(UnitMeasure.class, id);
    }
    
    public void deleteUnitMeasure(Long id) {
        em.remove(readUnitMeasure(id));
    }
    
    public List<UnitMeasure> listUnitMeasures(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitMeasure> query = cb.createQuery(UnitMeasure.class);
        Root<UnitMeasure> root = query.from(UnitMeasure.class);
        CriteriaQuery<UnitMeasure> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(UnitMeasure_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        query.orderBy(cb.asc(root.get(UnitMeasure_.name)));

        return em.createQuery(select).getResultList();
    }
}
