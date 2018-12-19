/*
 * Copyright (C) 2018 Prosystem Ingegneri Affiliati.
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
package com.prosystemingegneri.censistant.business.purchasing.boundary;

import com.prosystemingegneri.censistant.business.purchasing.entity.BoxedItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@Stateless
public class SupplierItemImportExportService implements Serializable {
    @Inject
    private BoxedItemService boxedItemService;
    
    private enum COLUMNS {
        ID_BOXED_ITEM,
        SUPPLIER_ITEM_CODE,
        ITEM_DESCRIPTION,
        SUPPLIER_NAME,
        BOXED_ITEM_COST;
    }
    
    public void importSupplierItems(InputStream fileContent) throws IOException, InvalidFormatException, NumberFormatException {
        Workbook wb = WorkbookFactory.create(fileContent);
        Sheet sheet = wb.getSheetAt(0);
        
        String idStr;
        BoxedItem boxedItem;
        BigDecimal cost;
        
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); //start from second line
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            boxedItem = null;
            
            try {
                idStr = readCell(row.getCell(COLUMNS.ID_BOXED_ITEM.ordinal()));
                if (idStr != null && !idStr.isEmpty())
                    boxedItem = boxedItemService.read((new Double(idStr)).longValue());
            } catch (IllegalArgumentException e) {
                boxedItem = null;
            }
            
            if (boxedItem != null) {
                try {
                    cost = BigDecimal.valueOf(new Double(readCell(row.getCell(COLUMNS.BOXED_ITEM_COST.ordinal()))));
                    if (!boxedItem.getCost().equals(cost)) {
                        boxedItem.setCost(cost);
                        boxedItemService.save(boxedItem);
                    }
                } catch (IllegalArgumentException | TransactionRequiredException e) {
                    //nothing to do - passes to the next row
                }
            }
        }
        
        wb.close();
        
    }

    private String readCell(Cell cell) {
        if (cell != null)
            switch (cell.getCellTypeEnum()) {
                case NUMERIC:
                    return Double.toString(cell.getNumericCellValue());
                case STRING:
                    return cell.getStringCellValue().trim();
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                default:
                    return "";
            }
        return "";
    }
}
