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
package com.prosystemingegneri.censistant.business.user.boundary;

import com.prosystemingegneri.censistant.business.user.entity.GroupApp;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class UserService {
    @PersistenceContext
    EntityManager em;
    
    public UserApp saveUserApp(UserApp userApp) {
        return em.merge(userApp);
    }
    
    public void createUserApp(UserApp userApp) {
        em.persist(userApp);
    }
    
    public UserApp readUserApp(String userName) {
        return em.find(UserApp.class, userName);
    }
    
    public GroupApp readGroupApp(String groupName) {
        return em.find(GroupApp.class, groupName);
    }
    
    @RolesAllowed("admin")
    public void deleteUserApp(UserApp userApp) {
        em.remove(em.merge(userApp));
    }

    @RolesAllowed("admin")
    public List<UserApp> listUserApps() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserApp> query = cb.createQuery(UserApp.class);
        Root<UserApp> root = query.from(UserApp.class);
        query.select(root);
        return em.createQuery(query).getResultList();
    }
    
    /**
     * Return the groups list avaible for user
     * @param user
     * @return
     */
    public List<GroupApp> avaibleGroups(UserApp user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GroupApp> query = cb.createQuery(GroupApp.class);
        Root<GroupApp> root = query.from(GroupApp.class);
        if (!user.getGroups().isEmpty())
            query.where(cb.not(root.in(user.getGroups())));
        query.select(root);
        
        return em.createQuery(query).getResultList();
    }
}