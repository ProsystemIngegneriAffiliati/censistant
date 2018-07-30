/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.sales.boundary.OfferService;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
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
public class OfferListPresenter implements Serializable{
    @Inject
    OfferService service;
    
    private OfferLazyDataModel lazyOffers;
    private List<Offer> selectedOffers;
    
    private List<Offer> offersNotAssociatedToJobOrder;
    
    @PostConstruct
    public void init() {
        lazyOffers = new OfferLazyDataModel(service);
    }
    
    public void deleteOffer() {
        if (selectedOffers != null && !selectedOffers.isEmpty()) {
            for (Offer siteSurveyReportTemp : selectedOffers) {
                try {
                    service.deleteOffer(siteSurveyReportTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<Offer> completeOffersNotAssociatedToJobOrder(String name) {
        return service.listOffers(0, 10, null, Boolean.FALSE, null, name, null, null, Boolean.FALSE);
    }

    public OfferLazyDataModel getLazyOffers() {
        return lazyOffers;
    }

    public void setLazyOffers(OfferLazyDataModel lazyOffers) {
        this.lazyOffers = lazyOffers;
    }

    public List<Offer> getSelectedOffers() {
        return selectedOffers;
    }

    public void setSelectedOffers(List<Offer> selectedOffer) {
        this.selectedOffers = selectedOffer;
    }

    public List<Offer> getOffersNotAssociatedToJobOrder() {
        if (offersNotAssociatedToJobOrder == null || offersNotAssociatedToJobOrder.isEmpty())
            offersNotAssociatedToJobOrder = service.listOffers(0, 0, null, Boolean.FALSE, null, null, null, null, Boolean.FALSE);
        return offersNotAssociatedToJobOrder;
    }
    
}
