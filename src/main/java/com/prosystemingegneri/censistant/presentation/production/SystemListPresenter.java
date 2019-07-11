/*
 * Copyright (C) 2017-2019 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.presentation.production;

import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SystemListPresenter implements Serializable{
    @Inject
    SystemService service;
    
    private SystemLazyDataModel lazySystems;
    private List<System> selectedSystems;
    
    private List<System> systems;
    
    @PostConstruct
    public void init() {
        lazySystems = new SystemLazyDataModel(service);
        systems = new ArrayList<>();
    }
    
    public void deleteSystem() {
        if (selectedSystems != null && !selectedSystems.isEmpty()) {
            for (System siteSurveyReportTemp : selectedSystems) {
                try {
                    service.deleteSystem(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<System> completeSystems(String description) {
        systems = service.listSystems(0, 10, null, null, description, null, null);
        return systems;
    }
    
    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultSystem Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<System> getSystems(System defaultSystem) {
        if (systems.isEmpty())
            systems.add(defaultSystem);
        return systems;
    }

    public SystemLazyDataModel getLazySystems() {
        return lazySystems;
    }

    public void setLazySystems(SystemLazyDataModel lazySystems) {
        this.lazySystems = lazySystems;
    }

    public List<System> getSelectedSystems() {
        return selectedSystems;
    }

    public void setSelectedSystems(List<System> selectedSystem) {
        this.selectedSystems = selectedSystem;
    }
}
