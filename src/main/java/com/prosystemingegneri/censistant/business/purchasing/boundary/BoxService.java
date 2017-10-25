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
package com.prosystemingegneri.censistant.business.purchasing.boundary;

import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure_;
import com.prosystemingegneri.censistant.business.purchasing.entity.Box;
import com.prosystemingegneri.censistant.business.purchasing.entity.Box_;
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class BoxService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public Box saveBox(Box box) {        
        if (box.getId() == null)
            em.persist(box);
        else
            return em.merge(box);
        
        return null;
    }
    
    public Box readBox(Long id) {
        return em.find(Box.class, id);
    }
    
    public void deleteBox(Long id) {
        em.remove(readBox(id));
    }

    public List<Box> listBoxes(int first, int pageSize, String sortField, Boolean isAscending, String unitMeasureName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Box> query = cb.createQuery(Box.class);
        Root<Box> root = query.from(Box.class);
        CriteriaQuery<Box> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, unitMeasureName);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(Box_.unitMeasure).get(UnitMeasure_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "unitMeasure":
                    path = root.get(Box_.unitMeasure).get(UnitMeasure_.name);
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
        
        TypedQuery<Box> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getBoxesCount(String unitMeasureName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Box> root = query.from(Box.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, unitMeasureName);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<Box> root, String unitMeasureName) {
        List<Predicate> conditions = new ArrayList<>();

        //name of unit of measure
        if (unitMeasureName != null && !unitMeasureName.isEmpty()) {
            Join<Box, UnitMeasure> unitMeasureRoot = root.join(Box_.unitMeasure);
            conditions.add(cb.like(cb.lower(unitMeasureRoot.get(UnitMeasure_.name)), "%" + unitMeasureName.toLowerCase() + "%"));
        }
        
        return conditions;
    }
}
