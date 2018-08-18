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

import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.Worker;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@StaticMetamodel(MaintenanceTask.class)
public class MaintenanceTask_ {
    public static volatile SingularAttribute<MaintenanceTask, Long> id;
    public static volatile SingularAttribute<MaintenanceTask, Integer> number;
    public static volatile SingularAttribute<MaintenanceTask, MaintenanceContract> maintenanceContract;
    public static volatile SingularAttribute<MaintenanceTask, PreventiveMaintenance> preventiveMaintenance;
    public static volatile SingularAttribute<MaintenanceTask, System> system;
    public static volatile SingularAttribute<MaintenanceTask, String> description;
    public static volatile SingularAttribute<MaintenanceTask, Date> creation;
    public static volatile SingularAttribute<MaintenanceTask, Worker> inChargeWorker;
    public static volatile SingularAttribute<MaintenanceTask, Boolean> isGuaranteeValid;
    public static volatile SingularAttribute<MaintenanceTask, Date> expiry;
    public static volatile SingularAttribute<MaintenanceTask, Date> closed;
}
