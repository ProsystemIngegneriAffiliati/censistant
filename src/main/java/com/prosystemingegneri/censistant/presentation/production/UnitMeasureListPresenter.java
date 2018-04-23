/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.production.boundary.UnitMeasureService;
import com.prosystemingegneri.censistant.business.production.entity.UnitMeasure;
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
public class UnitMeasureListPresenter implements Serializable{
    @Inject
    UnitMeasureService service;
    
    private List<UnitMeasure> unitMeasures;
    private List<UnitMeasure> selectedUnitMeasures;
    
    @PostConstruct
    public void init() {
        unitMeasures = service.listUnitMeasures(null);
    }
    
    public void deleteUnitMeasures() {
        if (selectedUnitMeasures != null && !selectedUnitMeasures.isEmpty()) {
            for (UnitMeasure unitMeasureTemp : selectedUnitMeasures) {
                try {
                    service.deleteUnitMeasure(unitMeasureTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            unitMeasures = service.listUnitMeasures(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<UnitMeasure> completeUnitMeasures(String name) {
        return service.listUnitMeasures(name);
    }

    public List<UnitMeasure> getUnitMeasures() {
        return unitMeasures;
    }

    public List<UnitMeasure> getSelectedUnitMeasures() {
        return selectedUnitMeasures;
    }

    public void setSelectedUnitMeasures(List<UnitMeasure> selectedUnitMeasures) {
        this.selectedUnitMeasures = selectedUnitMeasures;
    }
}