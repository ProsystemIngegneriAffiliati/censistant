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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceContractPresenter implements Serializable{
    @Inject
    private MaintenanceContractService service;
    
    private MaintenanceContract maintenanceContract;
    private Long id;
    
    private CustomerSupplier tempCustomer;
    private List<System> avaibleSystems = new ArrayList<>();
    private System systemToBeAdded;
    
    @Inject
    private SystemService systemService;
    
    @Resource
    Validator validator;
    
    public String save() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<MaintenanceContract> constraintViolation : validator.validate(maintenanceContract, Default.class)) {
                isValidated = false;
                Messages.create("Error").error().detail(constraintViolation.getMessage()).add();
            }
            if (!isValidated)
                return null;
            
            maintenanceContract = service.save(maintenanceContract);
        } catch (EJBException e) {
            Messages.create("Error").error().detail(ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()).add();
            return null;
        }
        
        Messages.create("success").detail("saved").flash().add();
        if (id == 0L)
            id = maintenanceContract.getId();
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&includeViewParams=true";
    }
    
    public void detail() {
        if (id != null) {
            if (id == 0)
                maintenanceContract = service.create();
            else {
                maintenanceContract = service.find(id);
                tempCustomer = maintenanceContract.getCustomer();
                updateAvaibleSystems();
            }
        }
    }
    
    public void onTempCustomerSelect(SelectEvent event) {
        updateAvaibleSystems();
    }
    
    public void onSystemSelect(SelectEvent event) {
        if (systemToBeAdded != null) {
            maintenanceContract.addContractedSystem(new ContractedSystem(systemToBeAdded));
            avaibleSystems.remove(systemToBeAdded);
            systemToBeAdded = null;
        }
    }
    
    public void updateAvaibleSystems() {
        avaibleSystems = service.avaibleSystems(maintenanceContract, tempCustomer);
    }
    
    public void createNewMaintenancePlan(ContractedSystem contractedSystem) {
        contractedSystem.addMaintenancePlan(new MaintenancePlan());
    }
    
    public void removeMaintenancePlan(ContractedSystem contractedSystem, MaintenancePlan maintenancePlan) {
        contractedSystem.removeMaintenancePlan(maintenancePlan);
    }

    public MaintenanceContract getMaintenanceContract() {
        return maintenanceContract;
    }

    public void setMaintenanceContract(MaintenanceContract maintenanceContract) {
        this.maintenanceContract = maintenanceContract;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getTempCustomer() {
        return tempCustomer;
    }

    public void setTempCustomer(CustomerSupplier tempCustomer) {
        this.tempCustomer = tempCustomer;
    }

    public List<System> getAvaibleSystems() {
        return avaibleSystems;
    }

    public void setAvaibleSystems(List<System> avaibleSystems) {
        this.avaibleSystems = avaibleSystems;
    }

    public System getSystemToBeAdded() {
        return systemToBeAdded;
    }

    public void setSystemToBeAdded(System systemToBeAdded) {
        this.systemToBeAdded = systemToBeAdded;
    }
    
}