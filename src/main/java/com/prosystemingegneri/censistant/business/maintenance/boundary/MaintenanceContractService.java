/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem_;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract_;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class MaintenanceContractService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    private MaintenancePlanService maintenancePlanService;
    @Inject
    private MaintenanceTaskService maintenanceTaskService;
    @Inject
    private WorkerService workerService;
    
    public MaintenanceContract create() {
        return new MaintenanceContract();
    }
    
    public MaintenanceContract save(MaintenanceContract maintenanceContract) {
        if (maintenanceContract.getId() == null)
            em.persist(maintenanceContract);
        else
            maintenanceContract = em.merge(maintenanceContract);
        
        createMaintenanceTasks(maintenanceContract);

        return maintenanceContract;
    }
    
    public MaintenanceContract find(Long id) {
        return em.find(MaintenanceContract.class, id);
    }
    
    public void delete(Long id) {
        em.remove(find(id));
    }

    public List<MaintenanceContract> list(int first, int pageSize, String sortField, Boolean isAscending, String customerName, Boolean isExpired) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenanceContract> query = cb.createQuery(MaintenanceContract.class);
        Root<MaintenanceContract> root = query.from(MaintenanceContract.class);
        CriteriaQuery<MaintenanceContract> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, customerName, isExpired);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(MaintenanceContract_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "creation":
                    path = root.get(MaintenanceContract_.creation);
                    break;
                case "expiry":
                    path = root.get(MaintenanceContract_.creation);
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
        
        TypedQuery<MaintenanceContract> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getCount(String customerName, Boolean isExpired) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MaintenanceContract> root = query.from(MaintenanceContract.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, customerName, isExpired);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<MaintenanceContract> root, String customerName, Boolean isExpired) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Customer name
        if (customerName != null && !customerName.isEmpty())
            conditions.add(cb.like(cb.lower(root
                    .join(MaintenanceContract_.contractedSystems)
                    .join(ContractedSystem_.system)
                    .join(System_.offers)
                    .join(Offer_.siteSurveyReport)
                    .join(SiteSurveyReport_.plant)
                    .join(Plant_.customerSupplier)
                    .get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"));
        
        //Is expired
        if (isExpired != null) {
            Calendar createdOn = new GregorianCalendar();
            createdOn.add(Calendar.MONTH, MaintenanceContract.DURATION_MONTHS * -1);
            if (isExpired)
                conditions.add(cb.lessThan(root.<Date>get(MaintenanceContract_.creation), createdOn.getTime()));
            else
                conditions.add(cb.greaterThanOrEqualTo(root.<Date>get(MaintenanceContract_.creation), createdOn.getTime()));
        }
        
        return conditions;
    }
    
    
    public List<System> avaibleSystems(@NotNull MaintenanceContract contract, @NotNull CustomerSupplier customer) {
        List<Predicate> conditions = new ArrayList<>();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<System> query = cb.createQuery(System.class);
        Root<System> root = query.from(System.class);
        
        ListJoin<System, Offer> offersRoot = root.join(System_.offers);
        conditions.add(cb.isNotNull(offersRoot.get(Offer_.jobOrder)));
        conditions.add(cb.equal(
                offersRoot
                .join(Offer_.siteSurveyReport)
                .join(SiteSurveyReport_.plant)
                .join(Plant_.customerSupplier), customer));
        if (!contract.getContractedSystems().isEmpty()) {
            List<System> systemsUnderContract = new ArrayList<>();
            for (ContractedSystem contractedSystem : contract.getContractedSystems())
                systemsUnderContract.add(contractedSystem.getSystem());
            conditions.add(cb.not(root.in(systemsUnderContract)));
        }
        
        query.where(conditions.toArray(new Predicate[conditions.size()]));
        query.select(root);
        
        return em.createQuery(query).getResultList();
    }
    
    private void createMaintenanceTasks(@NotNull MaintenancePlan maintenancePlan, @NotNull int numberOfMaintenanceTasksToBeCreated) {
        if (maintenancePlan.getMaintenanceType() == MaintenanceType.PREVENTIVE_MAINTENANCE) {
            int spanInMonths = MaintenanceContract.DURATION_MONTHS / maintenancePlan.getMaintenanceTasksNumber();
            GregorianCalendar expiry = new GregorianCalendar();
            expiry.setTime(maintenancePlan.getContractedSystem().getMaintenanceContract().getCreation());
            expiry.add(Calendar.MONTH, MaintenanceContract.DURATION_MONTHS);
            GregorianCalendar tempExpiry = new GregorianCalendar();
            for (int i = 0; i < numberOfMaintenanceTasksToBeCreated; i++) {
                MaintenanceTask maintenanceTask = maintenanceTaskService.create(maintenancePlan);
                maintenanceTask.setDescription("Manutenzione programmata");
                maintenanceTask.setInChargeWorker(workerService.getDefaultWorker());
                tempExpiry.setTime(expiry.getTime());
                tempExpiry.add(Calendar.MONTH, -1 * spanInMonths * (i + 1));
                maintenanceTask.setExpiry(tempExpiry.getTime());
                
                maintenanceTaskService.saveMaintenanceTask(maintenanceTask);
            }
        }
    }
    
    public void createMaintenanceTasks(@NotNull MaintenanceContract maintenanceContract) {
        for (ContractedSystem contractedSystem : maintenanceContract.getContractedSystems())
            for (MaintenancePlan maintenancePlan : contractedSystem.getMaintenancePlans())
                createMaintenanceTasks(maintenancePlan, maintenancePlan.getMaintenanceTasksNumber() - maintenancePlanService.getMaintenanceTasks(maintenancePlan).size());
    }
}
