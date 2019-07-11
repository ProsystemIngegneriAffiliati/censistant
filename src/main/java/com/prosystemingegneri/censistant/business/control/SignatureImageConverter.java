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
package com.prosystemingegneri.censistant.business.control;

import com.prosystemingegneri.censistant.business.maintenance.control.SigGen;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
public class SignatureImageConverter {

    public static Image convertoToImage(String signature) {
        Image signatureImg = null;
        if (signature != null) {
            try {
                ByteArrayOutputStream signatureOut = new ByteArrayOutputStream();
                SigGen.generateSignature(signature, signatureOut);

                // take the copy of the stream and re-write it to an InputStream
                PipedInputStream signatureIn = new PipedInputStream();
                final PipedOutputStream out = new PipedOutputStream(signatureIn);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            // write the original OutputStream to the PipedOutputStream
                            signatureOut.writeTo(out);
                        } catch (IOException e) {
                            // logging and exception handling should go here
                        }
                    }
                }).start();

                signatureImg = ImageIO.read(signatureIn);
            } catch (IOException ex) {
                return null;
            }
        }

        return signatureImg;
    }
}
