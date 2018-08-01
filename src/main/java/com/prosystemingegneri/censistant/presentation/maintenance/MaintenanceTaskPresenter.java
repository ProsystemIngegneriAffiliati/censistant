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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.ScheduledMaintenance;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
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

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceTaskPresenter implements Serializable{
    @Inject
    MaintenanceTaskService service;
    
    private MaintenanceTask maintenanceTask;
    private Long id;
    
    private boolean wasClosed;
    
    @Resource
    Validator validator;
    
    public String saveMaintenanceTask() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<MaintenanceTask> constraintViolation : validator.validate(maintenanceTask, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return null;

            service.saveMaintenanceTask(maintenanceTask, wasClosed);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/maintenance/maintenanceTasks?faces-redirect=true";
    }
    
    public void detailMaintenanceTask() {
        if (maintenanceTask == null && id != null) {
            if (id == 0)
                maintenanceTask = new MaintenanceTask();
            else {
                maintenanceTask = service.readMaintenanceTask(id);
                wasClosed = maintenanceTask.getClosed() != null;
            }
        }
    }
    
    public void onScheduledMaintenancSelect(SelectEvent event) {
        ScheduledMaintenance scheduledMaintenance = (ScheduledMaintenance) event.getObject();
        maintenanceTask.setScheduledMaintenance(scheduledMaintenance);
        maintenanceTask.setDescription(scheduledMaintenance.getDescription());
    }
    
    public void clearCustomerSignature() {
        maintenanceTask.setCustomerSignature(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }
    
}