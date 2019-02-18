/*
 * Copyright (C) 2017-2019 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.presentation.user;

import com.prosystemingegneri.censistant.business.user.boundary.UserService;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import com.prosystemingegneri.censistant.presentation.Authenticator;
import com.prosystemingegneri.censistant.presentation.ExceptionUtility;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class ListUserPresenter implements Serializable{
    private List<UserApp> users;
    private List<UserApp> selectedUsers;
    
    @Inject
    UserService userService;
    
    @Inject
    Authenticator authenticator;
    
    @PostConstruct
    public void init() {
        users = userService.listUserApps();
    }
    
    public void deleteUsers() {
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            for (UserApp userTemp : selectedUsers)
                if (userTemp.getUserName().equals(authenticator.getLoggedUser().getUserName()))
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in deleting user", "You cannot delete yourself"));
                else {
                    try {
                        userService.deleteUserApp(userTemp);
                    } catch (EJBException e) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtility.unwrap(e.getCausedByException()).getLocalizedMessage()));
                    }
                }
            users = userService.listUserApps();
        }
        else
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Missing selection", "Select a user before deleting"));
    }

    public List<UserApp> getUsers() {
        return users;
    }

    public List<UserApp> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<UserApp> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}