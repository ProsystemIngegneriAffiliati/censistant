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

import com.prosystemingegneri.censistant.business.production.entity.Item;
import com.prosystemingegneri.censistant.business.production.entity.Item_;
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
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
public class ItemService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public Item saveItem(Item item) {        
        if (item.getId() == null)
            em.persist(item);
        else
            return em.merge(item);
        
        return null;
    }
    
    public Item readItem(Long id) {
        return em.find(Item.class, id);
    }
    
    public void deleteItem(Long id) {
        em.remove(readItem(id));
    }

    public List<Item> listItems(int first, int pageSize, String sortField, Boolean isAscending, String description) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> root = query.from(Item.class);
        CriteriaQuery<Item> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();

        //description
        if (description != null && !description.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(Item_.description)), "%" + description.toLowerCase() + "%"));

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(Item_.description));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "description":
                    path = root.get(Item_.description);
                    break;
                case "unitMeasure":
                    path = root.get(Item_.unitMeasure).get(UnitMeasure_.name);
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
        
        TypedQuery<Item> typedQuery = em.createQuery(select);
        typedQuery.setMaxResults(pageSize);
        typedQuery.setFirstResult(first);

        return typedQuery.getResultList();
    }
    
    public Long getItemsCount(String description) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Item> root = query.from(Item.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = new ArrayList<>();

        //description
        if (description != null && !description.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(Item_.description)), "%" + description.toLowerCase() + "%"));

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
}
