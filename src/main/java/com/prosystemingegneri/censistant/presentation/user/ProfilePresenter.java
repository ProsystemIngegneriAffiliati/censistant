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
import com.prosystemingegneri.censistant.presentation.DocumentAndImageUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class ProfilePresenter implements Serializable {

    @Inject
    UserService userService;
    @Inject
    Authenticator authenticator;

    UserApp profile;

    @PostConstruct
    public void init() {
        profile = authenticator.getLoggedUser();
        if (profile == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must logout before change your account profile"));
        }
    }

    public String saveProfile() {
        profile = userService.saveUserApp(profile);
        return "/securedBasic/welcome?faces-redirect=true";
    }

    public void handleFileUpload(FileUploadEvent event) {
        BufferedImage croppedImage;
        String fileType = "png";

        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(event.getFile().getInputstream());
            if (originalImage.getHeight() > originalImage.getWidth()) {
                croppedImage = originalImage.getSubimage(0, (originalImage.getHeight() - originalImage.getWidth()) / 2, originalImage.getWidth(), originalImage.getWidth());
            } else {
                croppedImage = originalImage.getSubimage((originalImage.getWidth() - originalImage.getHeight()) / 2, 0, originalImage.getHeight(), originalImage.getHeight());
            }

            BufferedImage finalImage = new BufferedImage(DocumentAndImageUtils.IMAGE_HEIGHT, DocumentAndImageUtils.IMAGE_HEIGHT, croppedImage.getType());
            Graphics2D g = finalImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(croppedImage, 0, 0, DocumentAndImageUtils.IMAGE_HEIGHT, DocumentAndImageUtils.IMAGE_HEIGHT, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), null);
            g.dispose();
            profile.setImageFilename(profile.getUserName() + "." + fileType);
            ImageIO.write(finalImage, fileType, new File(DocumentAndImageUtils.IMAGE_PATH + "/" + profile.getImageFilename()));

        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during image uploading and processing" + 
                    System.lineSeparator() +
                    System.lineSeparator() + ex.getLocalizedMessage()));
        }
    }

    public UserApp getProfile() {
        return profile;
    }

    public void setProfile(UserApp profile) {
        this.profile = profile;
    }

}
