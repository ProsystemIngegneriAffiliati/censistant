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
package com.prosystemingegneri.censistant.business.maintenance.entity2;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.production.entity.System;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;


/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class ContractedSystem extends BaseEntity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(optional = false)
    private MaintenanceContract2 maintenanceContract;
    
    @NotNull
    @ManyToOne(optional = false)
    private System system;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contractedSystem", orphanRemoval = true)
    private final List<MaintenancePlan> maintenancePlans;
    
    @Version
    private int version;

    public ContractedSystem() {
        maintenancePlans = new ArrayList<>();
    }

    public ContractedSystem(System system) {
        this();
        this.system = system;
    }

    public MaintenanceContract2 getMaintenanceContract() {
        return maintenanceContract;
    }

    public void setMaintenanceContract(MaintenanceContract2 maintenanceContract) {
        this.maintenanceContract = maintenanceContract;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }
    
    public void addMaintenancePlan(MaintenancePlan maintenancePlan) {
        if (!maintenancePlans.contains(maintenancePlan)) {
            maintenancePlans.add(maintenancePlan);
            maintenancePlan.setContractedSystem(this);
        }
    }
    
    public void removeMaintenancePlan(MaintenancePlan maintenancePlan) {
        if (maintenancePlans.contains(maintenancePlan)) {
            maintenancePlans.remove(maintenancePlan);
            maintenancePlan.setContractedSystem(this);
        }
    }
    
    public List<MaintenancePlan> getMaintenancePlans() {
        return maintenancePlans;
    }

    @Override
    public Long getId() {
        return id;
    }
    
}
