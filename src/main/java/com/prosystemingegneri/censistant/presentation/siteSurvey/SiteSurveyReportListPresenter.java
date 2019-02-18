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

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import static com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_.customerSupplier;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class SiteSurveyReportListPresenter implements Serializable{
    @Inject
    SiteSurveyReportService service;
    
    private SiteSurveyReportLazyDataModel lazySiteSurveyReports;
    private List<SiteSurveyReport> selectedSiteSurveyReports;
    
    private List<SiteSurveyReport> reportsNotAssociatedToOffer;
    
    @Inject
    private CustomerSupplierService customerSupplierService;
    private Plant plant;
    private Plant newPlant;
    private List<Plant> plants;
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        lazySiteSurveyReports = new SiteSurveyReportLazyDataModel(service);
        plants = new ArrayList<>();
    }
    
    public void deleteSiteSurveyReport() {
        if (selectedSiteSurveyReports != null && !selectedSiteSurveyReports.isEmpty()) {
            for (SiteSurveyReport siteSurveyReportTemp : selectedSiteSurveyReports) {
                try {
                    service.deleteSiteSurveyReport(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<SiteSurveyReport> completeReportsNotAssociatedToOffer(String name) {
        return service.listSiteSurveyReports(0, 10, "expected", Boolean.FALSE, null, null, null, name, null, null, null, null, Boolean.FALSE);
    }
    
    public void createNewPlant() {
        if (plant != null) {
            newPlant = new Plant();
            plant.getCustomerSupplier().addPlant(newPlant);
        }
    }
    
    public String createNewSiteSurveyReport() {
        if (!saveCustomer())
            return null;
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idPlant", plant.getId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "/secured/siteSurvey/siteSurveyReports");
        return "/secured/siteSurvey/siteSurveyReport?faces-redirect=true";
    }
    
    private boolean saveCustomer() {
        if (newPlant != null) {
            CustomerSupplier customer = newPlant.getCustomerSupplier();
            try {
                boolean isValidated = true;
                for (ConstraintViolation<CustomerSupplier> constraintViolation : validator.validate(customer, Default.class)) {
                    isValidated = false;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
                }
                if (!isValidated)
                    return false;

                plant = customerSupplierService.saveCustomerSupplier(customer).getPlants().get(customer.getPlants().size() - 1);
            } catch (EJBException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                return false;
            }
        }
        
        return true;
    }

    public List<SiteSurveyReport> getReportsNotAssociatedToOffer() {
        if (reportsNotAssociatedToOffer == null || reportsNotAssociatedToOffer.isEmpty())
            reportsNotAssociatedToOffer = service.listSiteSurveyReports(0, 0, null, Boolean.FALSE, null, null, null, null, null, null, null, null, Boolean.FALSE);
        return reportsNotAssociatedToOffer;
    }
    
    public List<Plant> completePlantsAndCustomers(String nameAddress) {
        plants = customerSupplierService.listPlants(0, 25, null, null, null, null, null, nameAddress);
        return plants;
    }
    
    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultPlant Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<Plant> getPlants(Plant defaultPlant) {
        if (plants.isEmpty())
            plants.add(defaultPlant);
        return plants;
    }

    public SiteSurveyReportLazyDataModel getLazySiteSurveyReports() {
        return lazySiteSurveyReports;
    }

    public void setLazySiteSurveyReports(SiteSurveyReportLazyDataModel lazySiteSurveyReports) {
        this.lazySiteSurveyReports = lazySiteSurveyReports;
    }

    public List<SiteSurveyReport> getSelectedSiteSurveyReports() {
        return selectedSiteSurveyReports;
    }

    public void setSelectedSiteSurveyReports(List<SiteSurveyReport> selectedSiteSurveyReport) {
        this.selectedSiteSurveyReports = selectedSiteSurveyReport;
    }
    
    public Date getStart() {
        return lazySiteSurveyReports.getStart();
    }

    public void setStart(Date start) {
        this.lazySiteSurveyReports.setStart(start);
    }

    public Date getEnd() {
        return lazySiteSurveyReports.getEnd();
    }

    public void setEnd(Date end) {
        this.lazySiteSurveyReports.setEnd(end);
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Plant getNewPlant() {
        return newPlant;
    }

    public void setNewPlant(Plant newPlant) {
        this.newPlant = newPlant;
    }
    
}
