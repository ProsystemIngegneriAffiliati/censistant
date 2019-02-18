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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.maintenance.boundary.PreventiveMaintenanceService;
import com.prosystemingegneri.censistant.business.maintenance.entity.PreventiveMaintenance;
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
public class PreventiveMaintenanceListPresenter implements Serializable{
    @Inject
    PreventiveMaintenanceService service;
    
    private List<PreventiveMaintenance> preventiveMaintenances;
    private List<PreventiveMaintenance> selectedPreventiveMaintenances;
    
    @PostConstruct
    public void init() {
        preventiveMaintenances = service.listPreventiveMaintenances(null);
    }
    
    public void deletePreventiveMaintenances() {
        if (selectedPreventiveMaintenances != null && !selectedPreventiveMaintenances.isEmpty()) {
            for (PreventiveMaintenance preventiveMaintenanceTemp : selectedPreventiveMaintenances) {
                try {
                    service.deletePreventiveMaintenance(preventiveMaintenanceTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            preventiveMaintenances = service.listPreventiveMaintenances(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<PreventiveMaintenance> completePreventiveMaintenances(String name) {
        return service.listPreventiveMaintenances(name);
    }

    public List<PreventiveMaintenance> getPreventiveMaintenances() {
        return preventiveMaintenances;
    }

    public List<PreventiveMaintenance> getSelectedPreventiveMaintenances() {
        return selectedPreventiveMaintenances;
    }

    public void setSelectedPreventiveMaintenances(List<PreventiveMaintenance> selectedPreventiveMaintenances) {
        this.selectedPreventiveMaintenances = selectedPreventiveMaintenances;
    }
}