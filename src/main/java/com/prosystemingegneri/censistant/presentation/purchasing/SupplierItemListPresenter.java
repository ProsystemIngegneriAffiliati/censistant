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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.purchasing.boundary.SupplierItemService;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
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
public class SupplierItemListPresenter implements Serializable{
    @Inject
    SupplierItemService service;
    
    private SupplierItemLazyDataModel lazySupplierItems;
    private List<SupplierItem> selectedSupplierItems;
    
    private List<SupplierItem> supplierItems;
    
    @PostConstruct
    public void init() {
        lazySupplierItems = new SupplierItemLazyDataModel(service);
    }
    
    public void deleteSupplierItem() {
        if (selectedSupplierItems != null && !selectedSupplierItems.isEmpty()) {
            for (SupplierItem siteSurveyReportTemp : selectedSupplierItems) {
                try {
                    service.deleteSupplierItem(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<SupplierItem> completeSupplierItems(String value) {
        return service.listSupplierItemsAllField(0, 10, value);
    }

    public List<SupplierItem> getSupplierItems() {
        if (supplierItems == null || supplierItems.isEmpty())
            supplierItems = service.listSupplierItemsAllField(0, 0, null);
        return supplierItems;
    }

    public SupplierItemLazyDataModel getLazySupplierItems() {
        return lazySupplierItems;
    }

    public void setLazySupplierItems(SupplierItemLazyDataModel lazySupplierItems) {
        this.lazySupplierItems = lazySupplierItems;
    }

    public List<SupplierItem> getSelectedSupplierItems() {
        return selectedSupplierItems;
    }

    public void setSelectedSupplierItems(List<SupplierItem> selectedSupplierItem) {
        this.selectedSupplierItems = selectedSupplierItem;
    }
}
