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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
public class Node extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Node father;
    
    @OneToMany(mappedBy = "father", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Node> children;
    
    @NotNull
    @Column(nullable = false)
    private String description;
    
    @OneToOne(mappedBy = "node")
    private System system;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node", orphanRemoval = true)
    private final List<Device> devices;
    
    @Version
    private int version;

    public Node() {
        children = new ArrayList<>();
        devices = new ArrayList<>();
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }
    
    public void addNode(Node node) {
        if (!children.contains(node)) {
            node.setFather(this);
            children.add(node);
        }
    }
    
    public void removeNode(Node node) {
        if (children.contains(node)) {
            node.setFather(null);
            children.remove(node);
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public void addDevice(Device device) {
        if (!devices.contains(device)) {
            device.setNode(this);
            devices.add(device);
        }
    }
    
    public void removeDevice(Device device) {
        if (devices.contains(device)) {
            device.setNode(null);
            devices.remove(device);
        }
    }
    
    public List<Device> getDevices() {
        return devices;
    }
    
}
