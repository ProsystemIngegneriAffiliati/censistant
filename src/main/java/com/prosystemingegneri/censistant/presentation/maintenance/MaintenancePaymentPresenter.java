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

import com.prosystemingegneri.censistant.business.maintenance.boundary.MaintenancePaymentService;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenancePayment;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
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
public class MaintenancePaymentPresenter implements Serializable{
    @Inject
    MaintenancePaymentService service;
    
    private MaintenancePayment maintenancePayment;
    private Long id;
    
    public String saveMaintenancePayment() {
        try {
            service.saveMaintenancePayment(maintenancePayment);
        } catch (EJBException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
            return null;
        }
        
        return "/secured/maintenance/maintenancePayments?faces-redirect=true";
    }
    
    public void detailMaintenancePayment() {
        if (id == 0)
            maintenancePayment = new MaintenancePayment();
        else
            maintenancePayment = service.readMaintenancePayment(id);
    }

    public MaintenancePayment getMaintenancePayment() {
        return maintenancePayment;
    }

    public void setMaintenancePayment(MaintenancePayment maintenancePayment) {
        this.maintenancePayment = maintenancePayment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}