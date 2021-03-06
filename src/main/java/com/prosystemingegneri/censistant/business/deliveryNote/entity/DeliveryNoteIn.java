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
package com.prosystemingegneri.censistant.business.deliveryNote.entity;

import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@DiscriminatorValue(value = "0")
public class DeliveryNoteIn extends DeliveryNoteCommon {
    @NotNull
    @Column(nullable = false)
    private String numberStr;

    public DeliveryNoteIn() {
    }
    
    public DeliveryNoteIn(Integer number) {
        super(number);
    }

    public String getNumberStr() {
        return numberStr;
    }

    public void setNumberStr(String numberStr) {
        this.numberStr = numberStr;
    }
    
    @Override
    public String getNumberAndCreation() {
        return new StringBuilder(numberStr)
                .append(SEPARATOR)
                .append(new SimpleDateFormat("dd/MM/yyyy").format(super.getCreation()))
                .toString();
    }
    
}
