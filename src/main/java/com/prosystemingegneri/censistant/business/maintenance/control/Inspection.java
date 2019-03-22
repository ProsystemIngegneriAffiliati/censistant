/*
 * Copyright (C) 2019 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.maintenance.control;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public enum Inspection {
    POWER_SUPPLY_220V(0),
    POWER_SUPPLY_12V(1),
    ACCUMULATORS(2),
    SENSORS_UP_AND_RUNNING(3),
    WARNING_DEVICES(4),
    SYSTEM_TESTING(5);

    private final int value;

    private Inspection(int value) {
        this.value = value;
    }
}
