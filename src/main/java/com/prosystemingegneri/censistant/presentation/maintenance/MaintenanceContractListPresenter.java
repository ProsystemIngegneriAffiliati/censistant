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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenanceContractService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceContract;
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

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceContractListPresenter implements Serializable{
    @Inject
    MaintenanceContractService service;
    
    private MaintenanceContractLazyDataModel lazyMaintenanceContracts;
    private List<MaintenanceContract> selectedMaintenanceContracts;
    
    @PostConstruct
    public void init() {
        lazyMaintenanceContracts = new MaintenanceContractLazyDataModel(service);
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

    public MaintenanceContractLazyDataModel getLazyMaintenanceContracts() {
        return lazyMaintenanceContracts;
    }

    public void setLazyMaintenanceContracts(MaintenanceContractLazyDataModel lazyMaintenanceContracts) {
        this.lazyMaintenanceContracts = lazyMaintenanceContracts;
    }

    public List<MaintenanceContract> getSelectedMaintenanceContracts() {
        return selectedMaintenanceContracts;
    }

    public void setSelectedMaintenanceContracts(List<MaintenanceContract> selectedMaintenanceContract) {
        this.selectedMaintenanceContracts = selectedMaintenanceContract;
    }
}
