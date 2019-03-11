/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class MaintenanceContractListPresenter implements Serializable {
    @Inject
    private MaintenanceContractService service;
    
    private MaintenanceContractLazyDataModel lazyMaintenanceContracts;
    private List<MaintenanceContract> selectedMaintenanceContracts;
    
    @PostConstruct
    public void init() {
        lazyMaintenanceContracts = new MaintenanceContractLazyDataModel(service);
    }
    
    public void delete() {
        if (selectedMaintenanceContracts != null && !selectedMaintenanceContracts.isEmpty())
            for (MaintenanceContract selectedMaintenanceContract : selectedMaintenanceContracts) {
                try {
                    service.delete(selectedMaintenanceContract.getId());
                } catch (EJBException e) {
                    Messages.create("Error").error().detail(ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()).add();
                }
            }
        else
            Messages.create("Missing selection").warn().detail("Select a row before deleting").add();
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