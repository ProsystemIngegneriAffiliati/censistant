/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.purchasing.boundary.BoxService;
import com.prosystemingegneri.censistant.business.purchasing.entity.Box;
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
public class BoxListPresenter implements Serializable{
    @Inject
    BoxService service;
    
    private BoxLazyDataModel lazyBoxes;
    private List<Box> selectedBoxes;
    
    private List<Box> boxes;
    
    @PostConstruct
    public void init() {
        lazyBoxes = new BoxLazyDataModel(service);
    }
    
    public void deleteBox() {
        if (selectedBoxes != null && !selectedBoxes.isEmpty()) {
            for (Box siteSurveyReportTemp : selectedBoxes) {
                try {
                    service.deleteBox(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Box> completeBoxes(String value) {
        return service.listBoxes(0, 10, null, null, value);
    }

    public List<Box> getBoxes() {
        if (boxes == null || boxes.isEmpty())
            boxes = service.listBoxes(0, 0, null, null, null);
        return boxes;
    }

    public BoxLazyDataModel getLazyBoxes() {
        return lazyBoxes;
    }

    public void setLazyBoxes(BoxLazyDataModel lazyBoxes) {
        this.lazyBoxes = lazyBoxes;
    }

    public List<Box> getSelectedBoxes() {
        return selectedBoxes;
    }

    public void setSelectedBoxes(List<Box> selectedBox) {
        this.selectedBoxes = selectedBox;
    }
}
