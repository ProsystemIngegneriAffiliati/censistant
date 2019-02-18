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
package com.prosystemingegneri.censistant.presentation.siteSurvey;

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.WorkerService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
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
public class WorkerListPresenter implements Serializable{
    @Inject
    WorkerService service;
    
    private List<Worker> workers;
    private List<Worker> selectedWorkers;
    
    @PostConstruct
    public void init() {
        workers = service.listWorkers(null);
    }
    
    public void deleteWorkers() {
        if (selectedWorkers != null && !selectedWorkers.isEmpty()) {
            for (Worker workerTemp : selectedWorkers) {
                try {
                    service.deleteWorker(workerTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            workers = service.listWorkers(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Worker> completeWorkers(String name) {
        return service.listWorkers(name);
    }

    public List<Worker> getWorkers() {
        return workers;
    }

    public List<Worker> getSelectedWorkers() {
        return selectedWorkers;
    }

    public void setSelectedWorkers(List<Worker> selectedWorkers) {
        this.selectedWorkers = selectedWorkers;
    }
}