/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.sales.boundary;

import com.prosystemingegneri.censistant.business.sales.entity.WorkingOperation;
import com.prosystemingegneri.censistant.business.sales.entity.WorkingOperation_;
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
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class WorkingOperationService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public WorkingOperation create() {
        return new WorkingOperation();
    }
    
    public WorkingOperation save(WorkingOperation workingOperation) {        
        if (workingOperation.getId() == null)
            em.persist(workingOperation);
        else
            return em.merge(workingOperation);
        
        return null;
    }
    
    public WorkingOperation find(Long id) {
        return em.find(WorkingOperation.class, id);
    }
    
    public void delete(Long id) {
        em.remove(find(id));
    }

    public List<WorkingOperation> list(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkingOperation> query = cb.createQuery(WorkingOperation.class);
        Root<WorkingOperation> root = query.from(WorkingOperation.class);
        CriteriaQuery<WorkingOperation> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(WorkingOperation_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        query.orderBy(cb.asc(root.get("id")));

        return em.createQuery(select).getResultList();
    }
}
