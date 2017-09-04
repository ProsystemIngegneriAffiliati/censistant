/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyRequestService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyRequest;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.Date;
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
public class SiteSurveyRequestListPresenter implements Serializable{
    @Inject
    SiteSurveyRequestService service;
    
    private SiteSurveyRequestLazyDataModel lazySiteSurveyRequests;
    private List<SiteSurveyRequest> selectedSiteSurveyRequests;
    
    @PostConstruct
    public void init() {
        lazySiteSurveyRequests = new SiteSurveyRequestLazyDataModel(service);
    }
    
    public void deleteSiteSurveyRequest() {
        if (selectedSiteSurveyRequests != null && !selectedSiteSurveyRequests.isEmpty()) {
            for (SiteSurveyRequest siteSurveyRequestTemp : selectedSiteSurveyRequests) {
                try {
                    service.deleteSiteSurveyRequest(siteSurveyRequestTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }

    public SiteSurveyRequestLazyDataModel getLazySiteSurveyRequests() {
        return lazySiteSurveyRequests;
    }

    public void setLazySiteSurveyRequests(SiteSurveyRequestLazyDataModel lazySiteSurveyRequests) {
        this.lazySiteSurveyRequests = lazySiteSurveyRequests;
    }

    public List<SiteSurveyRequest> getSelectedSiteSurveyRequests() {
        return selectedSiteSurveyRequests;
    }

    public void setSelectedSiteSurveyRequests(List<SiteSurveyRequest> selectedSiteSurveyRequest) {
        this.selectedSiteSurveyRequests = selectedSiteSurveyRequest;
    }
    
    public Date getStart() {
        return lazySiteSurveyRequests.getStart();
    }

    public void setStart(Date start) {
        this.lazySiteSurveyRequests.setStart(start);
    }

    public Date getEnd() {
        return lazySiteSurveyRequests.getEnd();
    }

    public void setEnd(Date end) {
        this.lazySiteSurveyRequests.setEnd(end);
    }
    
}
