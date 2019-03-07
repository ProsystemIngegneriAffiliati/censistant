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
package com.prosystemingegneri.censistant.business.maintenance.entity2;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Entity
public class MaintenanceContract2 extends BaseEntity<Long> {
    @Transient
    public static final Integer DURATION_MONTHS = 12;
    @Transient
    public static final int SCALE = 2; //If zero or positive, the scale is the number of digits to the right of the decimal point.
    @Transient
    public static final int PRECISION = 7;
    @Transient
    private final String SEPARATOR = " - ";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creation;
    
    private String payment;
    
    @NotNull
    @DecimalMin(value = "0")
    @Column(nullable = false, scale = SCALE, precision = PRECISION)   
    private BigDecimal price;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceContract", orphanRemoval = true)
    private final List<ContractedSystem> contractedSystems;
    
    @Lob
    private String notes;
    
    @Version
    private int version;

    public MaintenanceContract2() {
        creation = new Date();
        price = BigDecimal.ZERO;
        contractedSystems = new ArrayList<>();
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public void addContractedSystem(ContractedSystem contractedSystem) {
        if (!contractedSystems.contains(contractedSystem)) {
            contractedSystems.add(contractedSystem);
            contractedSystem.setMaintenanceContract(this);
        }
    }
    
    public void removeContractedSystem(ContractedSystem contractedSystem) {
        if (contractedSystems.contains(contractedSystem)) {
            contractedSystems.remove(contractedSystem);
            contractedSystem.setMaintenanceContract(this);
        }
    }
    
    public List<ContractedSystem> getContractedSystems() {
        return contractedSystems;
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
    
    public Date getExpiry() {
        Calendar created = new GregorianCalendar();
        created.setTime(creation);
        created.add(Calendar.MONTH, DURATION_MONTHS);
        
        return created.getTime();
    }
    
    public CustomerSupplier getCustomer() {
        if (contractedSystems != null && !contractedSystems.isEmpty())
            return contractedSystems.get(0).getSystem().getCustomerSupplier();
        else
            return null;
    }
    
    public String getCustomerName() {
        if (contractedSystems != null && !contractedSystems.isEmpty() && contractedSystems.get(0).getSystem().getCustomerSupplier() != null)
            return contractedSystems.get(0).getSystem().getCustomerSupplier().getName();
        else
            return "";
    }
    
    public String getCustomerNameContractExpiry() {
                return new StringBuilder(getCustomerName())
                .append(SEPARATOR)
                .append(new SimpleDateFormat("dd/MM/yyyy").format(getExpiry()))
                .toString();
    }
    
}
