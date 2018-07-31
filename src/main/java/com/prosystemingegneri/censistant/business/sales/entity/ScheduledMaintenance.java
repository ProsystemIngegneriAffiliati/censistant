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
package com.prosystemingegneri.censistant.business.sales.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.sales.control.Interval;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class ScheduledMaintenance extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false, unique = true)
    private String name;
    
    @NotNull
    @Column(nullable = false)
    private String description;
    
    @Pattern(regexp = "[dmwy]\\d{1,2}", message = "Type a letter ('d', 'w', 'm' o 'y') and a number")
    private String repeatPattern;  //letter and number: d=day, w=week, m=month, y=year
    
    @NotNull
    @Column(nullable = false)
    private Boolean isDynamicExpiry;
    
    @Version
    private int version;

    public ScheduledMaintenance() {
        isDynamicExpiry = Boolean.FALSE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepeatPattern() {
        return repeatPattern;
    }

    public void setRepeatPattern(String repeatPattern) {
        this.repeatPattern = repeatPattern;
    }

    public Boolean getIsDynamicExpiry() {
        return isDynamicExpiry;
    }

    public void setIsDynamicExpiry(Boolean isDynamicExpiry) {
        this.isDynamicExpiry = isDynamicExpiry;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    private static int calculateCalendarField(String field) {
        for (Interval interval : Interval.values()) {
            if (field.equalsIgnoreCase(interval.toString())) {
                switch (interval) {
                    case DAY:
                        return Calendar.DAY_OF_YEAR;
                    case MONTH:
                        return Calendar.MONTH;
                    case WEEK:
                        return Calendar.WEEK_OF_YEAR;
                    case YEAR:
                        return Calendar.YEAR;
                    default:
                }
                break;
            }
        }
        
        return -1;
    }
    
    public static Date calculateNextDeadline(MaintenanceTask maintenanceTask) {
        ScheduledMaintenance scheduledMaintenance = maintenanceTask.getScheduledMaintenance();
        String repeatPattern = scheduledMaintenance.getRepeatPattern();
        if (repeatPattern != null) {
            int field = calculateCalendarField(repeatPattern.substring(0, 1));
            if (field >= 0) {
                try {
                    Date startDate;
                    if (scheduledMaintenance.getIsDynamicExpiry())
                        startDate = maintenanceTask.getClosed();
                    else
                        startDate = maintenanceTask.getExpiry();
                    Calendar nextExpiry = new GregorianCalendar();
                    nextExpiry.setTime(startDate);
                    nextExpiry.add(field, Integer.valueOf(repeatPattern.substring(1)));

                    return nextExpiry.getTime();
                } catch (NumberFormatException e) {
                }
            }
        }
            
        return null;
    }
}
