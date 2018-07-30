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
package com.prosystemingegneri.censistant.presentation.deliveryNote;

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.DeliveryNoteOutService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
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
public class DeliveryNoteOutListPresenter implements Serializable{
    @Inject
    DeliveryNoteOutService service;
    
    private DeliveryNoteOutLazyDataModel lazyDeliveryNoteOuts;
    private List<DeliveryNoteOut> selectedDeliveryNoteOuts;
    
    @PostConstruct
    public void init() {
        lazyDeliveryNoteOuts = new DeliveryNoteOutLazyDataModel(service);
    }
    
    public void deleteDeliveryNoteOut() {
        if (selectedDeliveryNoteOuts != null && !selectedDeliveryNoteOuts.isEmpty()) {
            for (DeliveryNoteOut siteSurveyReportTemp : selectedDeliveryNoteOuts) {
                try {
                    service.deleteDeliveryNoteOut(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public DeliveryNoteOutLazyDataModel getLazyDeliveryNoteOuts() {
        return lazyDeliveryNoteOuts;
    }

    public void setLazyDeliveryNoteOuts(DeliveryNoteOutLazyDataModel lazyDeliveryNoteOuts) {
        this.lazyDeliveryNoteOuts = lazyDeliveryNoteOuts;
    }

    public List<DeliveryNoteOut> getSelectedDeliveryNoteOuts() {
        return selectedDeliveryNoteOuts;
    }

    public void setSelectedDeliveryNoteOuts(List<DeliveryNoteOut> selectedDeliveryNoteOut) {
        this.selectedDeliveryNoteOuts = selectedDeliveryNoteOut;
    }
    
    public Date getStart() {
        return lazyDeliveryNoteOuts.getStart();
    }

    public void setStart(Date start) {
        lazyDeliveryNoteOuts.setStart(start);
    }

    public Date getEnd() {
        return lazyDeliveryNoteOuts.getEnd();
    }

    public void setEnd(Date end) {
        lazyDeliveryNoteOuts.setEnd(end);
    }
}
