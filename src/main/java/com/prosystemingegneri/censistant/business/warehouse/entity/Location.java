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

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Version;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "smallint")
public abstract class Location extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "toLocation")
    private final List<HandledItem> inHandledItem;  //received items
    
    @OneToMany(mappedBy = "fromLocation")
    private final List<HandledItem> outHandledItem; //picked items
    
    abstract String getName();
    
    @Version
    private int version;

    public Location() {
        inHandledItem = new ArrayList<>();
        outHandledItem = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    public List<HandledItem> getInHandledItem() {
        return inHandledItem;
    }

    public List<HandledItem> getOutHandledItem() {
        return outHandledItem;
    }
    
}
