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
package com.prosystemingegneri.censistant.business.deliveryNote.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class DeliveryNoteRow extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(optional = false)
    private DeliveryNoteCommon deliveryNote;
    
    @NotNull
    @OneToOne(optional = false)
    private HandledItem handledItem;
    
    @ManyToOne
    private PurchaseOrderRow purchaseOrderRow;
    
    private String notes;
    
    @Version
    private int version;

    public DeliveryNoteRow() {
    }

    public DeliveryNoteCommon getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNoteCommon deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public HandledItem getHandledItem() {
        return handledItem;
    }

    public void addHandledItem(HandledItem handledItem) {
        handledItem.setDeliveryNoteRow(this);
        this.handledItem = handledItem;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public Long getId() {
        return id;
    }

    public PurchaseOrderRow getPurchaseOrderRow() {
        return purchaseOrderRow;
    }

    public void setPurchaseOrderRow(PurchaseOrderRow purchaseOrderRow) {
        this.purchaseOrderRow = purchaseOrderRow;
    }
    
}
