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

import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker_;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import com.prosystemingegneri.censistant.business.user.entity.UserApp_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
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
public class WorkerService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Resource
    SessionContext ctx;
    
    public Worker saveWorker(Worker worker) {
        if (worker.getId() == null)
            em.persist(worker);
        else
            return em.merge(worker);
        
        return null;
    }
    
    public Worker readWorker(Long id) {
        return em.find(Worker.class, id);
    }
    
    public void deleteWorker(Long id) {
        em.remove(readWorker(id));
    }
    
    public List<Worker> listWorkers(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Worker> query = cb.createQuery(Worker.class);
        Root<Worker> root = query.from(Worker.class);
        CriteriaQuery<Worker> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(Worker_.name)), "%" + String.valueOf(name).toLowerCase() + "%"));
        
        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        query.orderBy(cb.asc(root.get(Worker_.name)));

        return em.createQuery(select).getResultList();
    }
    
    public Optional<Worker> findByUserapp(String userAppUsername, UserApp userApp) {
        if ((userAppUsername != null && !userAppUsername.isEmpty()) || userApp != null) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Worker> query = cb.createQuery(Worker.class);
            Root<Worker> root = query.from(Worker.class);

            List<Predicate> conditions = new ArrayList<>();
            
            if (userAppUsername != null && !userAppUsername.isEmpty())
                conditions.add(cb.like(root.join(Worker_.userApp).get(UserApp_.userName), userAppUsername));
            
            if (userApp != null)
                conditions.add(cb.equal(root.get(Worker_.userApp), userApp));

            if (!conditions.isEmpty())
                query.where(conditions.toArray(new Predicate[conditions.size()]));

            Worker result;
            try {
                result = em.createQuery(query).getSingleResult();
                if (result == null)
                    return Optional.empty();
            } catch (NoResultException e) {
                return Optional.empty();
            }

            return Optional.of(result);
        }
        
        return Optional.empty();
    }
    
    public Optional<Worker> getLoggedWorker() {
        if (ctx.getCallerPrincipal() != null)
            return findByUserapp(ctx.getCallerPrincipal().getName(), null);
        else
            return Optional.empty();
    }
    
    public Worker findWorker(String name, boolean isCaseSensitive, UserApp user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Worker> query = cb.createQuery(Worker.class);
        Root<Worker> root = query.from(Worker.class);
        CriteriaQuery<Worker> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = new ArrayList<>();
        
        //name
        if (name != null && !name.isEmpty()) {
            if (isCaseSensitive)
                conditions.add(cb.like(root.get(Worker_.name), name));
            else
                conditions.add(cb.like(cb.lower(root.get(Worker_.name)), name.toLowerCase()));
        }
        
        //user
        if(user != null)
            conditions.add(cb.equal(root.get(Worker_.userApp), user));
        
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
    
    public Worker getDefaultWorker() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Worker> query = cb.createQuery(Worker.class);
        Root<Worker> root = query.from(Worker.class);
        CriteriaQuery<Worker> select = query.select(root).distinct(true);
        
        query.where(cb.like(cb.lower(root.get(Worker_.initials)), "fg"));
        
        try {
            return em.createQuery(select).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException ex) {
            return em.createQuery(select).getResultList().get(0);
        }
    }
}
