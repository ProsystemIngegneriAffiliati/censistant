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
package com.prosystemingegneri.censistant.business.maintenance.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class MaintenancePlan extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(optional = false)
    private ContractedSystem contractedSystem;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private MaintenanceType maintenanceType;
    
    @Min(1)
    private Integer maintenanceTasksNumber;  //number of maintenace tasks to be done in MaintenanceContract's DURATION_MONTHS
    
    @Version
    private int version;

    public MaintenancePlan() {
        maintenanceTasksNumber = 1;
    }

    public MaintenancePlan(MaintenanceType maintenanceType) {
        this();
        this.maintenanceType = maintenanceType;
    }

    public ContractedSystem getContractedSystem() {
        return contractedSystem;
    }

    public void setContractedSystem(ContractedSystem contractedSystem) {
        this.contractedSystem = contractedSystem;
    }

    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public Integer getMaintenanceTasksNumber() {
        return maintenanceTasksNumber;
    }

    public void setMaintenanceTasksNumber(Integer maintenanceTasksNumber) {
        this.maintenanceTasksNumber = maintenanceTasksNumber;
    }

    @Override
    public Long getId() {
        return id;
    }
    
}
