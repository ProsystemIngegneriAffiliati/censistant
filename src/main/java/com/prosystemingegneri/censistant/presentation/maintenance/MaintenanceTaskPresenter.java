/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.CustomerSupplierService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceTaskService;
import com.prosystemingegneri.censistant.business.maintenance.control.MaintenanceType;
import com.prosystemingegneri.censistant.business.maintenance.entity.ContractedSystem;
import com.prosystemingegneri.censistant.business.maintenance.entity.InspectionDone;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePlan;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.Replacement;
import com.prosystemingegneri.censistant.business.maintenance.entity.WorkingDuration;
import com.prosystemingegneri.censistant.business.production.boundary.SystemService;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.sales.entity.PlaceType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SystemType;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import com.prosystemingegneri.censistant.presentation.warehouse.HandledItemLazyDataModel;
import java.io.Serializable;
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
    
    private List<CustomerSupplier> customers;
    private List<Plant> plants;
    @Inject
    private SystemService systemService;
    private List<System> systemsToBeAdded;
    private System systemToBeAdded;
    
    @Inject
    private MaintenanceContractService maintenanceContractService;
    private List<MaintenanceContract> unexpiredMaintenanceContracts;
    private MaintenanceContract unexpiredMaintenanceContract;
    
    @Resource
    Validator validator;
    
    //userful for creating a new job order and system
    @Inject
    private CustomerSupplierService customerSupplierService;
    private CustomerSupplier customer;
    private CustomerSupplier newCustomer;
    private Plant plant;
    private Plant newPlant;
    private SystemType systemType;
    private PlaceType placeType;
    private Worker seller;
    
    private void init() {
        customers = new ArrayList<>();
        plants = new ArrayList<>();
    }
    
    private boolean isValid() {
        boolean isValidated = true;
        for (ConstraintViolation<MaintenanceTask> constraintViolation : validator.validate(maintenanceTask, Default.class)) {
            isValidated = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", constraintViolation.getMessage()));
        }
        
        return isValidated;
    }
    
    public String saveMaintenanceTask() {
        try {
            if (!isValid())
                return null;

            service.saveMaintenanceTask(maintenanceTask);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/maintenance/maintenanceTasks?faces-redirect=true";
    }
    
    public void detailMaintenanceTask() {
        init();
        if (maintenanceTask == null && id != null) {
            if (id == 0)
                maintenanceTask = service.create();
            else
                maintenanceTask = service.readMaintenanceTask(id);
        }
        
        clearNewCustomer();
        clearNewPlant();
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
        if (system != null) {
            lazyHandledItems = new HandledItemLazyDataModel(handledItemService, null, system, null, Boolean.FALSE);
            updateUnexpiredMaintenanceContracts();
        }
    }
    
    public void onMaintenanceContractSelect(SelectEvent event) {
        MaintenanceContract maintenanceContract = (MaintenanceContract) event.getObject();
        if (maintenanceContract != null) {
            for (ContractedSystem contractedSystem : maintenanceContract.getContractedSystems()) {
                if (contractedSystem.getSystem().equals(systemToBeAdded)) {
                    MaintenancePlan maintenancePlan = null;
                    for (MaintenancePlan maintenancePlan2 : contractedSystem.getMaintenancePlans())
                        if (maintenancePlan2.getMaintenanceType() == MaintenanceType.MAINTENANCE)
                            maintenancePlan = maintenancePlan2;
                    if (maintenancePlan == null) {
                        maintenancePlan = new MaintenancePlan(MaintenanceType.MAINTENANCE);
                        contractedSystem.addMaintenancePlan(maintenancePlan);
                    }
                    maintenanceTask.setSystem(null);
                    maintenanceTask.setMaintenancePlan(maintenancePlan);
                }
            }
        }
    }
    
    public void onHandledItemSelect(SelectEvent event) {
        HandledItem handledItem = (HandledItem) event.getObject();
        if (!maintenanceTask.isHandledItemPresent(handledItem))
            maintenanceTask.addReplacement(new Replacement(handledItem));
    }
    
    public StreamedContent print() {
        if (isValid()) {
            try {
                maintenanceTask = service.saveMaintenanceTask(maintenanceTask);
                List<MaintenanceTask> tempBean = new ArrayList<>();
                tempBean.add(maintenanceTask);
                Map<String, Object> params = new HashMap<>();
                params.put("ReportTitle", "Rapporto di intervento");
                params.put("subReportPath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/maintenance/") + "/");
                params.put("reportImagePath", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/images/") + "/");

                String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/document/maintenance/maintenanceTask.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, new JRBeanCollectionDataSource(tempBean));

                return new ByteArrayContent(JasperExportManager.exportReportToPdf(jasperPrint), "application/pdf", maintenanceTask.getId() + ".pdf");
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
            id = maintenanceTask.getId();
        
        return null;
    }

    public CustomerSupplier getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSupplier customer) {
        this.customer = customer;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public Worker getSeller() {
        return seller;
    }

    public void setSeller(Worker seller) {
        this.seller = seller;
    }
    
    public List<Plant> completePlant(String value) {
        plants = customerSupplierService.listPlants(0, 25, "address", Boolean.TRUE, customer, null, null, value);
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

    public CustomerSupplier getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(CustomerSupplier newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Plant getNewPlant() {
        return newPlant;
    }

    public void setNewPlant(Plant newPlant) {
        this.newPlant = newPlant;
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
        if (customer == null) {
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
            customer = newCustomer;
        }
        customer.addPlant(newPlant);
        plant = newPlant;
        customerSupplierService.saveCustomerSupplier(customer);
    }
    
    public void createNewJobOrder() {
        if (plant == null) {
            FacesContext.getCurrentInstance().addMessage("plant", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (customer == null) {
            FacesContext.getCurrentInstance().addMessage("customer", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (systemType == null) {
            FacesContext.getCurrentInstance().addMessage("systemType", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (placeType == null) {
            FacesContext.getCurrentInstance().addMessage("placeType", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        if (seller == null) {
            FacesContext.getCurrentInstance().addMessage("seller", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Not null"));
            return;
        }
        
        maintenanceTask.setSystem(service.createNewJobOrder(plant, systemType, placeType, seller).getOffer().getSystem());
    }
    
    public void clearNewCustomer() {
        newCustomer = new CustomerSupplier(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
    }
    
    public void clearNewPlant() {
        newPlant = new Plant();
    }
    
    public List<CustomerSupplier> completeCustomer(String value) {
        customers = customerSupplierService.listCustomerSuppliers(0, 25, "name", Boolean.TRUE, null, null, Boolean.TRUE, null, null, value, null);
        return customers;
    }

    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultCustomer Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<CustomerSupplier> getCustomers(CustomerSupplier defaultCustomer) {
        if (customers.isEmpty())
            customers.add(defaultCustomer);
        return customers;
    }

    public System getSystemToBeAdded() {
        return systemToBeAdded;
    }

    public void setSystemToBeAdded(System systemToBeAdded) {
        this.systemToBeAdded = systemToBeAdded;
    }
    
    public void updateSystemsToBeAdded() {
        systemsToBeAdded = systemService.listSystems(0, 0, null, null, null, customer, plant);
    }

    public List<System> getSystemsToBeAdded() {
        return systemsToBeAdded;
    }

    public MaintenanceContract getUnexpiredMaintenanceContract() {
        return unexpiredMaintenanceContract;
    }

    public void setUnexpiredMaintenanceContract(MaintenanceContract unexpiredMaintenanceContract) {
        this.unexpiredMaintenanceContract = unexpiredMaintenanceContract;
    }
    
    public void updateUnexpiredMaintenanceContracts() {
        unexpiredMaintenanceContracts = maintenanceContractService.list(0, 0, null, null, customer, null, plant, systemToBeAdded, Boolean.FALSE);
    }

    public List<MaintenanceContract> getUnexpiredMaintenanceContracts() {
        return unexpiredMaintenanceContracts;
    }

    public void setUnexpiredMaintenanceContracts(List<MaintenanceContract> unexpiredMaintenanceContracts) {
        this.unexpiredMaintenanceContracts = unexpiredMaintenanceContracts;
    }
    
    public String getSystemStr() {
        if (maintenanceTask != null) {
            if (maintenanceTask.getSystem() != null)
                return maintenanceTask.getSystem().getAddress();
            if (maintenanceTask.getMaintenancePlan() != null)
                return maintenanceTask.getMaintenancePlan().getContractedSystem().getSystem().getAddress();
        }
        
        return "";
    }
}