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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "scheduledMaintenance", orphanRemoval = true)
    private final List<MaintenanceTask> maintenanceTasks;
    
    @Version
    private int version;

    public ScheduledMaintenance() {
        isDynamicExpiry = Boolean.FALSE;
        maintenanceTasks = new ArrayList<>();
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
    
    public void addMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (!maintenanceTasks.contains(maintenanceTask)) {
            maintenanceTask.setScheduledMaintenance(this);
            maintenanceTasks.add(maintenanceTask);
        }
    }
    
    public void removeMaintenanceTask(MaintenanceTask maintenanceTask) {
        if (maintenanceTasks.contains(maintenanceTask)) {
            maintenanceTask.setScheduledMaintenance(null);
            maintenanceTasks.remove(maintenanceTask);
        }
    }

    public List<MaintenanceTask> getMaintenanceTasks() {
        return maintenanceTasks;
    }
    
}
