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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.controller.PlantCreation;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteCommon;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class PlantPresenter implements Serializable {
    private Plant plant;
    private CustomerSupplier customerSupplier;
    private Boolean isCustomerView;
    private String returnInitialPage;
    
    private SiteSurveyRequest siteSurveyRequest;
    private SiteSurveyReport siteSurveyReport;
    
    private Offer offer;
    private JobOrder jobOrder;
    
    private DeliveryNoteCommon deliveryNote;
    
    @PostConstruct
    public void init() {
        customerSupplier = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        isCustomerView = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("isCustomerView");
        returnInitialPage = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("returnPage");
        
        siteSurveyRequest = (SiteSurveyRequest) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyRequest");
        siteSurveyReport = (SiteSurveyReport) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyReport");
        
        offer = (Offer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("offer");
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        
        deliveryNote = (DeliveryNoteCommon) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        
        plant = (Plant) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("plant");
        if (plant == null) {
            if (isCustomerView)
                plant = PlantCreation.createCustomerPlant();
            else
                plant = PlantCreation.createSupplierPlant();
        }
    }
    
    public String savePlant() {
        if (plant.getCustomerSupplier() == null)
            customerSupplier.addPlant(plant);
        
        putExternalContext();
        
        return chooseReturnString();
    }
    
    public String cancel() {
        putExternalContext();
        
        return chooseReturnString();
    }
    
    private String chooseReturnString() {
        String returnString;
        if (isCustomerView)
            returnString = "/secured/customerSupplier/customer";
        else
            returnString = "/secured/customerSupplier/supplier";
        
        return returnString + "?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", isCustomerView);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", returnInitialPage);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyRequest", siteSurveyRequest);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyReport", siteSurveyReport);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("offer", offer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNote);
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }   
}