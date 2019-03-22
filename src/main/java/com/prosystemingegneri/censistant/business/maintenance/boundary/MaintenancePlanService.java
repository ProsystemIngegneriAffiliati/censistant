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
package com.prosystemingegneri.censistant.business.maintenance.boundary;

import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask_;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class MaintenancePlanService implements Serializable {
    @PersistenceContext
    EntityManager em;
    
    public List<MaintenanceTask> getMaintenanceTasks(@NotNull MaintenancePlan maintenancePlan) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenanceTask> query = cb.createQuery(MaintenanceTask.class);
        Root<MaintenanceTask> root = query.from(MaintenanceTask.class);
        CriteriaQuery<MaintenanceTask> select = query.select(root).distinct(true);
        query.where(cb.equal(root.get(MaintenanceTask_.maintenancePlan), maintenancePlan));

        return em.createQuery(query).getResultList();
    }
}
