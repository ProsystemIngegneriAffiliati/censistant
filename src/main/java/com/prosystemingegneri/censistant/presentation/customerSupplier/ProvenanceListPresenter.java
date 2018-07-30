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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.boundary.ProvenanceService;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Provenance;
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
public class ProvenanceListPresenter implements Serializable{
    @Inject
    ProvenanceService service;
    
    private List<Provenance> provenances;
    private List<Provenance> selectedProvenances;
    
    @PostConstruct
    public void init() {
        provenances = service.listProvenances(null);
    }
    
    public void deleteProvenances() {
        if (selectedProvenances != null && !selectedProvenances.isEmpty()) {
            for (Provenance provenanceTemp : selectedProvenances) {
                try {
                    service.deleteProvenance(provenanceTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            provenances = service.listProvenances(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Provenance> completeProvenances(String name) {
        return service.listProvenances(name);
    }

    public List<Provenance> getProvenances() {
        return provenances;
    }

    public List<Provenance> getSelectedProvenances() {
        return selectedProvenances;
    }

    public void setSelectedProvenances(List<Provenance> selectedProvenances) {
        this.selectedProvenances = selectedProvenances;
    }
}