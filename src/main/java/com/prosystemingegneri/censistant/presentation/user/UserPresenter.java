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
package com.prosystemingegneri.censistant.presentation.user;

import com.prosystemingegneri.censistant.business.user.boundary.UserService;
import com.prosystemingegneri.censistant.business.user.entity.GroupApp;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class UserPresenter implements Serializable {
    @Inject
    UserService userService;
    
    private UserApp user;
    private String id;
    
    private DualListModel<GroupApp> groups = new DualListModel<>();
    
    public void readUserApp() {
        if (id != null && !id.isEmpty())
            user = userService.readUserApp(id);
    }
    
    public String saveUserApp() {
        user.getGroups().clear();
        user.getGroups().addAll(groups.getTarget());
        user = userService.saveUserApp(user);
        
        return "/secured/manageUser/users?faces-redirect=true";
    }

    public UserApp getUserApp() {
        return user;
    }

    public void setUserApp(UserApp userApp) {
        this.user = userApp;
    }
    
    public String addGroup(GroupApp group) {
        this.user.getGroups().add(group);
        return "/secured/manageUser/user?faces-redirect=true";
    }
    
    public void removeGroup(GroupApp group) {
        this.user.getGroups().remove(group);
    }
    
    public List<GroupApp> avaibleGroups(UserApp user) {
        return userService.avaibleGroups(user);
    }

    public DualListModel<GroupApp> getGroups() {
        if (groups.getSource().isEmpty() && groups.getTarget().isEmpty()) {
            groups.setSource(userService.avaibleGroups(user));
            groups.setTarget(user.getGroups());
        }
        return groups;
    }

    public void setGroups(DualListModel<GroupApp> groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
