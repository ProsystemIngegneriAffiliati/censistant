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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.warehouse.boundary.WarehouseService;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse;
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
public class WarehouseListPresenter implements Serializable{
    @Inject
    WarehouseService service;
    
    private List<Warehouse> warehouses;
    private List<Warehouse> selectedWarehouses;
    
    @PostConstruct
    public void init() {
        warehouses = service.listWarehouses(0, 0, null, Boolean.FALSE, null);
    }
    
    public void deleteWarehouses() {
        if (selectedWarehouses != null && !selectedWarehouses.isEmpty()) {
            for (Warehouse warehouseTemp : selectedWarehouses) {
                try {
                    service.deleteWarehouse(warehouseTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            warehouses = service.listWarehouses(0, 0, null, Boolean.FALSE, null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Warehouse> completeWarehouses(String name) {
        return service.listWarehouses(0, 0, null, Boolean.FALSE, name);
    }

    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public List<Warehouse> getSelectedWarehouses() {
        return selectedWarehouses;
    }

    public void setSelectedWarehouses(List<Warehouse> selectedWarehouses) {
        this.selectedWarehouses = selectedWarehouses;
    }
}