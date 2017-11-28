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
package com.prosystemingegneri.censistant.presentation.deliveryNote;

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.DeliveryNoteInService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.Date;
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
public class DeliveryNoteInListPresenter implements Serializable{
    @Inject
    DeliveryNoteInService service;
    
    private DeliveryNoteInLazyDataModel lazyDeliveryNoteIns;
    private List<DeliveryNoteIn> selectedDeliveryNoteIns;
    
    @PostConstruct
    public void init() {
        lazyDeliveryNoteIns = new DeliveryNoteInLazyDataModel(service);
    }
    
    public void deleteDeliveryNoteIn() {
        if (selectedDeliveryNoteIns != null && !selectedDeliveryNoteIns.isEmpty()) {
            for (DeliveryNoteIn siteSurveyReportTemp : selectedDeliveryNoteIns) {
                try {
                    service.deleteDeliveryNoteIn(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public DeliveryNoteInLazyDataModel getLazyDeliveryNoteIns() {
        return lazyDeliveryNoteIns;
    }

    public void setLazyDeliveryNoteIns(DeliveryNoteInLazyDataModel lazyDeliveryNoteIns) {
        this.lazyDeliveryNoteIns = lazyDeliveryNoteIns;
    }

    public List<DeliveryNoteIn> getSelectedDeliveryNoteIns() {
        return selectedDeliveryNoteIns;
    }

    public void setSelectedDeliveryNoteIns(List<DeliveryNoteIn> selectedDeliveryNoteIn) {
        this.selectedDeliveryNoteIns = selectedDeliveryNoteIn;
    }
    
    public Date getStart() {
        return lazyDeliveryNoteIns.getStart();
    }

    public void setStart(Date start) {
        lazyDeliveryNoteIns.setStart(start);
    }

    public Date getEnd() {
        return lazyDeliveryNoteIns.getEnd();
    }

    public void setEnd(Date end) {
        lazyDeliveryNoteIns.setEnd(end);
    }
}
