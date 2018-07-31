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

import com.prosystemingegneri.censistant.business.sales.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.sales.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class MaintenanceTaskListPresenter implements Serializable{
    @Inject
    MaintenanceTaskService service;
    
    private MaintenanceTaskLazyDataModel lazyMaintenanceTasks;
    private List<MaintenanceTask> selectedMaintenanceTasks;
    
    @PostConstruct
    public void init() {
        lazyMaintenanceTasks = new MaintenanceTaskLazyDataModel(service);
    }
    
    public void deleteMaintenanceTask() {
        if (selectedMaintenanceTasks != null && !selectedMaintenanceTasks.isEmpty()) {
            for (MaintenanceTask siteSurveyReportTemp : selectedMaintenanceTasks) {
                try {
                    service.deleteMaintenanceTask(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public String calculateExpiryColor(MaintenanceTask maintenanceTask) {
        if (maintenanceTask.getClosed() == null) {
            GregorianCalendar oneMonthAhead = new GregorianCalendar();
            oneMonthAhead.add(Calendar.MONTH, 1);

            GregorianCalendar oneWeekAhead = new GregorianCalendar();
            oneWeekAhead.add(Calendar.WEEK_OF_YEAR, 1);

            GregorianCalendar yesterday = new GregorianCalendar();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);

            GregorianCalendar scadCal = new GregorianCalendar();
            scadCal.setTime(maintenanceTask.getExpiry());

            if (scadCal.before(yesterday))
                return "blue-grey-row";
            if (scadCal.before(oneWeekAhead))
                return "red-row";
            if (scadCal.before(oneMonthAhead))
                return "amber-row";            
        }
        
        return "";
    }

    public MaintenanceTaskLazyDataModel getLazyMaintenanceTasks() {
        return lazyMaintenanceTasks;
    }

    public void setLazyMaintenanceTasks(MaintenanceTaskLazyDataModel lazyMaintenanceTasks) {
        this.lazyMaintenanceTasks = lazyMaintenanceTasks;
    }

    public List<MaintenanceTask> getSelectedMaintenanceTasks() {
        return selectedMaintenanceTasks;
    }

    public void setSelectedMaintenanceTasks(List<MaintenanceTask> selectedMaintenanceTask) {
        this.selectedMaintenanceTasks = selectedMaintenanceTask;
    }
}
