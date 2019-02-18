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

import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.omnifaces.servlet.FileServlet;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@WebServlet("/images/*")
public class ImageFileServlet extends FileServlet {

    private File folder;

    @Override
    public void init() throws ServletException {
        folder = new File(DocumentAndImageUtils.IMAGE_PATH);
    }

    @Override
    protected File getFile(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            throw new IllegalArgumentException();
        }

        return new File(folder, pathInfo);
    }

    @Override
    protected boolean isAttachment(HttpServletRequest request, String contentType) {
        return false;
    }
}
