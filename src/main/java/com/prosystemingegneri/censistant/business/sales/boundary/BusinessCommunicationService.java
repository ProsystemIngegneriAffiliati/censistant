/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.sales.entity.BusinessCommunication;
import com.prosystemingegneri.censistant.business.sales.entity.BusinessCommunication_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
public class BusinessCommunicationService implements Serializable {
    @PersistenceContext
    EntityManager em;
    
    public Date getLastEmailSent(CustomerSupplier customer) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<BusinessCommunication> root = query.from(BusinessCommunication.class);
        query.select(cb.greatest(root.get(BusinessCommunication_.emailSent)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        if (customer != null)
            conditions.add(cb.equal(root.get(BusinessCommunication_.customer), customer));

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Date result;
        try {
            result = em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }
        
	return result;
    }
}
