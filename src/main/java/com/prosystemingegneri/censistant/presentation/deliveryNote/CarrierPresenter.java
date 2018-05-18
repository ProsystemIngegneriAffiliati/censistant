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
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteCommon;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
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
public class CarrierPresenter implements Serializable{
    @Inject
    CarrierService service;
    
    private Carrier carrier;
    private Long id;
    
    private String returnPage;
    private DeliveryNoteCommon deliveryNote;
    private Integer activeIndex;
    
    @PostConstruct
    public void init() {
        returnPage = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("returnPage");
        if (returnPage == null || returnPage.isEmpty())
            returnPage = "deliveryNote/carriers";
        
        deliveryNote = (DeliveryNoteCommon) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
    }
    
    public String saveCarrier() {
        try {
            service.saveCarrier(carrier);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return exit();
    }
    
    public String cancel() {
        return exit();
    }
    
    private String exit() {
        putExternalContext();
        
        return "/secured/" + returnPage + "?faces-redirect=true";
    }
    
    public void detailCarrier() {
        if (carrier == null) {
            if (id == null || id == 0)
                carrier = new Carrier();
            else
                carrier = service.readCarrier(id);
        }
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNote);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}