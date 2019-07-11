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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.production.entity.Area;
import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.SystemAttachment;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.boundary.OfferService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.warehouse.boundary.StockService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FilenameUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.ByteArrayContent;
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
    @Inject
    OfferService offerService;
    @Inject
    StockService stockService;
    
    private JobOrder jobOrder;
    private Long id;
    
    private Offer dummyOffer;
    private SiteSurveyReport dummyReport;
    
    private List<CustomerSupplier> customers;
    
    private List<Plant> plants;
    
    private System system;
    
    private Integer activeIndex;    //useful for keep tab opened when reloading a page
    
    @Resource
    Validator validator;
    
    @PostConstruct
    public void init() {
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
        
        if (activeIndex == null)
            activeIndex = 0;
        
        if (jobOrder != null) {
            Long idCustomer = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idCustomer");
            if (idCustomer != null && idCustomer > 0) {
                CustomerSupplier customer = customerSupplierService.readCustomerSupplier(idCustomer);
                jobOrder.getOffer().getSiteSurveyReport().getRequest().setCustomer(customer);
                if (!customer.getPlants().isEmpty()) {
                    Plant mostRecentPlant = customer.getPlants().get(0);
                    for (Plant plant : customer.getPlants())
                        if (plant.getId() != null && plant.getId() > mostRecentPlant.getId())
                            mostRecentPlant = plant;
                    
                    jobOrder.getOffer().getSiteSurveyReport().setPlant(mostRecentPlant);
                }
            }
            system = jobOrder.getOffer().getSystem();
        }
    }
    
    private boolean isValid() {
        boolean isValidated = true;
        for (ConstraintViolation<JobOrder> constraintViolation : validator.validate(jobOrder, Default.class)) {
            isValidated = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
        }
        
        return isValidated;
    }
    
    public String saveJobOrder() {
        try {
            if (!isValid())
                return null;
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
                jobOrder = service.createNewJobOrder(null);
            else
                jobOrder = service.readJobOrder(id);
        }
    }
    
    public void onSiteSurveyReportSelect(SelectEvent event) {
        jobOrder.getOffer().addSiteSurveyReport((SiteSurveyReport) event.getObject());
    }
    
    public void onOfferSelect(SelectEvent event) {
        jobOrder.addOffer((Offer) event.getObject());
    }
    
    public void onRequestCustomerSelect(SelectEvent event) {
        jobOrder.getOffer().getSiteSurveyReport().setPlant(null);
    }

    public void onSystemSelect(SelectEvent event) {
        if (event.getObject() != null) {
            System oldSystem = jobOrder.getOffer().getSystem();
            oldSystem.removeOffer(jobOrder.getOffer());
            
            System selectedSystem = (System) event.getObject();
            selectedSystem.addOffer(jobOrder.getOffer());
        }
    }
    
    public String createCustomer() {
        return prepareToOpenCustomer(customerSupplierService.createCustomer());
    }
    
    public String openCustomer() {
        return prepareToOpenCustomer(jobOrder.getOffer().getSiteSurveyReport().getRequest().getCustomer());
    }
    
    private String prepareToOpenCustomer(CustomerSupplier customer) {
        putExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customer);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCustomerView", Boolean.TRUE);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "sales/jobOrder");
        
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
            jobOrder.getOffer().getSystem().addSystemAttachment(attachment);
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
    
    public List<Plant> completePlant(String value) {
        return customerSupplierService.listPlants(0, 10, "address", Boolean.TRUE, jobOrder.getOffer().getSiteSurveyReport().getRequest().getCustomer(), value, null, null);
    }
    
    public List<Plant> getPlants() {
        if (plants == null || plants.isEmpty())
            plants = customerSupplierService.listPlants(0, 0, null, null, jobOrder.getOffer().getSiteSurveyReport().getRequest().getCustomer(), null, null, null);
        return plants;
    }
    
    public void createNewArea() {
        jobOrder.getOffer().getSystem().addArea(new Area());
    }
    
    public void duplicateArea(Area area) {
        jobOrder.getOffer().getSystem().addArea(area.duplicate());
    }
    
    public void deleteArea(Area area) {
        if (area != null)
            jobOrder.getOffer().getSystem().removeArea(area);
    }
    
    public String openItemMovement() {
        putExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("returnPage", "sales/jobOrder");
        return "/secured/warehouse/itemMovement?faces-redirect=true";
    }
    
    public String detailDevice(Device device) {
        if (device != null)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("device", device);
        putExternalContext();
        
        return "/secured/production/device?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
    }
    
    public void deleteDevice(Device device) {
        if (device != null)
            jobOrder.getOffer().getSystem().removeDevice(device);
    }
    
    public Long calculateDevicePlacedQuantity(Device device) {
        if (jobOrder != null && jobOrder.getOffer().getSystem() != null && device != null)
            return stockService.countPlacedQuantity(jobOrder.getOffer().getSystem().getId(), device.getSupplierItem().getItem().getId());
        else
            return 0L;
    }
    
    public List<CustomerSupplier> completeCustomer(String value) {
        customers = customerSupplierService.listCustomerSuppliers(0, 10, "name", Boolean.TRUE, null, null, Boolean.TRUE, null, null, value, null);
        return customers;
    }

    public List<CustomerSupplier> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
            if (jobOrder.getOffer().getSiteSurveyReport().getRequest() != null
                    && jobOrder.getOffer().getSiteSurveyReport() != null
                    && jobOrder.getOffer() != null)
                customers.add(jobOrder.getOffer().getSiteSurveyReport().getRequest().getCustomer());
        }
        return customers;
    }
    
    public StreamedContent print() {
        if (isValid()) {
            try {
                jobOrder = service.saveJobOrder(jobOrder);
                List<JobOrder> tempBean = new ArrayList<>();
                tempBean.add(jobOrder);
                Map<String, Object> params = new HashMap<>();
                params.put("ReportTitle", "Scheda installazione");
                params.put("subReportPath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/jobOrder/") + "/");
                params.put("reportImagePath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/images/") + "/");

                String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/jobOrder/jobOrder.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, new JRBeanCollectionDataSource(tempBean));

                return new ByteArrayContent(JasperExportManager.exportReportToPdf(jasperPrint), "application/pdf", jobOrder.getNumberStr() + ".pdf");
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                    return null;
                } catch (JRException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during printing" + 
                        java.lang.System.lineSeparator() +
                        java.lang.System.lineSeparator() + ex.getLocalizedMessage()));
            }
        }
        
        if (id == 0L)
            id = jobOrder.getId();
        
        return null;
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

    public Offer getDummyOffer() {
        return dummyOffer;
    }

    public void setDummyOffer(Offer dummyOffer) {
        this.dummyOffer = dummyOffer;
    }

    public SiteSurveyReport getDummyReport() {
        return dummyReport;
    }

    public void setDummyReport(SiteSurveyReport dummyReport) {
        this.dummyReport = dummyReport;
    }
    
}