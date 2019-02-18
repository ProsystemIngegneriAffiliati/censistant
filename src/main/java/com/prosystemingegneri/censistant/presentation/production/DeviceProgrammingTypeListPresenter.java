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

import com.prosystemingegneri.censistant.business.production.boundary.DeviceProgrammingTypeService;
import com.prosystemingegneri.censistant.business.production.entity.DeviceProgrammingType;
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
public class DeviceProgrammingTypeListPresenter implements Serializable{
    @Inject
    DeviceProgrammingTypeService service;
    
    private List<DeviceProgrammingType> deviceProgrammingTypes;
    private List<DeviceProgrammingType> selectedDeviceProgrammingTypes;
    
    @PostConstruct
    public void init() {
        deviceProgrammingTypes = service.listDeviceProgrammingTypes(null);
    }
    
    public void deleteDeviceProgrammingTypes() {
        if (selectedDeviceProgrammingTypes != null && !selectedDeviceProgrammingTypes.isEmpty()) {
            for (DeviceProgrammingType deviceProgrammingTypeTemp : selectedDeviceProgrammingTypes) {
                try {
                    service.deleteDeviceProgrammingType(deviceProgrammingTypeTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            deviceProgrammingTypes = service.listDeviceProgrammingTypes(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<DeviceProgrammingType> completeDeviceProgrammingTypes(String name) {
        return service.listDeviceProgrammingTypes(name);
    }
    
    public List<String> completeDeviceProgrammingTypesStr(String name) {
        List<String> result = new ArrayList<>();
        List<DeviceProgrammingType> foundDeviceProgrammingTypes = service.listDeviceProgrammingTypes(name);
        if (foundDeviceProgrammingTypes != null)
            for (DeviceProgrammingType deviceProgrammingType : foundDeviceProgrammingTypes)
                result.add(deviceProgrammingType.getName());
        
        return result;
    }

    public List<DeviceProgrammingType> getDeviceProgrammingTypes() {
        return deviceProgrammingTypes;
    }

    public List<DeviceProgrammingType> getSelectedDeviceProgrammingTypes() {
        return selectedDeviceProgrammingTypes;
    }

    public void setSelectedDeviceProgrammingTypes(List<DeviceProgrammingType> selectedDeviceProgrammingTypes) {
        this.selectedDeviceProgrammingTypes = selectedDeviceProgrammingTypes;
    }
}