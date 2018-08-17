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

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class TaskPrice extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Transient
    public static final int SCALE_PRICE = 2; //If zero or positive, the scale is the number of digits to the right of the decimal point.
    @Transient
    public static final int PRECISION_PRICE = 8;
    
    @NotNull
    @OneToOne(optional = false)
    private MaintenanceTask maintenanceTask;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_PRICE, precision = PRECISION_PRICE)
    private BigDecimal fixedCallAmount;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkingDuration normalWorking;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkingDuration overtimeWorking;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkingDuration travelWorking;
    
    @Min(0)
    @NotNull
    @Column(nullable = false)
    private Integer kilometersTravelled;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_PRICE, precision = PRECISION_PRICE)
    private BigDecimal pricePerKilometer;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_PRICE, precision = PRECISION_PRICE)
    private BigDecimal travelExpenses;
    
    @Version
    private int version;

    public TaskPrice() {
        fixedCallAmount = BigDecimal.ZERO;
        kilometersTravelled = 0;
        pricePerKilometer = BigDecimal.ZERO;
        travelExpenses = BigDecimal.ZERO;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public BigDecimal getFixedCallAmount() {
        return fixedCallAmount;
    }

    public void setFixedCallAmount(BigDecimal fixedCallAmount) {
        this.fixedCallAmount = fixedCallAmount;
    }

    public WorkingDuration getNormalWorking() {
        return normalWorking;
    }

    public void setNormalWorking(WorkingDuration normalWorking) {
        this.normalWorking = normalWorking;
    }

    public WorkingDuration getOvertimeWorking() {
        return overtimeWorking;
    }

    public void setOvertimeWorking(WorkingDuration overtimeWorking) {
        this.overtimeWorking = overtimeWorking;
    }

    public WorkingDuration getTravelWorking() {
        return travelWorking;
    }

    public void setTravelWorking(WorkingDuration travelWorking) {
        this.travelWorking = travelWorking;
    }

    public Integer getKilometersTravelled() {
        return kilometersTravelled;
    }

    public void setKilometersTravelled(Integer kilometersTravelled) {
        this.kilometersTravelled = kilometersTravelled;
    }

    public BigDecimal getPricePerKilometer() {
        return pricePerKilometer;
    }

    public void setPricePerKilometer(BigDecimal pricePerKilometer) {
        this.pricePerKilometer = pricePerKilometer;
    }

    public BigDecimal getTravelExpenses() {
        return travelExpenses;
    }

    public void setTravelExpenses(BigDecimal travelExpenses) {
        this.travelExpenses = travelExpenses;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public BigDecimal getPriceTravelled() {
        return pricePerKilometer.multiply(new BigDecimal(kilometersTravelled));
    }
    
    public BigDecimal getPrice() {
        BigDecimal result = BigDecimal.ZERO;
        
        result = result
                .add(fixedCallAmount)
                .add(getPriceTravelled())
                .add(travelExpenses);
        if (normalWorking != null)
            result = result.add(normalWorking.getPrice());
        if (overtimeWorking != null)
            result = result.add(overtimeWorking.getPrice());
        if (travelWorking != null)
            result = result.add(travelWorking.getPrice());
        
        return result;
    }
    
}
