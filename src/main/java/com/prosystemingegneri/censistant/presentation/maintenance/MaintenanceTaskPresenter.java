/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.maintenance.entity.InspectionDone;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.Replacement;
import com.prosystemingegneri.censistant.business.maintenance.entity.WorkingDuration;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;


/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceTaskPresenter implements Serializable{
    @Inject
    MaintenanceTaskService service;
    
    private MaintenanceTask maintenanceTask;
    private Long id;
    
    @Inject
    HandledItemService handledItemService;
    private HandledItemLazyDataModel lazyHandledItems;
    
    @Resource
    Validator validator;
    
    public String saveMaintenanceTask() {
        try {
            boolean isValidated = true;
            for (ConstraintViolation<MaintenanceTask> constraintViolation : validator.validate(maintenanceTask, Default.class)) {
                isValidated = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
            }
            if (!isValidated)
                return null;

            service.saveMaintenanceTask(maintenanceTask);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/maintenance/maintenanceTasks?faces-redirect=true";
    }
    
    public void detailMaintenanceTask() {
        if (maintenanceTask == null && id != null) {
            if (id == 0)
                maintenanceTask = service.createNewMaintenanceTask(null);
            else
                maintenanceTask = service.readMaintenanceTask(id);
        }
    }
    
    public void clearCustomerSignature() {
        maintenanceTask.setCustomerSignature(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }
    
    public String calculateInspectionDoneResultColor(InspectionDone inspectionDone) {
        switch (inspectionDone.getInspectionResult()) {
            case OK:
                return "green-row";
            case KO:
                return "red-row";
            default:
                return "";
        }
    }
    
    public void onNormalWorkingPanelToggle(ToggleEvent event) {
        if (event.getVisibility().equals(Visibility.VISIBLE))
            maintenanceTask.getTaskPrice().setNormalWorking(new WorkingDuration());
    }
    
    public void onOvertimeWorkingPanelToggle(ToggleEvent event) {
        if (event.getVisibility().equals(Visibility.VISIBLE))
            maintenanceTask.getTaskPrice().setOvertimeWorking(new WorkingDuration());
    }
    
    public void onTravelWorkingPanelToggle(ToggleEvent event) {
        if (event.getVisibility().equals(Visibility.VISIBLE))
            maintenanceTask.getTaskPrice().setTravelWorking(new WorkingDuration());
    }

    public HandledItemLazyDataModel getLazyHandledItems() {
        return lazyHandledItems;
    }

    public void setLazyHandledItems(HandledItemLazyDataModel lazyHandledItems) {
        this.lazyHandledItems = lazyHandledItems;
    }
    
    public void onSystemSelect(SelectEvent event) {
        System system = (System) event.getObject();
        if (system != null)
            lazyHandledItems = new HandledItemLazyDataModel(handledItemService, null, system, null, Boolean.FALSE);
    }
    
    public void onHandledItemSelect(SelectEvent event) {
        HandledItem handledItem = (HandledItem) event.getObject();
        if (!maintenanceTask.isHandledItemPresent(handledItem))
            maintenanceTask.addReplacement(new Replacement(handledItem));
    }
    
    public StreamedContent print() {
        try {
            List<MaintenanceTask> tempBean = new ArrayList<>();
            tempBean.add(maintenanceTask);
            Map<String, Object> params = new HashMap<>();
            params.put("ReportTitle", "Rapporto di intervento");
            params.put("subReportPath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/maintenance/") + "/");
            //params.put("reportImagePath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/images/") + "/");
            
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/maintenance/maintenanceTask.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, new JRBeanCollectionDataSource(tempBean));
            
            return new ByteArrayContent(JasperExportManager.exportReportToPdf(jasperPrint), "application/pdf", maintenanceTask.getNumber() + ".pdf");
            
        } catch (JRException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during printing" + 
                    java.lang.System.lineSeparator() +
                    java.lang.System.lineSeparator() + ex.getLocalizedMessage()));
        }
        
        return null;
    }
}