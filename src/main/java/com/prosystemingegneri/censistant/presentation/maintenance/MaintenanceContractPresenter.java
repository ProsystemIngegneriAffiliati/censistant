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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.ScheduledMaintenance;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceContractPresenter implements Serializable{
    @Inject
    MaintenanceContractService service;
    
    private MaintenanceContract maintenanceContract;
    private Long id;
    
    @Resource
    Validator validator;
    
    private CustomerSupplier tempCustomer;
    private List<System> customerSystems = new ArrayList<>();
    private DualListModel<System> systems = new DualListModel<>();
    @Inject
    SystemService systemService;
    
    @Inject
    MaintenanceTaskService maintenanceTaskService;
    private final List<MaintenanceTask> maintenanceTasks = new ArrayList<>();
    
    public String saveMaintenanceContract() {
        try {
            maintenanceContract.getSystems().clear();
            maintenanceContract.getSystems().addAll(systems.getTarget());
            
            boolean isValidated = true;
            for (ConstraintViolation<MaintenanceContract> constraintViolation : validator.validate(maintenanceContract, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return null;
            
            service.saveMaintenanceContract(maintenanceContract);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/maintenance/maintenanceContracts?faces-redirect=true";
    }
    
    public void detailMaintenanceContract() {
        if (maintenanceContract == null && id != null) {
            if (id == 0)
                maintenanceContract = new MaintenanceContract();
            else {
                maintenanceContract = service.readMaintenanceContract(id);
                tempCustomer = maintenanceContract.getCustomer();
                updateCustomerSystem();
            }
        }
    }
    
    public void createNewScheduledMaintenance() {
        maintenanceContract.addScheduledMaintenance(new ScheduledMaintenance());
    }
    
    public DualListModel<System> getSystems() {
        if (tempCustomer == null)
            return new DualListModel<>();
        
        if (systems.getSource().isEmpty() && systems.getTarget().isEmpty()) {
            systems.setSource(service.avaibleSystems(maintenanceContract, tempCustomer));
            systems.setTarget(maintenanceContract.getSystems());
        }
        
        return systems;
    }

    public void setSystems(DualListModel<System> systems) {
        this.systems = systems;
    }
    
    public void onTempCustomerSelect(SelectEvent event) {
        updateCustomerSystem();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaintenanceContract getMaintenanceContract() {
        return maintenanceContract;
    }

    public void setMaintenanceContract(MaintenanceContract maintenanceContract) {
        this.maintenanceContract = maintenanceContract;
    }

    public CustomerSupplier getTempCustomer() {
        return tempCustomer;
    }

    public void setTempCustomer(CustomerSupplier tempCustomer) {
        this.tempCustomer = tempCustomer;
    }

    public List<System> getCustomerSystems() {
        return customerSystems;
    }
    
    public void updateCustomerSystem() {
        customerSystems = systemService.listSystems(0, 0, null, null, null, tempCustomer);
    }
    
    public void updateMaintenanceTasks() {
        if (maintenanceContract != null) {
            maintenanceTasks.clear();
            maintenanceTasks.addAll(maintenanceTaskService.listMaintenanceTasks(0, 0, null, null, null, null, null, null, maintenanceContract, null));
        }
    }

    public List<MaintenanceTask> getMaintenanceTasks() {
        if (maintenanceTasks.isEmpty())
            updateMaintenanceTasks();
        
        return maintenanceTasks;
    }
    
    public void onMaintenanceTaskEdit(MaintenanceTask maintenanceTask) {
        maintenanceTaskService.saveMaintenanceTask(maintenanceTask);
    }
    
}