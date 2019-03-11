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

import java.io.Serializable;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceContractOLDListPresenter implements Serializable{
    /*@Inject
    MaintenanceContractOLDService service;
    
    private MaintenanceContractOLDLazyDataModel lazyMaintenanceContracts;
    private List<MaintenanceContract> selectedMaintenanceContracts;
    
    private List<MaintenanceContract> unexpiredMaintenanceContracts;
    
    @PostConstruct
    public void init() {
        lazyMaintenanceContracts = new MaintenanceContractOLDLazyDataModel(service);
    }
    
    public void deleteMaintenanceContract() {
        if (selectedMaintenanceContracts != null && !selectedMaintenanceContracts.isEmpty()) {
            for (MaintenanceContract siteSurveyReportTemp : selectedMaintenanceContracts) {
                try {
                    service.deleteMaintenanceContract(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public boolean isMaintenanceContractCompleted(Long idMaintenanceContract) {
        return service.isMaintenanceContractCompleted(idMaintenanceContract);
    }

    public MaintenanceContractOLDLazyDataModel getLazyMaintenanceContracts() {
        return lazyMaintenanceContracts;
    }

    public void setLazyMaintenanceContracts(MaintenanceContractOLDLazyDataModel lazyMaintenanceContracts) {
        this.lazyMaintenanceContracts = lazyMaintenanceContracts;
    }

    public List<MaintenanceContract> getSelectedMaintenanceContracts() {
        return selectedMaintenanceContracts;
    }

    public void setSelectedMaintenanceContracts(List<MaintenanceContract> selectedMaintenanceContract) {
        this.selectedMaintenanceContracts = selectedMaintenanceContract;
    }
    
    public List<MaintenanceContract> completeUnexpiredMaintenanceContract(String value) {
        return service.listMaintenanceContracts(0, 0, null, null, null, null, value, Boolean.FALSE, null);
    }

    public List<MaintenanceContract> getUnexpiredMaintenanceContracts() {
        if (unexpiredMaintenanceContracts == null || unexpiredMaintenanceContracts.isEmpty())
            unexpiredMaintenanceContracts = service.listMaintenanceContracts(0, 0, null, null, null, null, null, Boolean.FALSE, null);
        return unexpiredMaintenanceContracts;
    }*/
}
