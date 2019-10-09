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
package com.prosystemingegneri.censistant.presentation.sales;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.boundary.TimeSpentService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import com.prosystemingegneri.censistant.business.sales.entity.TimeSpent;
import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class TimeSpentPresenter implements Serializable{
    @Inject
    private TimeSpentService service;

    @Inject
    private WorkerService workerService;
    
    private TimeSpent timeSpent;
    private Long id;
    
    @Inject
    private JobOrderService jobOrderService;
    private CustomerSupplier customer;
    private List<JobOrder> jobOrders = new ArrayList<>();
    private JobOrder jobOrder;
    
    private void saveTimeSpent() {
        timeSpent = service.save(timeSpent);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successo", "Salvato con successo"));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }
    
    public String save() {
        saveTimeSpent();
        if (id == 0L)
            id = timeSpent.getId();
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&includeViewParams=true";
    }
    
    public String saveAndReset() {
        saveTimeSpent();
        id = 0L;
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true&includeViewParams=true";
    }
    
    public void detail() {
        if (id != null) {
            if (id == 0)
                timeSpent = service.create();
            else {
                timeSpent = service.find(id);
                customer = timeSpent.getJobOrder().getOffer().getSiteSurveyReport().getPlant().getCustomerSupplier();
            }
            
            if (!FacesContext.getCurrentInstance().getExternalContext().isUserInRole("admin") && workerService.getLoggedWorker().isPresent())
                timeSpent.setWorker(workerService.getLoggedWorker().get());
        }
    }
    
    public List<JobOrder> completeJobOrder(String descrizione) {
        jobOrders = jobOrderService.listJobOrders(0, 25, null, null, null, null, customer, descrizione, null);
        return jobOrders;
    }
    
    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultJobOrder Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<JobOrder> getJobOrders(JobOrder defaultJobOrder) {
        if (jobOrders.isEmpty())
            jobOrders.add(defaultJobOrder);
        return jobOrders;
    }

    public TimeSpent getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(TimeSpent timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerSupplier getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSupplier customer) {
        this.customer = customer;
    }

    public JobOrder getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(JobOrder jobOrder) {
        this.jobOrder = jobOrder;
    }
    
}