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
package com.prosystemingegneri.censistant.presentation.deliveryNote;

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.GoodsDescriptionService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription;
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
public class GoodsDescriptionListPresenter implements Serializable{
    @Inject
    GoodsDescriptionService service;
    
    private List<GoodsDescription> goodsDescriptions;
    private List<GoodsDescription> selectedGoodsDescriptions;
    
    @PostConstruct
    public void init() {
        goodsDescriptions = service.listGoodsDescriptions(0, 0, null, Boolean.FALSE, null);
    }
    
    public void deleteGoodsDescriptions() {
        if (selectedGoodsDescriptions != null && !selectedGoodsDescriptions.isEmpty()) {
            for (GoodsDescription goodsDescriptionTemp : selectedGoodsDescriptions) {
                try {
                    service.deleteGoodsDescription(goodsDescriptionTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            goodsDescriptions = service.listGoodsDescriptions(0, 0, null, Boolean.FALSE, null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<GoodsDescription> completeGoodsDescriptions(String name) {
        return service.listGoodsDescriptions(0, 0, null, Boolean.FALSE, name);
    }

    public List<GoodsDescription> getGoodsDescriptions() {
        return goodsDescriptions;
    }

    public List<GoodsDescription> getSelectedGoodsDescriptions() {
        return selectedGoodsDescriptions;
    }

    public void setSelectedGoodsDescriptions(List<GoodsDescription> selectedGoodsDescriptions) {
        this.selectedGoodsDescriptions = selectedGoodsDescriptions;
    }
}