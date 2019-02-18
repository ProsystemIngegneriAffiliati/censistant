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

import com.prosystemingegneri.censistant.business.sales.boundary.JobOrderService;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
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
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class JobOrderListPresenter implements Serializable{
    @Inject
    JobOrderService service;
    
    private JobOrderLazyDataModel lazyJobOrders;
    private List<JobOrder> selectedJobOrders;
    
    @PostConstruct
    public void init() {
        lazyJobOrders = new JobOrderLazyDataModel(service);
    }
    
    public void deleteJobOrder() {
        if (selectedJobOrders != null && !selectedJobOrders.isEmpty()) {
            for (JobOrder siteSurveyReportTemp : selectedJobOrders) {
                try {
                    service.deleteJobOrder(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public JobOrderLazyDataModel getLazyJobOrders() {
        return lazyJobOrders;
    }

    public void setLazyJobOrders(JobOrderLazyDataModel lazyJobOrders) {
        this.lazyJobOrders = lazyJobOrders;
    }

    public List<JobOrder> getSelectedJobOrders() {
        return selectedJobOrders;
    }

    public void setSelectedJobOrders(List<JobOrder> selectedJobOrder) {
        this.selectedJobOrders = selectedJobOrder;
    }
}
