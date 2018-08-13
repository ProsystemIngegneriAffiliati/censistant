/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati
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
import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.SystemAttachment;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.boundary.OfferService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
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
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.apache.commons.io.FilenameUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class OfferPresenter implements Serializable{
    @Inject
    OfferService service;
    @Inject
    CustomerSupplierService customerSupplierService;
    @Inject
    SiteSurveyReportService siteSurveyReportService;
    @Inject
    JobOrderService jobOrderService;
    
    private Offer offer;
    private Long id;
    
    private SiteSurveyReport dummyReport;
    
    private List<Plant> plants;
    
    private System system;
    
    private Integer activeIndex;    //useful for keep tab opened when reloading a page
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        offer = (Offer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("offer");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
        
        if (activeIndex == null)
            activeIndex = 0;
        
        if (offer != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                offer.getSiteSurveyReport().getRequest().setCustomer(customer);
                if (!customer.getPlants().isEmpty()) {
                    Plant mostRecentPlant = customer.getPlants().get(0);
                    for (Plant plant : customer.getPlants())
                        if (plant.getId() != null && plant.getId() > mostRecentPlant.getId())
                            mostRecentPlant = plant;
                    
                    offer.getSiteSurveyReport().setPlant(mostRecentPlant);
                }
            }
            system = offer.getSystem();
        }
    }
    
    private int validateAndSaveOffer() {
        boolean isValidated = true;
        for (ConstraintViolation<Offer> constraintViolation : validator.validate(offer, Default.class)) {
            isValidated = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
        }
        if (!isValidated)
            return -1;
        
        try {
            service.saveOffer(offer);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return -1;
        }
        
        return 0;
    }
    
    public String saveOffer() {
        if (validateAndSaveOffer() == 0)
            return "/secured/sales/offers?faces-redirect=true";
        else
            return null;
    }
    
    public void detailOffer() {
        if (offer == null && id != null) {
            if (id == 0)
                offer = service.createNewOffer();
            else
                offer = service.readOffer(id);
        }
    }
    
    public void onSiteSurveyReportSelect(SelectEvent event) {
        offer.addSiteSurveyReport((SiteSurveyReport) event.getObject());
    }
    
    public void onRequestCustomerSelect(SelectEvent event) {
        offer.getSiteSurveyReport().setPlant(null);
    }

    public void onSystemSelect(SelectEvent event) {
        if (event.getObject() != null) {
            System oldSystem = offer.getSystem();
            oldSystem.removeOffer(offer);
            
            System selectedSystem = (System) event.getObject();
            selectedSystem.addOffer(offer);
        }
    }
    
    public String createCustomer() {
        return prepareToOpenCustomer(customerSupplierService.createPotentialCustomer());
    }
    
    public String openCustomer() {
        return prepareToOpenCustomer(offer.getSiteSurveyReport().getRequest().getCustomer());
    }
    
    private String prepareToOpenCustomer(CustomerSupplier customer) {
        setExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.TRUE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "sales/offer");
        
        return "/secured/customerSupplier/customer?faces-redirect=true";
    }
    
    public void handleSystemAttachmentUpload(FileUploadEvent event) {
        String idMessageWidget = "addSystemAttachmentMessage";
        try {
            UploadedFile uploadedFile = event.getFile();
            InputStream input = uploadedFile.getInputstream();
            
            Path folder = Paths.get(DocumentAndImageUtils.DOCUMENT_PATH);
            String filename = FilenameUtils.getBaseName(uploadedFile.getFileName()); 
            String extension = FilenameUtils.getExtension(uploadedFile.getFileName());
            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
         
            FacesContext.getCurrentInstance().addMessage(idMessageWidget, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "File uploaed successfully"));
            
            SystemAttachment attachment = new SystemAttachment();
            attachment.setName(filename);
            attachment.setAttachmentFilename(FilenameUtils.getName(file.toString()));
            offer.getSystem().addSystemAttachment(attachment);
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(idMessageWidget, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during file uploading" + 
                    java.lang.System.lineSeparator() +
                    java.lang.System.lineSeparator() + ex.getLocalizedMessage()));
        }
    }
    
    public StreamedContent downloadSystemAttachment(SystemAttachment attachment) {
        String path = DocumentAndImageUtils.DOCUMENT_PATH + "/" + attachment.getAttachmentFilename();
        String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(path);
        try {
            return new DefaultStreamedContent(new FileInputStream(path), contentType, attachment.getName());
        } catch (FileNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during file downloading" + 
                    java.lang.System.lineSeparator() +
                    java.lang.System.lineSeparator() + ex.getLocalizedMessage()));
        }
        return null;
    }
    
    public String createJobOrder() {
        if (offer.getJobOrder() == null) {
            if (validateAndSaveOffer() == 0) {
                JobOrder newJobOrder = jobOrderService.createNewJobOrder(offer);

                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", newJobOrder);

                return "/secured/sales/jobOrder?faces-redirect=true";
            }
        }
        
        return null;
    }
    
    public List<Plant> completePlant(String value) {
        return customerSupplierService.listPlants(0, 10, "address", Boolean.TRUE, offer.getSiteSurveyReport().getRequest().getCustomer(), value, null);
    }
    
    public List<Plant> getPlants() {
        if (plants == null || plants.isEmpty())
            plants = customerSupplierService.listPlants(0, 0, null, null, offer.getSiteSurveyReport().getRequest().getCustomer(), null, null);
        return plants;
    }
    
    private void setExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("offer", offer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
    }
    
    public void createNewDevice() {
        offer.getSystem().addDevice(new Device());
    }
    
    public void duplicateDevice(Device device) {
        offer.getSystem().addDevice(device.duplicate());
    }
    
    public void deleteDevice(Device device) {
        if (device != null)
            offer.getSystem().removeDevice(device);
    }
    
    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }

    public SiteSurveyReport getDummyReport() {
        return dummyReport;
    }

    public void setDummyReport(SiteSurveyReport dummyReport) {
        this.dummyReport = dummyReport;
    }
    
}