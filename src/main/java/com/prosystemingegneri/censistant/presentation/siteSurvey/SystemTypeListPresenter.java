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
package com.prosystemingegneri.censistant.presentation.siteSurvey;

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SystemTypeService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SystemTypeListPresenter implements Serializable{
    @Inject
    SystemTypeService service;
    
    private List<SystemType> systemTypes;
    private List<SystemType> selectedSystemTypes;
    
    @PostConstruct
    public void init() {
        systemTypes = service.listSystemTypes(null);
    }
    
    public void deleteSystemTypes() {
        if (selectedSystemTypes != null && !selectedSystemTypes.isEmpty()) {
            for (SystemType systemTypeTemp : selectedSystemTypes) {
                try {
                    service.deleteSystemType(systemTypeTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            systemTypes = service.listSystemTypes(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<SystemType> completeSystemTypes(String name) {
        return service.listSystemTypes(name);
    }

    public List<SystemType> getSystemTypes() {
        return systemTypes;
    }

    public List<SystemType> getSelectedSystemTypes() {
        return selectedSystemTypes;
    }

    public void setSelectedSystemTypes(List<SystemType> selectedSystemTypes) {
        this.selectedSystemTypes = selectedSystemTypes;
    }
}