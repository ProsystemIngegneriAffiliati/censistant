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

import com.prosystemingegneri.censistant.business.sales.boundary.TimeSpentService;
import com.prosystemingegneri.censistant.business.sales.entity.TimeSpent;
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
public class TimeSpentListPresenter implements Serializable {
    @Inject
    private TimeSpentService service;
    
    private TimeSpentLazyDataModel lazyTimeSpents;
    private List<TimeSpent> selectedTimeSpents;
    
    @PostConstruct
    public void init() {
        lazyTimeSpents = new TimeSpentLazyDataModel(service);
    }
    
    public void delete() {
        if (selectedTimeSpents != null && !selectedTimeSpents.isEmpty())
            for (TimeSpent timeSpent : selectedTimeSpents)
                try {
                    service.delete(timeSpent.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public TimeSpentLazyDataModel getLazyTimeSpents() {
        return lazyTimeSpents;
    }

    public void setLazyTimeSpents(TimeSpentLazyDataModel lazyTimeSpents) {
        this.lazyTimeSpents = lazyTimeSpents;
    }

    public List<TimeSpent> getSelectedTimeSpents() {
        return selectedTimeSpents;
    }

    public void setSelectedTimeSpents(List<TimeSpent> selectedTimeSpent) {
        this.selectedTimeSpents = selectedTimeSpent;
    }
    
}