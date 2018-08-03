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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
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
    
    public String saveMaintenanceContract() {
        try {
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
            else
                maintenanceContract = service.readMaintenanceContract(id);
        }
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
    
}