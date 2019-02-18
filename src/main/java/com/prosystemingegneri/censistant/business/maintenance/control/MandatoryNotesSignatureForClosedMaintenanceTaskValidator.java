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
package com.prosystemingegneri.censistant.business.maintenance.control;

import com.prosystemingegneri.censistant.business.maintenance.entity.MaintenanceTask;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class MandatoryNotesSignatureForClosedMaintenanceTaskValidator implements ConstraintValidator<MandatoryNotesSignatureForClosedMaintenanceTask, MaintenanceTask> {

    @Override
    public void initialize(MandatoryNotesSignatureForClosedMaintenanceTask constraintAnnotation) {
    }

    @Override
    public boolean isValid(MaintenanceTask mantenanceTask, ConstraintValidatorContext context) {
        
        return !(mantenanceTask.getClosed() != null && ((mantenanceTask.getClosingNotes() == null || mantenanceTask.getClosingNotes().isEmpty()) || (mantenanceTask.getCustomerSignature() == null || mantenanceTask.getCustomerSignature().isEmpty())));
    }
    
}
