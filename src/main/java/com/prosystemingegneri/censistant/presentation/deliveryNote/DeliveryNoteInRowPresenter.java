/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati
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

import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@ViewScoped
public class DeliveryNoteInRowPresenter implements Serializable {
    private DeliveryNoteRow row;
    private DeliveryNoteIn deliveryNote;
    
    @PostConstruct
    public void init() {
        deliveryNote = (DeliveryNoteIn) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNote");
        row = (DeliveryNoteRow) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("deliveryNoteRow");
        if (row == null)
            row = new DeliveryNoteRow();
    }
    
    public String saveDeliveryNoteInRow() {
        if (row.getDeliveryNote() == null)
            deliveryNote.addRow(row);
        
        return putExternalContextAndReturnToPage();
    }
    
    public String cancel() {
        return putExternalContextAndReturnToPage();
    }
    
    private String putExternalContextAndReturnToPage() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deliveryNote", deliveryNote);
        
        return "/secured/deliveryNote/deliveryNoteIn?faces-redirect=true";
    }

    public DeliveryNoteRow getRow() {
        return row;
    }

    public void setRow(DeliveryNoteRow row) {
        this.row = row;
    }

    public DeliveryNoteIn getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNoteIn deliveryNote) {
        this.deliveryNote = deliveryNote;
    }
    
}