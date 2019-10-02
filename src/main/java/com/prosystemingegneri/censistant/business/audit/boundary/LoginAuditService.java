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
package com.prosystemingegneri.censistant.business.audit.boundary;

import com.prosystemingegneri.censistant.business.mail.control.Mailer;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class LoginAuditService implements Serializable {
    @Inject
    private Mailer mailer;
    
    private static final String AUDITOR = "forzato@antifurto.com";
    
    public void sendEventForLogin(String username) {
        try {
            mailer.sendMail(
                    username + ": nuovo login",
                    (new StringBuilder("Il giorno ")
                            .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" all'ora odierna l'utente "))
                            .append(username)
                            .append(" ha effettuato l'accesso.")
                            .toString(),
                    Arrays.asList(AUDITOR));
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendEventForLogout(String username) {
        try {
            mailer.sendMail(
                    username + ": uscita dal programma",
                    (new StringBuilder("Il giorno ")
                            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" all'ora odierna l'utente "))
                            .append(username)
                            .append(" è uscito dal programma.")
                            .toString(),
                    Arrays.asList(AUDITOR));
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
