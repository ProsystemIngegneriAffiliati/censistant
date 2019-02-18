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
package com.prosystemingegneri.censistant.presentation;

import com.prosystemingegneri.censistant.business.user.boundary.UserService;
import com.prosystemingegneri.censistant.business.user.entity.GroupApp;
import com.prosystemingegneri.censistant.business.user.entity.UserApp;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Base64;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@SessionScoped
public class Authenticator implements Serializable {
    @Inject
    UserService userService;
    
    private UserApp loggedUser;
    private String userName;
    private String oldPassword;
    private String newPassword1;
    private String newPassword2;
    
    public UserApp getLoggedUser() {
        /*if (loggedUser == null) {*/
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null)
                loggedUser = userService.readUserApp(principal.getName());
        /*}*/
        return loggedUser;
    }
    
    public String createNewUser() {
        if (loggedUser == null) {
            if (userService.readUserApp(userName) == null) {
                if (newPassword1.equals(newPassword2)) {
                    UserApp newUser = new UserApp();
                    newUser.setUserName(userName);
                    newUser.setPassword(hashPassword(newPassword1));
                    userService.createUserApp(newUser);
                }
                else {
                    FacesContext.getCurrentInstance().addMessage("createUserForm:newPassword2", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong new password", "New passwords must be equals"));
                    return null;
                }
            }
            else {
                FacesContext.getCurrentInstance().addMessage("createUserForm:username", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong username", "'" + userName + "' is already present. Choose another username."));
                return null;
            }
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must logout before create a new user"));
            return null;
        }
        cleanUserData();
        return "/index?faces-redirect=true";
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    
    public String changePassword() {
        if (loggedUser != null) {
            if (loggedUser.getPassword().equals(hashPassword(oldPassword))) {
                if (newPassword1.equals(newPassword2)) {
                    loggedUser.setPassword(hashPassword(newPassword1));
                    loggedUser = userService.saveUserApp(loggedUser);
                }
                else {
                    FacesContext.getCurrentInstance().addMessage("changePasswordForm:newPassword2", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong new password", "New passwords must be equals"));
                    return null;
                }
            }
            else {
                FacesContext.getCurrentInstance().addMessage("changePasswordForm:oldPassword", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong old password", "Old password is not valid"));
                return null;
            }
        }
        cleanUserData();
        return "/index?faces-redirect=true";
    }
    
    private static String hashPassword(String passwordToBeHashed) {
        String CHARSET = "UTF-8";
        String ENCRYPTION_ALGORITHM = "SHA-256";
        MessageDigest md;
        try {
            byte[] bytesOfMessage = passwordToBeHashed.getBytes(CHARSET);
            
            md = MessageDigest.getInstance(ENCRYPTION_ALGORITHM);
            md.update(bytesOfMessage);
            
            return Base64.getEncoder().encodeToString(md.digest());

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            return "Error";
        }
    }
    
    public boolean isLoggedUserAdmin() {
        if (loggedUser != null)
            for (GroupApp group : loggedUser.getGroups())
                if ("admin".equalsIgnoreCase(group.getGroupName()))
                    return true;
        return false;
    }
    
    private void cleanUserData() {
        userName = "";
        oldPassword = "";
        newPassword1 = "";
        newPassword2 = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
    
}