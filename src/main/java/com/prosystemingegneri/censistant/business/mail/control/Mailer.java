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
package com.prosystemingegneri.censistant.business.mail.control;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Dependent
public class Mailer implements Serializable {
    @Resource(lookup = "mail/censistantMail")
    private Session session;
    
    public void sendMail(String subject, String body, List<String> listTo, List<String> listToCcn) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);

        msg.setFrom();
        msg.addRecipients(RecipientType.TO, InternetAddress.parse(String.join(",", listTo)));
        if (listToCcn != null && !listToCcn.isEmpty())
            msg.addRecipients(RecipientType.BCC, InternetAddress.parse(String.join(",", listToCcn)));
        msg.setSubject(subject, "UTF-8");
        msg.setText(body, "UTF-8");
        msg.setHeader("Content-Type", "text/plain; charset=UTF-8");

        Transport.send(msg);
    }

}
