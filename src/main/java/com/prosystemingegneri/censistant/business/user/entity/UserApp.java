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
package com.prosystemingegneri.censistant.business.user.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Entity
@Table(name = "user_app")
public class UserApp implements Serializable{
    @Id
    @Column(name = "user_name")
    private String userName;
    
    @NotNull
    @Column(nullable = false, name = "pass_word")
    private String password;
    
    @ManyToMany
    @JoinTable(name = "users_groups_app",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "groups_group_name"))
    private List<GroupApp> groups;  //joinColumns è necessario perché la colonna deve avere lo stesso nome nella tabella degli utenti ed in quella di scambio
    
    private String imageFilename;
    
    @Version
    private int version;

    public UserApp() {
        groups = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<GroupApp> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupApp> groups) {
        this.groups = groups;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * An activated user belong to one (or more) group
     * @return true i user is activated, false otherwise
     */
    public boolean isActivated() {
        return !this.groups.isEmpty();
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
    
    @Override
    public boolean equals(Object object) {
        return (object instanceof UserApp) && (userName != null) 
             ? userName.equals(((UserApp) object).userName) 
             : (object == this);
    }

    @Override
    public int hashCode() {
        return (userName != null)
             ? (UserApp.class.hashCode() + userName.hashCode())
             : super.hashCode();
    }
    
}