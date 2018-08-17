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
import static com.prosystemingegneri.censistant.business.maintenance.entity.TaskPrice.PRECISION_PRICE;
import static com.prosystemingegneri.censistant.business.maintenance.entity.TaskPrice.SCALE_PRICE;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class WorkingDuration extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date started;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ended;
    
    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer workersNumber;
    
    @NotNull
    @DecimalMin("0")
    @Column(nullable = false, scale = SCALE_PRICE, precision = PRECISION_PRICE)
    private BigDecimal hourlyPrices;
    
    @Version
    private int version;

    public WorkingDuration() {
        started = new Date();
        ended = new Date();
        workersNumber = 1;
        hourlyPrices = BigDecimal.ZERO;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public Integer getWorkersNumber() {
        return workersNumber;
    }

    public void setWorkersNumber(Integer workersNumber) {
        this.workersNumber = workersNumber;
    }

    public BigDecimal getHourlyPrices() {
        return hourlyPrices;
    }

    public void setHourlyPrices(BigDecimal hourlyPrices) {
        this.hourlyPrices = hourlyPrices;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public BigDecimal getDurationInHours() {
        try {
            return new BigDecimal(TimeUnit.MILLISECONDS.toMinutes(ended.getTime() - started.getTime())).divide(BigDecimal.valueOf(60));
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getPrice() {
        return hourlyPrices
                .multiply(BigDecimal.valueOf(workersNumber))
                .multiply(getDurationInHours());
    }
    
}
