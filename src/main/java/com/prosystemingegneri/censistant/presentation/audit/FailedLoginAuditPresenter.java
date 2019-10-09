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
package com.prosystemingegneri.censistant.presentation.audit;

import com.prosystemingegneri.censistant.business.audit.boundary.LoginAuditService;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@RequestScoped
public class FailedLoginAuditPresenter {
    
    private String dummy;
    
    @Inject
    private LoginAuditService loginAuditService;
    @Inject
    private Authenticator authenticator;
    
    @PostConstruct
    public void init() {
        if (FacesContext.getCurrentInstance().isProjectStage(ProjectStage.Production))
            loginAuditService.sendEventForFailedLogin(authenticator.getLoggedUser());
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }
    
}
