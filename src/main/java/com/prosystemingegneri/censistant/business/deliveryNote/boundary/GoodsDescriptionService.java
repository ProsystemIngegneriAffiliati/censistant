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
package com.prosystemingegneri.censistant.business.deliveryNote.boundary;

import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription_;
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
public class GoodsDescriptionService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public GoodsDescription saveGoodsDescription(GoodsDescription goodsDescription) {        
        if (goodsDescription.getId() == null)
            em.persist(goodsDescription);
        else
            return em.merge(goodsDescription);
        
        return goodsDescription;
    }
    
    public GoodsDescription readGoodsDescription(Long id) {
        return em.find(GoodsDescription.class, id);
    }
    
    public void deleteGoodsDescription(Long id) {
        em.remove(readGoodsDescription(id));
    }

    public List<GoodsDescription> listGoodsDescriptions(int first, int pageSize, String sortField, Boolean isAscending, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GoodsDescription> query = cb.createQuery(GoodsDescription.class);
        Root<GoodsDescription> root = query.from(GoodsDescription.class);
        CriteriaQuery<GoodsDescription> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(GoodsDescription_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(GoodsDescription_.name);
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
        
        TypedQuery<GoodsDescription> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getGoodsDescriptionsCount(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<GoodsDescription> root = query.from(GoodsDescription.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<GoodsDescription> root, String name) {
        List<Predicate> conditions = new ArrayList<>();

        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(GoodsDescription_.name)), "%" + name.toLowerCase() + "%"));
        
        return conditions;
    }
}
