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
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

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
    
    private List<CustomerSupplier> customers;
    private String customerReturnPage;
    
    private CustomerSupplier newCustomer;
    private Plant newPlant;
    
    @PostConstruct
    public void init() {
        siteSurveyReport = (SiteSurveyReport) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("siteSurveyReport");
        customerReturnPage =(String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("returnPage");
        if (siteSurveyReport != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                siteSurveyReport.getRequest().setCustomer(customer);
                siteSurveyReport.setPlant(getMostRecentPlant(customer));
            }
            clearNewCustomer();
            clearNewPlant();
        }
    }
    
    private Plant getMostRecentPlant(CustomerSupplier customer) {
        if (!customer.getPlants().isEmpty()) {
            Plant mostRecentPlant = customer.getPlants().get(0);
            for (Plant plant : customer.getPlants())
                if (plant.getId() != null && plant.getId() > mostRecentPlant.getId())
                    mostRecentPlant = plant;

            return mostRecentPlant;
        }
        
        return null;
    }
    
    public String saveSiteSurveyReport() {
        try {
            siteSurveyReport = service.saveSiteSurveyReport(siteSurveyReport);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        if (id == null || id == 0)
            id = siteSurveyReport.getId();
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", customerReturnPage);
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successo", "Salvato con successo"));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&includeViewParams=true";
    }
    
    public void detailSiteSurveyReport() {
        if (siteSurveyReport == null && id != null) {
            if (id == 0)
                siteSurveyReport = service.createNewSiteSurveyReport();
            else
                siteSurveyReport = service.readSiteSurveyReport(id);
        }
        
        if (siteSurveyReport == null && id == null) {
            siteSurveyReport = service.createNewSiteSurveyReport();
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            Long idPlant = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idPlant");
            if (idPlant != null && idPlant > 0) {
                Plant plant = customerSupplierService.readPlant(idPlant);
                siteSurveyReport.getRequest().setCustomer(plant.getCustomerSupplier());
                siteSurveyReport.setPlant(plant);
            }
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                siteSurveyReport.getRequest().setCustomer(customer);
                siteSurveyReport.setPlant(getMostRecentPlant(customer));
            }
        }
        
        clearNewCustomer();
        clearNewPlant();
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
    
    public StreamedContent print() {
        try {
            siteSurveyReport = service.saveSiteSurveyReport(siteSurveyReport);
            
            List<SiteSurveyReport> tempBean = new ArrayList<>();
            tempBean.add(siteSurveyReport);
            Map<String, Object> params = new HashMap<>();
            params.put("ReportTitle", "Stampa verbale di sopralluogo");
            //params.put("subReportPath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/document/offerta/") + "/");
            params.put("reportImagePath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/images/") + "/");
            
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/siteSurvey/siteSurveyReport.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, new JRBeanCollectionDataSource(tempBean));
            
            return new ByteArrayContent(JasperExportManager.exportReportToPdf(jasperPrint), "application/pdf", siteSurveyReport.getNumber() + ".pdf");
            
        } catch (JRException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during printing" + 
                    System.lineSeparator() +
                    System.lineSeparator() + ex.getLocalizedMessage()));
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return null;
    }
    
    public List<CustomerSupplier> completeCustomer(String value) {
        customers = customerSupplierService.listCustomerSuppliers(0, 10, "name", Boolean.TRUE, null, null, Boolean.TRUE, null, null, value, null);
        return customers;
    }

    public List<CustomerSupplier> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
            customers.add(siteSurveyReport.getRequest().getCustomer());
        }
        return customers;
    }
    
    public List<Plant> completePlant(String value) {
        return customerSupplierService.listPlants(0, 10, "address", Boolean.TRUE, siteSurveyReport.getRequest().getCustomer(), null, value, null);
    }

    public List<Plant> getPlants() {
        if (plants == null || plants.isEmpty())
            plants = customerSupplierService.listPlants(0, 0, "address", Boolean.TRUE, siteSurveyReport.getRequest().getCustomer(), null, null, null);
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }
    
    public void addNewCustomer() {
        if (newPlant.getName() == null ||newPlant.getName().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("name", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (newPlant.getAddress() == null ||newPlant.getAddress().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("address", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (siteSurveyReport.getRequest().getCustomer() == null) {
            if (newCustomer.getBusinessName() == null ||newCustomer.getBusinessName().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("businessName", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
                return;
            }
            if (newCustomer.getProvenance() == null) {
                FacesContext.getCurrentInstance().addMessage("provenance", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
                return;
            }
            newPlant.setIsHeadOffice(Boolean.TRUE);
            newCustomer.addPlant(newPlant);
            siteSurveyReport.getRequest().setCustomer(newCustomer);
        }
        siteSurveyReport.getRequest().getCustomer().addPlant(newPlant);
        siteSurveyReport.setPlant(newPlant);
        customerSupplierService.saveCustomerSupplier(siteSurveyReport.getRequest().getCustomer());
    }
    
    /*public void addNewPlantToCustomer() {
        if (siteSurveyReport.getRequest() != null && siteSurveyReport.getRequest().getCustomer() != null && newPlant != null) {
            siteSurveyReport.getRequest().getCustomer().addPlant(newPlant);
            customerSupplierService.saveCustomerSupplier(siteSurveyReport.getRequest().getCustomer());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("siteSurveyReport", siteSurveyReport);
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idCustomer", siteSurveyReport.getRequest().getCustomer().getId());
            init();
        }
    }*/
    
    public void clearNewCustomer() {
        newCustomer = new CustomerSupplier(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
    }
    
    public void clearNewPlant() {
        newPlant = new Plant();
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

    public Plant getNewPlant() {
        return newPlant;
    }

    public void setNewPlant(Plant newPlant) {
        this.newPlant = newPlant;
    }

    public CustomerSupplier getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(CustomerSupplier newCustomer) {
        this.newCustomer = newCustomer;
    }

    public String getCustomerReturnPage() {
        return customerReturnPage;
    }

    public void setCustomerReturnPage(String customerReturnPage) {
        this.customerReturnPage = customerReturnPage;
    }
    
}