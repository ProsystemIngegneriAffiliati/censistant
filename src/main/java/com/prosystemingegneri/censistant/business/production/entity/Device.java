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
package com.prosystemingegneri.censistant.business.production.entity;

import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@DiscriminatorValue(value = "2")
public class Device extends Location {
    
    @ManyToOne(optional = false)
    private Node node;
    
    @NotNull
    @ManyToOne(optional = false)
    private Item item;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    public Device() {
    }
    
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
