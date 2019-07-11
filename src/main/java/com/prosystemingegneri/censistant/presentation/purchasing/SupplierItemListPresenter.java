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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.purchasing.boundary.SupplierItemService;
import com.prosystemingegneri.censistant.business.purchasing.entity.SupplierItem;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;

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
    
    @Inject
    private SupplierItemImportExportPresenter importExportPresenter;
    
    @PostConstruct
    public void init() {
        lazySupplierItems = new SupplierItemLazyDataModel(service);
        supplierItems = new ArrayList<>();
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
        supplierItems = service.listSupplierItemsAllField(0, 10, value);
        return supplierItems;
    }

    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultSupplierItem Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<SupplierItem> getSupplierItems(SupplierItem defaultSupplierItem) {
        if (supplierItems.isEmpty())
            supplierItems.add(defaultSupplierItem);
        return supplierItems;
    }
    
    public void handleSupplierItemsUpload(FileUploadEvent event) {
        importExportPresenter.handleSupplierItemsUpload(event);
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
