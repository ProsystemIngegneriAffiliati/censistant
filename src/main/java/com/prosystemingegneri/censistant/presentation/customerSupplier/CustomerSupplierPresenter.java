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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Referee;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteCommon;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import com.prosystemingegneri.censistant.presentation.siteSurvey.SiteSurveyReportLazyDataModel;
import com.prosystemingegneri.censistant.presentation.siteSurvey.SiteSurveyReportListPresenter;
import java.io.Serializable;
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
public class CustomerSupplierPresenter implements Serializable{
    @Inject
    CustomerSupplierService service;
    
    private CustomerSupplier customerSupplier;
    private Long id;
    private Boolean isCustomerView;
    private Boolean isAcquiredCustomer;
    private String returnPage;
    
    private SiteSurveyRequest siteSurveyRequest;
    private SiteSurveyReport siteSurveyReport;
    
    @Inject
    private SiteSurveyReportService siteSurveyReportService;
    @Inject
    SiteSurveyReportListPresenter siteSurveyReportListPresenter;
    private SiteSurveyReportLazyDataModel lazySiteSurveyReports;
    
    private Offer offer;
    private JobOrder jobOrder;
    
    private DeliveryNoteCommon deliveryNote;
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        customerSupplier = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        isCustomerView = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("isCustomerView");
        returnPage = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("returnPage");
        
        siteSurveyRequest = (SiteSurveyRequest) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyRequest");
        siteSurveyReport = (SiteSurveyReport) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyReport");
        
        lazySiteSurveyReports = new SiteSurveyReportLazyDataModel(siteSurveyReportService, customerSupplier);
        
        offer = (Offer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("offer");
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        
        deliveryNote = (DeliveryNoteCommon) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
    }
    
    private boolean onlySaveCustomerSupplier() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<CustomerSupplier> constraintViolation : validator.validate(customerSupplier, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return false;
            
            customerSupplier = service.saveCustomerSupplier(customerSupplier);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return false;
        }
        
        return true;
    }
    
    public String saveCustomerSupplier() {
        if (!onlySaveCustomerSupplier())
            return null;
        
        putMinimalExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idCustomer", customerSupplier.getId());
        
        return "/" + returnPage + "?faces-redirect=true";
    }
    
    public void detailCustomerSupplier() {
        if (customerSupplier == null && id != null) {
            if (id == 0) {
                if (isCustomerView) {
                    if (isAcquiredCustomer)
                        customerSupplier = service.createCustomer();
                    else
                        customerSupplier = service.createPotentialCustomer();
                }
                else
                    customerSupplier = service.createSupplier();
            }
            else
                customerSupplier = service.readCustomerSupplier(id);
            
            lazySiteSurveyReports = new SiteSurveyReportLazyDataModel(siteSurveyReportService, customerSupplier);
        }
    }
    
    public void onIsOnlyInfoChanged() {
        if (customerSupplier.getIsOnlyInfo() && (customerSupplier.getHeadOffice() != null && (customerSupplier.getHeadOffice().getAddress() == null || customerSupplier.getHeadOffice().getAddress().isEmpty())))
            customerSupplier.removePlant(customerSupplier.getHeadOffice());
        else {
            if (!customerSupplier.getIsOnlyInfo() && customerSupplier.getHeadOffice() == null)
                customerSupplier.addPlant(new Plant(Boolean.TRUE, "Sede", null));
        }
    }
    
    public void onPhoneChanged() {
        if (customerSupplier.getHeadOffice() != null && (customerSupplier.getHeadOffice().getAddress() == null || customerSupplier.getHeadOffice().getAddress().isEmpty()))
            customerSupplier.getHeadOffice().setAddress(".");
    }
    
    public String createNewSiteSurveyReport() {
        if (!onlySaveCustomerSupplier())
            return null;
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idCustomer", customerSupplier.getId());
        putExternalContext();
        return "/secured/siteSurvey/siteSurveyReport?faces-redirect=true";
    }
    
    public void createNewPlant() {
        customerSupplier.addPlant(new Plant());
    }
    
    public String detailPlant(Plant plant) {
        if (plant != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("plant", plant);
        putExternalContext();
        
        return "/secured/customerSupplier/plant?faces-redirect=true";
    }
    
    public void deletePlant(Plant plant) {
        if (plant != null)
            customerSupplier.removePlant(plant);
    }
    
    public void createNewReferee() {
        customerSupplier.addReferee(new Referee());
    }
    
    public String detailReferee(Referee referee) {
        if (referee != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("referee", referee);
        putExternalContext();
        
        return "/secured/customerSupplier/referee?faces-redirect=true";
    }
    
    public String cancel() {
        putMinimalExternalContext();
        
        return "/" + returnPage + "?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", isCustomerView);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", returnPage);
        putMinimalExternalContext();
    }
    
    private void putMinimalExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyRequest", siteSurveyRequest);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyReport", siteSurveyReport);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("offer", offer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNote);
    }
    
    public void deleteReferee(Referee referee) {
        if (referee != null)
            customerSupplier.removeReferee(referee);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getCustomerSupplier() {
        return customerSupplier;
    }

    public void setCustomerSupplier(CustomerSupplier customerSupplier) {
        this.customerSupplier = customerSupplier;
    }

    public Boolean getIsCustomerView() {
        return isCustomerView;
    }

    public void setIsCustomerView(Boolean isCustomerView) {
        this.isCustomerView = isCustomerView;
    }

    public String getReturnPage() {
        return returnPage;
    }

    public void setReturnPage(String returnPage) {
        this.returnPage = returnPage;
    }

    public void setIsOnlyInfo(Boolean isOnlyInfo) {
    }

    public SiteSurveyReportLazyDataModel getLazySiteSurveyReports() {
        return lazySiteSurveyReports;
    }

    public void setLazySiteSurveyReports(SiteSurveyReportLazyDataModel lazySiteSurveyReports) {
        this.lazySiteSurveyReports = lazySiteSurveyReports;
    }

    public SiteSurveyReportListPresenter getSiteSurveyReportListPresenter() {
        return siteSurveyReportListPresenter;
    }

    public Boolean getIsAcquiredCustomer() {
        return isAcquiredCustomer;
    }

    public void setIsAcquiredCustomer(Boolean isAcquiredCustomer) {
        this.isAcquiredCustomer = isAcquiredCustomer;
    }
    
}
