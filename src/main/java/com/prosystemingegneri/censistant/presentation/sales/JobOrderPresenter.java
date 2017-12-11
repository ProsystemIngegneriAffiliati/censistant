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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.production.entity.SystemAttachment;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.presentation.DocumentAndImageUtils;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class JobOrderPresenter implements Serializable{
    @Inject
    JobOrderService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    @Inject
    SiteSurveyReportService siteSurveyReportService;
    
    private JobOrder jobOrder;
    private Long id;
    
    private SiteSurveyReport selectedReport;
    
    private List<Plant> plants;
    
    @PostConstruct
    public void init() {
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        if (jobOrder != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                jobOrder.getSiteSurveyReport().getRequest().setCustomer(customer);
                if (!customer.getPlants().isEmpty()) {
                    Plant mostRecentPlant = customer.getPlants().get(0);
                    for (Plant plant : customer.getPlants())
                        if (plant.getId() != null && plant.getId() > mostRecentPlant.getId())
                            mostRecentPlant = plant;
                    
                    jobOrder.getSiteSurveyReport().setPlant(mostRecentPlant);
                }
            }
        }
    }
    
    public String saveJobOrder() {
        try {
            service.saveJobOrder(jobOrder);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/sales/jobOrders?faces-redirect=true";
    }
    
    public void detailJobOrder() {
        if (jobOrder == null && id != null) {
            if (id == 0)
                jobOrder = service.createNewJobOrder();
            else
                jobOrder = service.readJobOrder(id);
        }
    }
    
    public void onSiteSurveyReportSelect(SelectEvent event) {
        jobOrder.addSiteSurveyReport((SiteSurveyReport) event.getObject());
    }
    
    public String createCustomer() {
        return prepareToOpenCustomer(customerSupplierService.createCustomer());
    }
    
    public String openCustomer() {
        return prepareToOpenCustomer(jobOrder.getSiteSurveyReport().getRequest().getCustomer());
    }
    
    private String prepareToOpenCustomer(CustomerSupplier customer) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.TRUE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "sales/jobOrder");
        
        return "/secured/customerSupplier/customer?faces-redirect=true";
    }
    
    public String chooseSystem() {
        //TODO
        return "";
    }
    
    public void handleSystemAttachmentUpload(FileUploadEvent event) {
        String idMessageWidget = "addSystemAttachmentMessage";
        try {
            UploadedFile uploadedFile = event.getFile();
            InputStream input = uploadedFile.getInputstream();
            
            Path folder = Paths.get(DocumentAndImageUtils.IMAGE_PATH);
            String filename = FilenameUtils.getBaseName(uploadedFile.getFileName()); 
            String extension = FilenameUtils.getExtension(uploadedFile.getFileName());
            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
         
            FacesContext.getCurrentInstance().addMessage(idMessageWidget, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "File uploaed successfully"));
            
            SystemAttachment attachment = new SystemAttachment();
            attachment.setName(FilenameUtils.getName(file.toString()));
            attachment.setAttachmentFilename(filename);
            jobOrder.getSystem().addSystemAttachment(attachment);
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(idMessageWidget, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during file uploading" + 
                    System.lineSeparator() +
                    System.lineSeparator() + ex.getLocalizedMessage()));
        }
    }
    
    public StreamedContent downloadSystemAttachment(SystemAttachment attachment) {
        String path = DocumentAndImageUtils.DOCUMENT_PATH + "/" + attachment.getAttachmentFilename();
        String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(path);
        try {
            return new DefaultStreamedContent(new FileInputStream(path), contentType, attachment.getName());
        } catch (FileNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during file downloading" + 
                    System.lineSeparator() +
                    System.lineSeparator() + ex.getLocalizedMessage()));
        }
        return null;
    }
    
    public List<Plant> completePlant(String value) {
        return customerSupplierService.listPlants(0, 10, "address", Boolean.TRUE, jobOrder.getSiteSurveyReport().getRequest().getCustomer(), value);
    }
    
    public List<Plant> getPlants() {
        if (plants == null || plants.isEmpty())
            plants = customerSupplierService.listPlants(0, 0, null, null, jobOrder.getSiteSurveyReport().getRequest().getCustomer(), null);
        return plants;
    }
    
    public JobOrder getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(JobOrder jobOrder) {
        this.jobOrder = jobOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SiteSurveyReport getSelectedReport() {
        return selectedReport;
    }

    public void setSelectedReport(SiteSurveyReport selectedReport) {
        this.selectedReport = selectedReport;
    }
    
}