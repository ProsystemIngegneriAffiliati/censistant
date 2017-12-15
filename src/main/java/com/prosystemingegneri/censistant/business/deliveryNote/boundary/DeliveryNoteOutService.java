/*
 * Copyright (C) 2017 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.deliveryNote.boundary;

import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteCommon_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class DeliveryNoteOutService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    public DeliveryNoteOut saveDeliveryNoteOut(DeliveryNoteOut deliveryNoteOut) {        
        if (deliveryNoteOut.getId() == null)
            em.persist(deliveryNoteOut);
        else
            return em.merge(deliveryNoteOut);
        
        return deliveryNoteOut;
    }
    
    public DeliveryNoteOut readDeliveryNoteOut(Long id) {
        return em.find(DeliveryNoteOut.class, id);
    }
    
    public void deleteDeliveryNoteOut(Long id) {
        em.remove(readDeliveryNoteOut(id));
    }

    public List<DeliveryNoteOut> listDeliveryNoteOuts(int first, int pageSize, String sortField, Boolean isAscending, Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeliveryNoteOut> query = cb.createQuery(DeliveryNoteOut.class);
        Root<DeliveryNoteOut> root = query.from(DeliveryNoteOut.class);
        CriteriaQuery<DeliveryNoteOut> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, goodsDescription, shipmentReason, shippingPayment);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        Order order = cb.desc(root.get(DeliveryNoteCommon_.creation));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "number":
                    path = root.get(DeliveryNoteCommon_.number);
                    break;
                case "creation":
                    path = root.get(DeliveryNoteCommon_.creation);
                    break;
                case "goodsDescription":
                    path = root.get(DeliveryNoteOut_.goodsDescription).get(GoodsDescription_.name);
                    break;
                case "shipmentReason":
                    path = root.get(DeliveryNoteOut_.shipmentReason).get(ShipmentReason_.name);
                    break;
                case "shippingPayment":
                    path = root.get(DeliveryNoteOut_.shippingPayment).get(ShippingPayment_.name);
                    break;
                    
                default:
                    path = root.get(sortField);
            }
            if (isAscending)
                order = cb.asc(path);
            else
                order = cb.desc(path);
        }
        query.orderBy(order);
        
        TypedQuery<DeliveryNoteOut> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getDeliveryNoteOutsCount(Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DeliveryNoteOut> root = query.from(DeliveryNoteOut.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, goodsDescription, shipmentReason, shippingPayment);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<DeliveryNoteOut> root, Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(DeliveryNoteCommon_.number), number));
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(DeliveryNoteCommon_.creation), start, end));
        
        //description of goods
        if (goodsDescription != null && !goodsDescription.isEmpty()) {
            Join<DeliveryNoteOut, GoodsDescription> goodsDescriptionRoot = root.join(DeliveryNoteOut_.goodsDescription);
            conditions.add(cb.like(cb.lower(goodsDescriptionRoot.get(GoodsDescription_.name)), "%" + String.valueOf(goodsDescription).toLowerCase() + "%"));
        }
        
        //reason for shipment
        if (shipmentReason != null && !shipmentReason.isEmpty()) {
            Join<DeliveryNoteOut, ShipmentReason> shipmentReasonRoot = root.join(DeliveryNoteOut_.shipmentReason);
            conditions.add(cb.like(cb.lower(shipmentReasonRoot.get(ShipmentReason_.name)), "%" + String.valueOf(shipmentReason).toLowerCase() + "%"));
        }
        
        //shipping payment
        if (shippingPayment != null && !shippingPayment.isEmpty()) {
            Join<DeliveryNoteOut, ShippingPayment> shippingPaymentRoot = root.join(DeliveryNoteOut_.shippingPayment);
            conditions.add(cb.like(cb.lower(shippingPaymentRoot.get(ShippingPayment_.name)), "%" + String.valueOf(shippingPayment).toLowerCase() + "%"));
        }
        
        return conditions;
    }
}