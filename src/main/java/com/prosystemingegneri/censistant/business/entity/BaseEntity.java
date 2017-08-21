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
package com.prosystemingegneri.censistant.business.entity;

import java.io.Serializable;

/**
 *
 * @author Bauke Scholtz
 * @param <T> IDs must extend Number class
 */
public abstract class BaseEntity<T extends Number> implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract T getId();

    //public abstract void setId(T id); //ID's are auto-generated, so there is no need to set them

    @Override
    public int hashCode() {
        return (getId() != null) 
            ? (getClass().getSimpleName().hashCode() + getId().hashCode())
            : super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other != null && getId() != null
                && other.getClass().isAssignableFrom(getClass()) 
                && getClass().isAssignableFrom(other.getClass())) 
            ? getId().equals(((BaseEntity<?>) other).getId())
            : (other == this);
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

}
