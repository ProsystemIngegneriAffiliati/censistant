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
package com.prosystemingegneri.censistant.business.deliveryNote.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteCommon_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteOut_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.GoodsDescription_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShipmentReason_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.ShippingPayment_;
import com.prosystemingegneri.censistant.business.production.entity.System;
import com.prosystemingegneri.censistant.business.production.entity.System_;
import com.prosystemingegneri.censistant.business.sales.entity.Offer;
import com.prosystemingegneri.censistant.business.sales.entity.Offer_;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport_;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierPlantLocation_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class DeliveryNoteOutService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    HandledItemService handledItemService;
    
    public DeliveryNoteOut createNewDeliveryNoteOut() {
        return new DeliveryNoteOut(getNextNumber());
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<DeliveryNoteOut> root = query.from(DeliveryNoteOut.class);
        query.select(cb.greatest(root.get(DeliveryNoteCommon_.number)));
        
        List<Predicate> conditions = new ArrayList<>();
        
        GregorianCalendar dateStart = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 0, 01);
        GregorianCalendar dateEnd = new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR), 11, 31);
        
        conditions.add(cb.between(root.<Date>get(DeliveryNoteCommon_.creation), dateStart.getTime(), dateEnd.getTime()));

        if (!conditions.isEmpty()) {
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        }
        
        Integer result;
        try {
            result = em.createQuery(query).getSingleResult();
            if (result != null)
                result++;
            else
                result = 1;
        } catch (NoResultException e) {
            result = 1;
        }
        
	return result;
    }
    
    public DeliveryNoteOut saveDeliveryNoteOut(DeliveryNoteOut deliveryNoteOut) {
        updateHandledItems(deliveryNoteOut);
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

    public List<DeliveryNoteOut> listDeliveryNoteOuts(int first, int pageSize, String sortField, Boolean isAscending, Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment, String customerSupplierNamePlantNameAddress) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeliveryNoteOut> query = cb.createQuery(DeliveryNoteOut.class);
        Root<DeliveryNoteOut> root = query.from(DeliveryNoteOut.class);
        CriteriaQuery<DeliveryNoteOut> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, goodsDescription, shipmentReason, shippingPayment, customerSupplierNamePlantNameAddress);

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
    
    public Long getDeliveryNoteOutsCount(Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment, String customerSupplierNamePlantNameAddress) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DeliveryNoteOut> root = query.from(DeliveryNoteOut.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, goodsDescription, shipmentReason, shippingPayment, customerSupplierNamePlantNameAddress);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<DeliveryNoteOut> root, Integer number, Date start, Date end, String goodsDescription, String shipmentReason, String shippingPayment, String customerSupplierNamePlantNameAddress) {
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
        
        //Customer (or supplier) name or plant name or plant address
        if (customerSupplierNamePlantNameAddress != null && !customerSupplierNamePlantNameAddress.isEmpty()) {
            Join<DeliveryNoteOut, SupplierPlantLocation> supplierPlantLocationRoot = cb.treat(root.join(DeliveryNoteOut_.location), SupplierPlantLocation.class);
            if (supplierPlantLocationRoot != null) {
                Join<SupplierPlantLocation, Plant> supplierPlantRoot = supplierPlantLocationRoot.join(SupplierPlantLocation_.plant);
                Join<Plant, CustomerSupplier> supplierRoot = supplierPlantRoot.join(Plant_.customerSupplier);
                conditions.add(
                        cb.or(
                                cb.like(cb.lower(supplierPlantRoot.get(Plant_.name)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%"),
                                cb.like(cb.lower(supplierPlantRoot.get(Plant_.address)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%"),
                                cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%")
                        )
                );
            }

            Join<DeliveryNoteOut, System> systemRoot = cb.treat(root.join(DeliveryNoteOut_.location), System.class);
            if (systemRoot != null) {
                Join<com.prosystemingegneri.censistant.business.production.entity.System, Offer> offersRoot = systemRoot.join(System_.offers, JoinType.LEFT);
                Join<Offer, SiteSurveyReport> siteSurveyReportRoot = offersRoot.join(Offer_.siteSurveyReport);
                Join<SiteSurveyReport, Plant> plantRoot = siteSurveyReportRoot.join(SiteSurveyReport_.plant);
                Join<Plant, CustomerSupplier> customerRoot = plantRoot.join(Plant_.customerSupplier);
                conditions.add(
                        cb.or(
                                cb.like(cb.lower(plantRoot.get(Plant_.name)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%"),
                                cb.like(cb.lower(plantRoot.get(Plant_.address)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%"),
                                cb.like(cb.lower(customerRoot.get(CustomerSupplier_.name)), "%" + String.valueOf(customerSupplierNamePlantNameAddress).toLowerCase() + "%")
                        )
                );
            }
        }
        
        return conditions;
    }
    
    /**
    *
    * Handled items have been created at the same moment of delivery note's rows, so they must be saved first
    */
    private void updateHandledItems(@NotNull DeliveryNoteOut deliveryNote) {
        for (DeliveryNoteRow row : deliveryNote.getRows())
            handledItemService.saveHandledItem(row.getHandledItem());
    }
}
