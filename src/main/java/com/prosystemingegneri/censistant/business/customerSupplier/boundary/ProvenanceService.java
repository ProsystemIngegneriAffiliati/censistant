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
package com.prosystemingegneri.censistant.business.customerSupplier.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Provenance;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Provenance_;
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
public class ProvenanceService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public Provenance saveProvenance(Provenance provenance) {
        if (provenance.getId() == null)
            em.persist(provenance);
        else
            return em.merge(provenance);
        
        return null;
    }
    
    public Provenance readProvenance(Long id) {
        return em.find(Provenance.class, id);
    }
    
    public void deleteProvenance(Long id) {
        em.remove(readProvenance(id));
    }
    
    public List<Provenance> listProvenances(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Provenance> query = cb.createQuery(Provenance.class);
        Root<Provenance> root = query.from(Provenance.class);
        CriteriaQuery<Provenance> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(Provenance_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        query.orderBy(cb.asc(root.get(Provenance_.name)));

        return em.createQuery(select).getResultList();
    }
    
    public Provenance findProvenance(String name, boolean isCaseSensitive) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Provenance> query = cb.createQuery(Provenance.class);
        Root<Provenance> root = query.from(Provenance.class);
        CriteriaQuery<Provenance> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            if (isCaseSensitive)
                conditions.add(cb.equal(root.get(Provenance_.name), name));
            else
                conditions.add(cb.equal(cb.lower(root.get(Provenance_.name)), name.toLowerCase()));
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