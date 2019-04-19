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
package com.prosystemingegneri.censistant.presentation.maintenance;

import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTaskDto;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class MaintenanceTaskControl {
    public static String calculateExpiryColor(MaintenanceTask maintenanceTask) {
        if (maintenanceTask.getClosed() == null && maintenanceTask.getExpiry() != null)
            return calculate(maintenanceTask.getExpiry());
        
        return "";
    }
    
    public static String calculateExpiryColor(MaintenanceTaskDto maintenanceTaskDto) {
        if (maintenanceTaskDto.getClosed() == null && maintenanceTaskDto.getExpiry() != null)
            return calculate(maintenanceTaskDto.getExpiry());
        
        return "";
    }
    
    private static String calculate(Date expiry) {
        GregorianCalendar oneMonthAhead = new GregorianCalendar();
        oneMonthAhead.add(Calendar.MONTH, 1);

        GregorianCalendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        GregorianCalendar scadCal = new GregorianCalendar();
        scadCal.setTime(expiry);

        if (scadCal.before(yesterday))
            return "red-row";
        if (scadCal.before(oneMonthAhead))
            return "amber-row";
        
        return "";
    }
}
