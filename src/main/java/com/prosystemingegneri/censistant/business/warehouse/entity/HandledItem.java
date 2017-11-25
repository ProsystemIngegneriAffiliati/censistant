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
package com.prosystemingegneri.censistant.business.warehouse.entity;

import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class HandledItem extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date handlingTimestamp;
    
    @NotNull
    @ManyToOne(optional = false)
    private Worker worker;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer quantity;
    
    //unit measure will always be item's one
    //private UnitMeasure unitMeasure;
    
    @NotNull
    @ManyToOne(optional = false)
    private PurchaseOrderRow purchaseOrderRow;
    
    @NotNull
    @ManyToOne(optional = false)
    private Location fromLocation;
    
    @NotNull
    @ManyToOne(optional = false)
    private Location toLocation;
    
    @OneToOne(mappedBy = "handledItem")
    private DeliveryNoteRow deliveryNoteRow;
    
    private String notes;
    
    @Version
    private int version;

    public HandledItem() {
        handlingTimestamp = new Date();
        quantity = 1;
    }

    public Date getHandlingTimestamp() {
        return handlingTimestamp;
    }

    public void setHandlingTimestamp(Date handlingTimestamp) {
        this.handlingTimestamp = handlingTimestamp;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public PurchaseOrderRow getPurchaseOrderRow() {
        return purchaseOrderRow;
    }

    public void setPurchaseOrderRow(PurchaseOrderRow purchaseOrderRow) {
        this.purchaseOrderRow = purchaseOrderRow;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
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

    public DeliveryNoteRow getDeliveryNoteRow() {
        return deliveryNoteRow;
    }

    public void setDeliveryNoteRow(DeliveryNoteRow deliveryNoteRow) {
        this.deliveryNoteRow = deliveryNoteRow;
    }
    
}
