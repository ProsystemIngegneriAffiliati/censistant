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

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@DiscriminatorValue(value = "1")
public class DeliveryNoteOut extends DeliveryNoteCommon {
    
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer packagesNumber;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Column(nullable = false)    
    private BigDecimal weight;
    
    @NotNull
    @ManyToOne(optional = false)
    private Plant plant;
    
    @NotNull
    @ManyToOne(optional = false)
    private GoodsDescription goodsDescription;

    @NotNull
    @ManyToOne(optional = false)
    private ShipmentReason shipmentReason;

    @NotNull
    @ManyToOne(optional = false)
    private ShippingPayment shippingPayment;
    
    @ManyToOne
    private Carrier carrier;

    public DeliveryNoteOut() {
        packagesNumber = 1;
    }
    
    public DeliveryNoteOut(Integer number) {
        super(number);
        packagesNumber = 1;
    }

    public Integer getPackagesNumber() {
        return packagesNumber;
    }

    public void setPackagesNumber(Integer packagesNumber) {
        this.packagesNumber = packagesNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public GoodsDescription getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(GoodsDescription goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public ShipmentReason getShipmentReason() {
        return shipmentReason;
    }

    public void setShipmentReason(ShipmentReason shipmentReason) {
        this.shipmentReason = shipmentReason;
    }

    public ShippingPayment getShippingPayment() {
        return shippingPayment;
    }

    public void setShippingPayment(ShippingPayment shippingPayment) {
        this.shippingPayment = shippingPayment;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @Override
    public String getNumberAndCreation() {
        return new StringBuilder(super.getNumber())
                .append(" - ")
                .append(new SimpleDateFormat("dd/MM/yyyy").format(super.getCreation()))
                .toString();
    }

}
