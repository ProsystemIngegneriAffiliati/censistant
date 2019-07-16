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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.sales.boundary.WorkingOperationService;
import com.prosystemingegneri.censistant.business.sales.entity.WorkingOperation;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class WorkingOperationListPresenter implements Serializable{
    @Inject
    WorkingOperationService service;
    
    private List<WorkingOperation> workingOperations;
    private List<WorkingOperation> selectedWorkingOperations;
    
    @PostConstruct
    public void init() {
        workingOperations = service.list(null);
    }
    
    public void delete() {
        if (selectedWorkingOperations != null && !selectedWorkingOperations.isEmpty()) {
            for (WorkingOperation workingOperationTemp : selectedWorkingOperations) {
                try {
                    service.delete(workingOperationTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            workingOperations = service.list(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<WorkingOperation> complete(String name) {
        return service.list(name);
    }

    public List<WorkingOperation> getWorkingOperations() {
        return workingOperations;
    }

    public List<WorkingOperation> getSelectedWorkingOperations() {
        return selectedWorkingOperations;
    }

    public void setSelectedWorkingOperations(List<WorkingOperation> selectedWorkingOperations) {
        this.selectedWorkingOperations = selectedWorkingOperations;
    }
}