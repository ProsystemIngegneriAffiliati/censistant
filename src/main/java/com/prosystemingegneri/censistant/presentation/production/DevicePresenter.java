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
package com.prosystemingegneri.censistant.presentation.production;

import com.prosystemingegneri.censistant.business.production.entity.Device;
import com.prosystemingegneri.censistant.business.sales.entity.JobOrder;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class DevicePresenter implements Serializable {
    private Device device;
    private JobOrder jobOrder;
    private Integer activeIndex;
    
    @PostConstruct
    public void init() {
        jobOrder = (JobOrder) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("jobOrder");
        activeIndex = (Integer) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("activeIndex");
        device = (Device) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("device");
        if (device == null)
            device = new Device();
    }
    
    public String saveDevice() {
        if (device.getSystem() == null)
            jobOrder.getOffer().getSystem().addDevice(device);
        
        putExternalContext();
        
        return "/secured/sales/jobOrder?faces-redirect=true";
    }
    
    public String cancel() {
        putExternalContext();
        
        return "/secured/sales/jobOrder?faces-redirect=true";
    }
    
    private void putExternalContext() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("jobOrder", jobOrder);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activeIndex", activeIndex);
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    
}