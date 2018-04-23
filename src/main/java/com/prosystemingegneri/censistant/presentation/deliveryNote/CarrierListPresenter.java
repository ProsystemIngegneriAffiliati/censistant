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

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.CarrierService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.Carrier;
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
public class CarrierListPresenter implements Serializable{
    @Inject
    CarrierService service;
    
    private List<Carrier> carriers;
    private List<Carrier> selectedCarriers;
    
    @PostConstruct
    public void init() {
        carriers = service.listCarriers(0, 0, null, Boolean.FALSE, null);
    }
    
    public void deleteCarriers() {
        if (selectedCarriers != null && !selectedCarriers.isEmpty()) {
            for (Carrier carrierTemp : selectedCarriers) {
                try {
                    service.deleteCarrier(carrierTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            carriers = service.listCarriers(0, 0, null, Boolean.FALSE, null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Carrier> completeCarriers(String name) {
        return service.listCarriers(0, 0, null, Boolean.FALSE, name);
    }

    public List<Carrier> getCarriers() {
        return carriers;
    }

    public List<Carrier> getSelectedCarriers() {
        return selectedCarriers;
    }

    public void setSelectedCarriers(List<Carrier> selectedCarriers) {
        this.selectedCarriers = selectedCarriers;
    }
}