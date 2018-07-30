/*
 * Copyright (C) 2017-2018 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.purchasing.entity;

import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import java.math.BigDecimal;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@StaticMetamodel(PurchaseOrderRow.class)
public class PurchaseOrderRow_ {
    public static volatile SingularAttribute<PurchaseOrderRow, Long> id;
    public static volatile SingularAttribute<PurchaseOrderRow, Integer> quantity;
    public static volatile SingularAttribute<PurchaseOrderRow, BigDecimal> cost;
    public static volatile SingularAttribute<PurchaseOrderRow, BoxedItem> boxedItem;
    public static volatile SingularAttribute<PurchaseOrderRow, PurchaseOrder> purchaseOrder;
    public static volatile ListAttribute<PurchaseOrderRow, DeliveryNoteRow> deliveryNoteRows;
}
