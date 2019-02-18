/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.presentation.purchasing;

import com.prosystemingegneri.censistant.business.purchasing.boundary.SupplierItemImportExportService;
import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Named
@Dependent
public class SupplierItemImportExportPresenter implements Serializable {
    @Inject
    private SupplierItemImportExportService importExportService;
    
    public void handleSupplierItemsUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        try {
            importExportService.importSupplierItems(file.getInputstream());
        } catch (IOException | NumberFormatException | InvalidFormatException e) {
            Messages.create("Error").error().detail("Import error").add("importSupplierItemsDialog");
        }
        Messages.create("Success").detail("Import successful").add("importSupplierItemsDialog");
    }
}
