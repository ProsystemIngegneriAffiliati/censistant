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
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.maintenance.control.Inspection;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.maintenance.control.SuitableForOperation;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem_;
import com.prosystemingegneri.censistant.business.maintenance.entity.InspectionDone;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePayment;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan_;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTaskDto;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask_;
import com.prosystemingegneri.censistant.business.maintenance.entity.TaskPrice;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.boundary.OfferService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.sales.entity.PlaceType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
public class MaintenanceTaskService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    private OfferService offerService;
    @Inject
    private JobOrderService jobOrderService;
    
    public MaintenanceTask create() {
        MaintenanceTask maintenanceTask = new MaintenanceTask();
        maintenanceTask.addTaskPrice(new TaskPrice());
        
        return maintenanceTask;
    }
    
    public MaintenanceTask create(MaintenancePlan maintenancePlan) {
        MaintenanceTask maintenanceTask = new MaintenanceTask(maintenancePlan);
        maintenanceTask.addTaskPrice(new TaskPrice());
        for (Inspection inspection : Inspection.values())
            maintenanceTask.addInspectionDone(new InspectionDone(inspection));
        
        return maintenanceTask;
    }
    
    public MaintenanceTask create(System system) {
        MaintenanceTask maintenanceTask = new MaintenanceTask(system);
        maintenanceTask.addTaskPrice(new TaskPrice());
        
        return maintenanceTask;
    }
    
    public MaintenanceTask saveMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (maintenanceTask.getExpiry() != null) {
            //since only the month is useful, last day of month is chosen
            Calendar temp = Calendar.getInstance();
            temp.setTime(maintenanceTask.getExpiry());
            temp.add(Calendar.MONTH, 1);
            Calendar temp2 = Calendar.getInstance();
            temp2.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), 1);
            temp2.add(Calendar.DAY_OF_YEAR, -1);
            maintenanceTask.setExpiry(temp2.getTime());
        }
        
        if (maintenanceTask.getId() == null)
            em.persist(maintenanceTask);
        else
            maintenanceTask = em.merge(maintenanceTask);
        
        return maintenanceTask;
    }
    
    public MaintenanceTask readMaintenanceTask(Long id) {
        return em.find(MaintenanceTask.class, id);
    }
    
    public void deleteMaintenanceTask(Long id) {
        em.remove(readMaintenanceTask(id));
    }
    
    public List<MaintenanceTaskDto> listMaintenanceTasks(
            int first, int pageSize,
            String sortField, Boolean isAscending,
            String customerName,
            String systemAddress,
            MaintenanceType maintenanceType,
            Date expiryStart, Date expiryEnd,
            SuitableForOperation suitableForOperation,
            Boolean isClosed) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenanceTaskDto> query = cb.createQuery(MaintenanceTaskDto.class);
        Root<MaintenanceTask> root = query.from(MaintenanceTask.class);
        
        Join<MaintenanceTask, MaintenancePlan> joinMaintenancePlan = root.join(MaintenanceTask_.maintenancePlan, JoinType.LEFT);
        Join<MaintenancePlan, ContractedSystem> joinContractedSystemFromMaintenancePlan = joinMaintenancePlan.join(MaintenancePlan_.contractedSystem, JoinType.LEFT);
        Join<ContractedSystem, System> joinSystemFromMaintenancePlan = joinContractedSystemFromMaintenancePlan.join(ContractedSystem_.system, JoinType.LEFT);
        Join<System, Offer> joinOfferFromMaintenancePlan = joinSystemFromMaintenancePlan.join(System_.offers, JoinType.LEFT);
        Join<Offer, SiteSurveyReport> joinSiteSurveyReportFromMaintenancePlan = joinOfferFromMaintenancePlan.join(Offer_.siteSurveyReport, JoinType.LEFT);
        Join<SiteSurveyReport, Plant> joinPlantFromMaintenancePlan = joinSiteSurveyReportFromMaintenancePlan.join(SiteSurveyReport_.plant, JoinType.LEFT);
        Join<Plant, CustomerSupplier> joinCustomerSupplierFromMaintenancePlan = joinPlantFromMaintenancePlan.join(Plant_.customerSupplier, JoinType.LEFT);
        
        Join<MaintenanceTask, System> joinSystem = root.join(MaintenanceTask_.system, JoinType.LEFT);
        Join<System, Offer> joinOfferFromSystem = joinSystem.join(System_.offers, JoinType.LEFT);
        Join<Offer, SiteSurveyReport> joinSiteSurveyReportSystem = joinOfferFromSystem.join(Offer_.siteSurveyReport, JoinType.LEFT);
        Join<SiteSurveyReport, Plant> joinPlantSystem = joinSiteSurveyReportSystem.join(SiteSurveyReport_.plant, JoinType.LEFT);
        Join<Plant, CustomerSupplier> joinCustomerSupplierFromSystem = joinPlantSystem.join(Plant_.customerSupplier, JoinType.LEFT);
        
        CriteriaQuery<MaintenanceTaskDto> select = query.select(cb.construct(
                MaintenanceTaskDto.class,
                root.get(MaintenanceTask_.id),
                joinCustomerSupplierFromMaintenancePlan.get(CustomerSupplier_.name),
                cb.concat(joinPlantFromMaintenancePlan.get(Plant_.name), cb.concat(cb.literal(" - "), joinPlantFromMaintenancePlan.get(Plant_.address))),
                joinCustomerSupplierFromSystem.get(CustomerSupplier_.name),
                joinPlantSystem.get(Plant_.address),
                joinMaintenancePlan.get(MaintenancePlan_.maintenanceType),
                root.get(MaintenanceTask_.expiry),
                root.get(MaintenanceTask_.suitableForOperation),
                root.get(MaintenanceTask_.closed),
                root.get(MaintenanceTask_.closingNotes)
        )).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, joinMaintenancePlan, joinPlantFromMaintenancePlan, joinCustomerSupplierFromMaintenancePlan, joinPlantSystem, joinCustomerSupplierFromSystem, customerName, systemAddress, maintenanceType, expiryStart, expiryEnd, suitableForOperation, isClosed);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.asc(root.get(MaintenanceTask_.expiry));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "maintenanceType":
                    path = root.get(MaintenanceTask_.maintenancePlan).get(MaintenancePlan_.maintenanceType);
                    break;
                case "expiry":
                    path = root.get(MaintenanceTask_.expiry);
                    break;
                case "closed":
                    path = root.get(MaintenanceTask_.closed);
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
        
        TypedQuery<MaintenanceTaskDto> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getMaintenanceTasksCount(
            String customerName,
            String systemAddress,
            MaintenanceType maintenanceType,
            Date expiryStart, Date expiryEnd,
            SuitableForOperation suitableForOperation,
            Boolean isClosed) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MaintenanceTask> root = query.from(MaintenanceTask.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));
        
        Join<MaintenanceTask, MaintenancePlan> joinMaintenancePlan = root.join(MaintenanceTask_.maintenancePlan, JoinType.LEFT);
        Join<MaintenancePlan, ContractedSystem> joinContractedSystemFromMaintenancePlan = joinMaintenancePlan.join(MaintenancePlan_.contractedSystem, JoinType.LEFT);
        Join<ContractedSystem, System> joinSystemFromMaintenancePlan = joinContractedSystemFromMaintenancePlan.join(ContractedSystem_.system, JoinType.LEFT);
        Join<System, Offer> joinOfferFromMaintenancePlan = joinSystemFromMaintenancePlan.join(System_.offers, JoinType.LEFT);
        Join<Offer, SiteSurveyReport> joinSiteSurveyReportFromMaintenancePlan = joinOfferFromMaintenancePlan.join(Offer_.siteSurveyReport, JoinType.LEFT);
        Join<SiteSurveyReport, Plant> joinPlantFromMaintenancePlan = joinSiteSurveyReportFromMaintenancePlan.join(SiteSurveyReport_.plant, JoinType.LEFT);
        Join<Plant, CustomerSupplier> joinCustomerSupplierFromMaintenancePlan = joinPlantFromMaintenancePlan.join(Plant_.customerSupplier, JoinType.LEFT);
        
        Join<MaintenanceTask, System> joinSystem = root.join(MaintenanceTask_.system, JoinType.LEFT);
        Join<System, Offer> joinOfferFromSystem = joinSystem.join(System_.offers, JoinType.LEFT);
        Join<Offer, SiteSurveyReport> joinSiteSurveyReportSystem = joinOfferFromSystem.join(Offer_.siteSurveyReport, JoinType.LEFT);
        Join<SiteSurveyReport, Plant> joinPlantSystem = joinSiteSurveyReportSystem.join(SiteSurveyReport_.plant, JoinType.LEFT);
        Join<Plant, CustomerSupplier> joinCustomerSupplierFromSystem = joinPlantSystem.join(Plant_.customerSupplier, JoinType.LEFT);

        List<Predicate> conditions = calculateConditions(cb, root, joinMaintenancePlan, joinPlantFromMaintenancePlan, joinCustomerSupplierFromMaintenancePlan, joinPlantSystem, joinCustomerSupplierFromSystem, customerName, systemAddress, maintenanceType, expiryStart, expiryEnd, suitableForOperation, isClosed);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(
            CriteriaBuilder cb, Root<MaintenanceTask> root,
            Join<MaintenanceTask, MaintenancePlan> joinMaintenancePlan,
            Join<SiteSurveyReport, Plant> joinPlantFromMaintenancePlan,
            Join<Plant, CustomerSupplier> joinCustomerSupplierFromMaintenancePlan,
            Join<SiteSurveyReport, Plant> joinPlantSystem,
            Join<Plant, CustomerSupplier> joinCustomerSupplierFromSystem,
            String customerName,
            String systemAddress,
            MaintenanceType maintenanceType,
            Date expiryStart, Date expiryEnd,
            SuitableForOperation suitableForOperation,
            Boolean isClosed) {
        List<Predicate> conditions = new ArrayList<>();
        
        //Customer name
        if (customerName != null && !customerName.isEmpty())
            conditions.add(
                    cb.or(
                            cb.like(cb.lower(joinCustomerSupplierFromMaintenancePlan.get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%"),
                            cb.like(cb.lower(joinCustomerSupplierFromSystem.get(CustomerSupplier_.name)), "%" + customerName.toLowerCase() + "%")
                    ));
        
        //System address
        if (systemAddress != null && !systemAddress.isEmpty())
            conditions.add(
                    cb.or(
                            cb.like(cb.lower(joinPlantFromMaintenancePlan.get(Plant_.address)), "%" + systemAddress.toLowerCase() + "%"),
                            cb.like(cb.lower(joinPlantSystem.get(Plant_.address)), "%" + systemAddress.toLowerCase() + "%")
                    ));
        
        //Maintenance type
        if (maintenanceType != null)
            conditions.add(cb.equal(joinMaintenancePlan.get(MaintenancePlan_.maintenanceType), maintenanceType));
        
        //Expiry date
        if (expiryStart != null &&  expiryEnd != null)
            conditions.add(cb.between(root.<Date>get(MaintenanceTask_.expiry), expiryStart, expiryEnd));
        if (expiryStart == null &&  expiryEnd != null)
            conditions.add(cb.lessThan(root.<Date>get(MaintenanceTask_.expiry), expiryEnd));
        
        //Suitable for operation
        if (suitableForOperation != null)
            conditions.add(cb.equal(root.get(MaintenanceTask_.suitableForOperation), suitableForOperation));
        
        //isClosed
        if (isClosed != null) {
            if (isClosed)
                conditions.add(
                        cb.or(
                                cb.and(
                                        cb.isNotNull(root.get(MaintenanceTask_.closed)),
                                        cb.equal(joinMaintenancePlan.get(MaintenancePlan_.maintenanceType), MaintenanceType.TELECONTROL)
                                ),
                                cb.and(
                                        cb.isNotNull(root.get(MaintenanceTask_.closed)),
                                        cb.notEqual(joinMaintenancePlan.get(MaintenancePlan_.maintenanceType), MaintenanceType.TELECONTROL),
                                        cb.notEqual(root.get(MaintenanceTask_.suitableForOperation), SuitableForOperation.SUSPENDED)
                                )
                        )
                );
            else
                conditions.add(
                        cb.or(
                                cb.isNull(root.get(MaintenanceTask_.closed)),
                                cb.and(
                                        cb.isNotNull(root.get(MaintenanceTask_.closed)),
                                        cb.equal(root.get(MaintenanceTask_.suitableForOperation), SuitableForOperation.SUSPENDED)
                                )
                        )
                );
        }
        
        return conditions;
    }
    
    public List<MaintenancePayment> avaibleMaintenancePayments(MaintenanceTask maintenanceTask) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MaintenancePayment> query = cb.createQuery(MaintenancePayment.class);
        Root<MaintenancePayment> root = query.from(MaintenancePayment.class);
        
        if (!maintenanceTask.getMaintenancePayments().isEmpty())
            query.where(cb.not(root.in(maintenanceTask.getMaintenancePayments())));
        
        query.select(root);
        
        return em.createQuery(query).getResultList();
    }

    public JobOrder createNewJobOrder(@NotNull Plant plant, @NotNull SystemType systemType, @NotNull PlaceType placeType, @NotNull Worker seller) {
        Offer offer = offerService.createNewOffer();
        
        offer.getSiteSurveyReport().getRequest().setCustomer(plant.getCustomerSupplier());
        offer.getSiteSurveyReport().getRequest().setSystemType(systemType);
        offer.getSiteSurveyReport().setPlant(plant);
        offer.getSiteSurveyReport().setSeller(seller);
        
        JobOrder jobOrder = jobOrderService.createNewJobOrder(offer);
        jobOrder.setPlaceType(placeType);
        
        return jobOrderService.saveJobOrder(jobOrder);
    }
}
