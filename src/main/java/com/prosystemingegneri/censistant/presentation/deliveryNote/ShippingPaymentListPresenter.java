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

import com.prosystemingegneri.censistant.business.deliveryNote.boundary.ShippingPaymentService;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment;
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
public class ShippingPaymentListPresenter implements Serializable{
    @Inject
    ShippingPaymentService service;
    
    private List<ShippingPayment> shippingPayments;
    private List<ShippingPayment> selectedShippingPayments;
    
    @PostConstruct
    public void init() {
        shippingPayments = service.listShippingPayments(0, 0, null, Boolean.FALSE, null);
    }
    
    public void deleteShippingPayments() {
        if (selectedShippingPayments != null && !selectedShippingPayments.isEmpty()) {
            for (ShippingPayment shippingPaymentTemp : selectedShippingPayments) {
                try {
                    service.deleteShippingPayment(shippingPaymentTemp.getId());
                } catch (EJBException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                }
            }
            shippingPayments = service.listShippingPayments(0, 0, null, Boolean.FALSE, null);
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a row before deleting"));
    }
    
    public List<ShippingPayment> completeShippingPayments(String name) {
        return service.listShippingPayments(0, 0, null, Boolean.FALSE, name);
    }

    public List<ShippingPayment> getShippingPayments() {
        return shippingPayments;
    }

    public List<ShippingPayment> getSelectedShippingPayments() {
        return selectedShippingPayments;
    }

    public void setSelectedShippingPayments(List<ShippingPayment> selectedShippingPayments) {
        this.selectedShippingPayments = selectedShippingPayments;
    }
}