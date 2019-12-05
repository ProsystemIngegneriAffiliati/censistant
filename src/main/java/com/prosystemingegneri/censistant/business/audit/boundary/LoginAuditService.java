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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
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
    
    public static final String AUDITOR = "mainardi@prosystemingegneri.com";
    
    @Asynchronous
    public void sendEventForLogin(String username) {
        try {
            mailer.sendMail(
                    username + ": nuovo login",
                    new StringBuilder("Il giorno ")
                            .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" alle ore ")
                            .append(LocalTime.now(ZoneId.of("Europe/Paris")).format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" l'utente ")
                            .append(username)
                            .append(" ha effettuato l'accesso.")
                            .toString(),
                    Arrays.asList(AUDITOR),
                    null);
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Asynchronous
    public void sendEventForFailedLogin(String username) {
        String testoUtente = "è stato";
        if (username != null && !username.isEmpty())
            testoUtente = "l'utente " + username + " ha";
        try {
            mailer.sendMail(
                    "Tentativo di accesso",
                    new StringBuilder("Il giorno ")
                            .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" alle ore ")
                            .append(LocalTime.now(ZoneId.of("Europe/Paris")).format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" ")
                            .append(testoUtente)
                            .append(" tentato un accesso al programma.")
                            .append(System.lineSeparator())
                            .append("Lo username o la password sono risultati errati ed è stato impedito l'accesso.")
                            .toString(),
                    Arrays.asList(AUDITOR),
                    null);
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Asynchronous
    public void sendEventForAccessDenied(String username) {
        String testoUtente = "è stato";
        if (username != null && !username.isEmpty())
            testoUtente = "l'utente " + username + " ha";
        try {
            mailer.sendMail(
                    "Tentativo di accesso",
                    new StringBuilder("Il giorno ")
                            .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" alle ore ")
                            .append(LocalTime.now(ZoneId.of("Europe/Paris")).format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" ")
                            .append(testoUtente)
                            .append(" tentato un accesso a zone riservate del programma.")
                            .append(System.lineSeparator())
                            .append("I privilegi insufficienti non hanno consentito di continuare.")
                            .toString(),
                    Arrays.asList(AUDITOR),
                    null);
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Asynchronous
    public void sendEventForLogout(String username) {
        try {
            mailer.sendMail(
                    username + ": uscita dal programma",
                    new StringBuilder("Il giorno ")
                            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .append(" alle ore ")
                            .append(LocalTime.now(ZoneId.of("Europe/Paris")).format(DateTimeFormatter.ofPattern("HH:mm")))
                            .append(" l'utente ")
                            .append(username)
                            .append(" è uscito dal programma.")
                            .toString(),
                    Arrays.asList(AUDITOR),
                    null);
        } catch (MessagingException ex) {
            Logger.getLogger(LoginAuditService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
