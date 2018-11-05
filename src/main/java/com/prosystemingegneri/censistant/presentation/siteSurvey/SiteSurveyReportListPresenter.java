/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.siteSurvey.boundary.SiteSurveyReportService;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
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
public class SiteSurveyReportListPresenter implements Serializable{
    @Inject
    SiteSurveyReportService service;
    
    private SiteSurveyReportLazyDataModel lazySiteSurveyReports;
    private List<SiteSurveyReport> selectedSiteSurveyReports;
    
    private List<SiteSurveyReport> reportsNotAssociatedToOffer;
    
    @PostConstruct
    public void init() {
        lazySiteSurveyReports = new SiteSurveyReportLazyDataModel(service);
    }
    
    public void deleteSiteSurveyReport() {
        if (selectedSiteSurveyReports != null && !selectedSiteSurveyReports.isEmpty()) {
            for (SiteSurveyReport siteSurveyReportTemp : selectedSiteSurveyReports) {
                try {
                    service.deleteSiteSurveyReport(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<SiteSurveyReport> completeReportsNotAssociatedToOffer(String name) {
        return service.listSiteSurveyReports(0, 10, "expected", Boolean.FALSE, null, null, null, name, null, null, null, null, Boolean.FALSE, null);
    }

    public List<SiteSurveyReport> getReportsNotAssociatedToOffer() {
        if (reportsNotAssociatedToOffer == null || reportsNotAssociatedToOffer.isEmpty())
            reportsNotAssociatedToOffer = service.listSiteSurveyReports(0, 0, null, Boolean.FALSE, null, null, null, null, null, null, null, null, Boolean.FALSE, null);
        return reportsNotAssociatedToOffer;
    }

    public SiteSurveyReportLazyDataModel getLazySiteSurveyReports() {
        return lazySiteSurveyReports;
    }

    public void setLazySiteSurveyReports(SiteSurveyReportLazyDataModel lazySiteSurveyReports) {
        this.lazySiteSurveyReports = lazySiteSurveyReports;
    }

    public List<SiteSurveyReport> getSelectedSiteSurveyReports() {
        return selectedSiteSurveyReports;
    }

    public void setSelectedSiteSurveyReports(List<SiteSurveyReport> selectedSiteSurveyReport) {
        this.selectedSiteSurveyReports = selectedSiteSurveyReport;
    }
    
    public Date getStart() {
        return lazySiteSurveyReports.getStart();
    }

    public void setStart(Date start) {
        this.lazySiteSurveyReports.setStart(start);
    }

    public Date getEnd() {
        return lazySiteSurveyReports.getEnd();
    }

    public void setEnd(Date end) {
        this.lazySiteSurveyReports.setEnd(end);
    }
    
}
