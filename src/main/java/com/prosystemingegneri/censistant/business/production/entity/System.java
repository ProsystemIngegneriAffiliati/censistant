/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.production.entity;

import com.prosystemingegneri.censistant.business.entity.BaseEntity;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class System extends BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private String description;
    
    @NotNull
    @OneToOne(optional = false)
    private Node node;
    
    @OneToMany(mappedBy = "system")
    private final List<JobOrder> jobOrders;
    
    private String userCode;
    
    private String controlPanelPhoneNumber;
    
    private String controlPanelPosition;
    
    private String powerSource;
    
    private String notes;
    
    @Version
    private int version;

    public System() {
        jobOrders = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Node getNode() {
        return node;
    }

    public void addNode(Node node) {
        node.setSystem(this);
        this.node = node;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getControlPanelPhoneNumber() {
        return controlPanelPhoneNumber;
    }

    public void setControlPanelPhoneNumber(String controlPanelPhoneNumber) {
        this.controlPanelPhoneNumber = controlPanelPhoneNumber;
    }

    public String getControlPanelPosition() {
        return controlPanelPosition;
    }

    public void setControlPanelPosition(String controlPanelPosition) {
        this.controlPanelPosition = controlPanelPosition;
    }

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
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
    
    public void addJobOrder(JobOrder jobOrder) {
        if (!jobOrders.contains(jobOrder)) {
            jobOrder.setSystem(this);
            jobOrders.add(jobOrder);
        }
    }
    
    public void removeJobOrder(JobOrder jobOrder) {
        if (jobOrders.contains(jobOrder)) {
            jobOrder.setSystem(null);
            jobOrders.remove(jobOrder);
        }
    }
    
    public List<JobOrder> getJobOrders() {
        return jobOrders;
    }
    
}
