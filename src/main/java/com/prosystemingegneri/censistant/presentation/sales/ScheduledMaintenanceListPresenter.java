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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.sales.boundary.ScheduledMaintenanceService;
import com.prosystemingegneri.censistant.business.sales.entity.ScheduledMaintenance;
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
public class ScheduledMaintenanceListPresenter implements Serializable{
    @Inject
    ScheduledMaintenanceService service;
    
    private ScheduledMaintenanceLazyDataModel lazyScheduledMaintenances;
    private List<ScheduledMaintenance> selectedScheduledMaintenances;
    
    private List<ScheduledMaintenance> scheduledMaintenances;
    
    @PostConstruct
    public void init() {
        lazyScheduledMaintenances = new ScheduledMaintenanceLazyDataModel(service);
    }
    
    public void deleteScheduledMaintenance() {
        if (selectedScheduledMaintenances != null && !selectedScheduledMaintenances.isEmpty()) {
            for (ScheduledMaintenance siteSurveyReportTemp : selectedScheduledMaintenances) {
                try {
                    service.deleteScheduledMaintenance(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<ScheduledMaintenance> completeScheduledMaintenances(String description) {
        return service.listScheduledMaintenances(0, 10, null, null, description, null);
    }
    
    //Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
    public List<ScheduledMaintenance> getScheduledMaintenances() {
        if (scheduledMaintenances == null || scheduledMaintenances.isEmpty())
            scheduledMaintenances = service.listScheduledMaintenances(0, 0, null, null, null, null);
        
        return scheduledMaintenances;
    }

    public ScheduledMaintenanceLazyDataModel getLazyScheduledMaintenances() {
        return lazyScheduledMaintenances;
    }

    public void setLazyScheduledMaintenances(ScheduledMaintenanceLazyDataModel lazyScheduledMaintenances) {
        this.lazyScheduledMaintenances = lazyScheduledMaintenances;
    }

    public List<ScheduledMaintenance> getSelectedScheduledMaintenances() {
        return selectedScheduledMaintenances;
    }

    public void setSelectedScheduledMaintenances(List<ScheduledMaintenance> selectedScheduledMaintenance) {
        this.selectedScheduledMaintenances = selectedScheduledMaintenance;
    }
    
}
