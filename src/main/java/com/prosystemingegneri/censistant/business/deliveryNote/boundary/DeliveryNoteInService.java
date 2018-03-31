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
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteIn_;
import com.prosystemingegneri.censistant.business.deliveryNote.entity.DeliveryNoteRow;
import com.prosystemingegneri.censistant.business.purchasing.boundary.PurchaseOrderService;
import com.prosystemingegneri.censistant.business.warehouse.boundary.HandledItemService;
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
public class DeliveryNoteInService implements Serializable{
    @PersistenceContext
    EntityManager em;
    
    @Inject
    PurchaseOrderService purchaseOrderService;
    @Inject
    HandledItemService handledItemService;
    
    public DeliveryNoteIn createNewDeliveryNoteIn() {
        return new DeliveryNoteIn(getNextNumber());
    }
    
    private Integer getNextNumber() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<DeliveryNoteIn> root = query.from(DeliveryNoteIn.class);
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
    
    public DeliveryNoteIn saveDeliveryNoteIn(DeliveryNoteIn deliveryNoteIn) {
        checkAndEvetuallySavePurchaseOrder(deliveryNoteIn);
        checkAndEvetuallySaveHandledItems(deliveryNoteIn);
        if (deliveryNoteIn.getId() == null)
            em.persist(deliveryNoteIn);
        else
            return em.merge(deliveryNoteIn);
        
        return deliveryNoteIn;
    }
    
    public DeliveryNoteIn readDeliveryNoteIn(Long id) {
        return em.find(DeliveryNoteIn.class, id);
    }
    
    public void deleteDeliveryNoteIn(Long id) {
        em.remove(readDeliveryNoteIn(id));
    }

    public List<DeliveryNoteIn> listDeliveryNoteIns(int first, int pageSize, String sortField, Boolean isAscending, Integer number, Date start, Date end, String numberStr) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeliveryNoteIn> query = cb.createQuery(DeliveryNoteIn.class);
        Root<DeliveryNoteIn> root = query.from(DeliveryNoteIn.class);
        CriteriaQuery<DeliveryNoteIn> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, numberStr);

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
                case "numberStr":
                    path = root.get(DeliveryNoteIn_.numberStr);
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
        
        TypedQuery<DeliveryNoteIn> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getDeliveryNoteInsCount(Integer number, Date start, Date end, String numberStr) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DeliveryNoteIn> root = query.from(DeliveryNoteIn.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, number, start, end, numberStr);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<DeliveryNoteIn> root, Integer number, Date start, Date end, String numberStr) {
        List<Predicate> conditions = new ArrayList<>();

        //number
        if (number != null)
            conditions.add(cb.equal(root.get(DeliveryNoteCommon_.number), number));
        
        //creation
        if (start != null && end != null)
            conditions.add(cb.between(root.<Date>get(DeliveryNoteCommon_.creation), start, end));
        
        //textual number (external number)
        if (numberStr != null && !numberStr.isEmpty())
            conditions.add(cb.like(cb.lower(root.get(DeliveryNoteIn_.numberStr)), "%" + String.valueOf(numberStr).toLowerCase() + "%"));
        
        return conditions;
    }

    /**
    *
    * Purchase order has been created at the same moment of delivery note's row, so it must be saved first
    */
    private void checkAndEvetuallySavePurchaseOrder(@NotNull DeliveryNoteIn deliveryNoteIn) {
        if (!deliveryNoteIn.getRows().isEmpty() && deliveryNoteIn.getRows().get(0).getPurchaseOrderRow().getPurchaseOrder().getId() == null)
            purchaseOrderService.savePurchaseOrder(deliveryNoteIn.getRows().get(0).getPurchaseOrderRow().getPurchaseOrder());
    }

    /**
    *
    * Handled items have been created at the same moment of delivery note's rows, so they must be saved first
    */
    private void checkAndEvetuallySaveHandledItems(@NotNull DeliveryNoteIn deliveryNoteIn) {
        for (DeliveryNoteRow row : deliveryNoteIn.getRows())
            if (row.getHandledItem().getId() == null)
                handledItemService.saveHandledItem(row.getHandledItem());
    }
}
