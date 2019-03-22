/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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
import com.prosystemingegneri.censistant.business.maintenance.control.Inspection;
import com.prosystemingegneri.censistant.business.maintenance.control.ResultType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class InspectionDone extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private Inspection inspection;
    
    @NotNull
    @ManyToOne(optional = false)
    private MaintenanceTask maintenanceTask;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "smallint")
    private ResultType inspectionResult;
    
    private String notes;
    
    @Version
    private int version;

    public InspectionDone() {
        inspectionResult = ResultType.NULL;
    }

    public InspectionDone(Inspection inspection) {
        this();
        this.inspection = inspection;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public Long getId() {
        return id;
    }

    public ResultType getInspectionResult() {
        return inspectionResult;
    }

    public void setInspectionResult(ResultType inspectionResult) {
        this.inspectionResult = inspectionResult;
    }
    
}
