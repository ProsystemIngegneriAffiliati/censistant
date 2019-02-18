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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.warehouse.boundary.ShelfService;
import com.prosystemingegneri.censistant.business.warehouse.entity.Shelf;
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
public class ShelfListPresenter implements Serializable{
    @Inject
    private ShelfService service;
    
    private List<Shelf> shelfs;
    private List<Shelf> selectedShelfs;
    
    @PostConstruct
    public void init() {
        shelfs = service.listShelfs(null);
    }
    
    public void deleteShelfs() {
        if (selectedShelfs != null && !selectedShelfs.isEmpty()) {
            for (Shelf shelfTemp : selectedShelfs) {
                try {
                    service.deleteShelf(shelfTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            shelfs = service.listShelfs(null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Shelf> completeShelfs(String name) {
        shelfs = service.listShelfs(name);
        return shelfs;
    }
    
    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultShelf Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<Shelf> getShelfs(Shelf defaultShelf) {
        if (shelfs.isEmpty())
            shelfs.add(defaultShelf);
        return shelfs;
    }

    public List<Shelf> getSelectedShelfs() {
        return selectedShelfs;
    }

    public void setSelectedShelfs(List<Shelf> selectedShelfs) {
        this.selectedShelfs = selectedShelfs;
    }
}