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
package com.prosystemingegneri.censistant.presentation.customerSupplier;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Referee;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class RefereePresenter implements Serializable {
    private Referee referee;
    private CustomerSupplier customerSupplier;
    private Boolean isCustomerView;
    
    @PostConstruct
    public void init() {
        customerSupplier = (CustomerSupplier) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("customerSupplier");
        isCustomerView = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("isCustomerView");
        referee = (Referee) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("referee");
        if (referee == null)
            referee = new Referee();
    }
    
    public String saveReferee() {
        if (referee.getCustomerSupplier() == null)
            customerSupplier.addReferee(referee);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        
        return chooseReturnString();
    }
    
    public String cancel() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerSupplier", customerSupplier);
        
        return chooseReturnString();
    }
    
    private String chooseReturnString() {
        String returnString;
        if (isCustomerView)
            returnString = "/secured/customerSupplier/customers";
        else
            returnString = "/secured/customerSupplier/suppliers";
        
        return returnString + "?faces-redirect=true";
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }   
}