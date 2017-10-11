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
package com.prosystemingegneri.censistant.presentation.siteSurvey;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SiteSurveyReportPresenter implements Serializable{
    @Inject
    SiteSurveyReportService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    
    private SiteSurveyReport siteSurveyReport;
    private Long id;
    
    private SiteSurveyRequest selectedRequest;
    
    private List<Plant> plants;
    
    @PostConstruct
    public void init() {
        siteSurveyReport = (SiteSurveyReport) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyReport");
        if (siteSurveyReport != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                siteSurveyReport.getRequest().setCustomer(customer);
                if (!customer.getPlants().isEmpty()) {
                    Plant mostRecentPlant = customer.getPlants().get(0);
                    for (Plant plant : customer.getPlants())
                        if (plant.getId() != null && plant.getId() > mostRecentPlant.getId())
                            mostRecentPlant = plant;
                    
                    siteSurveyReport.setPlant(mostRecentPlant);
                }
            }
        }
    }
    
    public String saveSiteSurveyReport() {
        try {
            service.saveSiteSurveyReport(siteSurveyReport);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/siteSurvey/siteSurveyReports?faces-redirect=true";
    }
    
    public void detailSiteSurveyReport() {
        if (siteSurveyReport == null && id != null) {
            if (id == 0)
                siteSurveyReport = service.createNewSiteSurveyReport();
            else
                siteSurveyReport = service.readSiteSurveyReport(id);
        }
    }
    
    public String createPotentialCustomer() {
        return prepareToOpenCustomer(customerSupplierService.createPotentialCustomer());
    }
    
    public String openCustomer() {
        return prepareToOpenCustomer(siteSurveyReport.getRequest().getCustomer());
    }
    
    private String prepareToOpenCustomer(CustomerSupplier customer) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyReport", siteSurveyReport);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.TRUE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "siteSurvey/siteSurveyReport");
        
        return "/secured/customerSupplier/customer?faces-redirect=true";
    }
    
    public List<Plant> completePlant(String value) {
        return customerSupplierService.listPlants(0, 10, "address", Boolean.TRUE, siteSurveyReport.getRequest().getCustomer(), value);
    }

    public List<Plant> getPlants() {
        if (plants == null || plants.isEmpty())
            plants = customerSupplierService.listPlants(0, 0, "address", Boolean.TRUE, siteSurveyReport.getRequest().getCustomer(), null);
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }
    
    public void onSiteSurveyRequestSelect(SelectEvent event) {
        siteSurveyReport.addRequest((SiteSurveyRequest) event.getObject());
    }

    public SiteSurveyReport getSiteSurveyReport() {
        return siteSurveyReport;
    }

    public void setSiteSurveyReport(SiteSurveyReport siteSurveyReport) {
        this.siteSurveyReport = siteSurveyReport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SiteSurveyRequest getSelectedRequest() {
        return selectedRequest;
    }

    public void setSelectedRequest(SiteSurveyRequest selectedRequest) {
        this.selectedRequest = selectedRequest;
    }
    
}