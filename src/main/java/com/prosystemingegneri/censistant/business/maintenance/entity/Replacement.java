/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.maintenance.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import static com.prosystemingegneri.censistant.business.maintenance.entity.TaskPrice.PRECISION_PRICE;
import static com.prosystemingegneri.censistant.business.maintenance.entity.TaskPrice.SCALE_PRICE;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class Replacement extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(optional = false)
    private MaintenanceTask maintenanceTask;
    
    @NotNull
    @OneToOne(optional = false)
    private HandledItem handledItem;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_PRICE, precision = PRECISION_PRICE)
    private BigDecimal price;
    
    @Version
    private int version;

    public Replacement() {
        price = BigDecimal.ZERO;
    }

    public Replacement(HandledItem handledItem) {
        this();
        this.handledItem = handledItem;
        price = this.handledItem.getBoxedItem().getCost();
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public HandledItem getHandledItem() {
        return handledItem;
    }

    public void setHandledItem(HandledItem handledItem) {
        this.handledItem = handledItem;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public Long getId() {
        return id;
    }
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(handledItem.getQuantity()));
    }
}
