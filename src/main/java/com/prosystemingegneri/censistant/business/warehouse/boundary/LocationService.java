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
package com.prosystemingegneri.censistant.business.warehouse.boundary;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrderRow_;
import com.prosystemingegneri.censistant.business.purchasing.entity.PurchaseOrder_;
import com.prosystemingegneri.censistant.business.warehouse.control.LocationType;
import com.prosystemingegneri.censistant.business.warehouse.control.Stock;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem;
import com.prosystemingegneri.censistant.business.warehouse.entity.HandledItem_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location_;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierLocation;
import com.prosystemingegneri.censistant.business.warehouse.entity.SupplierLocation_;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse;
import com.prosystemingegneri.censistant.business.warehouse.entity.Warehouse_;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Stateless
public class LocationService implements Serializable {
    @PersistenceContext
    EntityManager em;
    
    public Location readLocation(Long id) {
        return em.find(Location.class, id);
    }
    
    public List<Location> listLocations(int first, int pageSize, String sortField, Boolean isAscending, LocationType locationType, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<Location> root = query.from(Location.class);
        CriteriaQuery<Location> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateConditions(cb, root, locationType, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        /*
        Order order = cb.asc(root.get(Warehouse_.name));
        if (isAscending != null && sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            switch (sortField) {
                case "name":
                    path = root.get(Warehouse_.name);
                    break;
                default:
                    path = root.get(sortField);
            }
            if (isAscending)
                order = cb.asc(path);
            else
                order = cb.desc(path);
        }
        query.orderBy(order);*/
        
        TypedQuery<Location> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getLocationsCount(LocationType locationType, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Location> root = query.from(Location.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateConditions(cb, root, locationType, name);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateConditions(CriteriaBuilder cb, Root<Location> root, LocationType locationType, String name) {
        List<Predicate> conditions = new ArrayList<>();

        //name
        if (name != null && !name.isEmpty()) {
            Root<Warehouse> warehouseRoot = cb.treat(root, Warehouse.class);
            Root<SupplierLocation> supplierLocationRoot = cb.treat(root, SupplierLocation.class);
            Join<SupplierLocation, CustomerSupplier> supplierRoot = supplierLocationRoot.join(SupplierLocation_.supplier, JoinType.LEFT);
            
            conditions.add(cb.or(
                    cb.like(cb.lower(warehouseRoot.get(Warehouse_.name)), "%" + name.toLowerCase() + "%"),
                    cb.like(cb.lower(supplierRoot.get(CustomerSupplier_.name)), "%" + name.toLowerCase() + "%")));
        }
        
        //location type
        if (locationType != null)
            conditions.add(cb.equal(root.get(Location_.type), locationType));
        
        return conditions;
    }
    
    public List<PurchaseOrderRow> listMovablePurchaseOrderRows(int first, int pageSize, String sortField, Boolean isAscending, Location location) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseOrderRow> query = cb.createQuery(PurchaseOrderRow.class);
        Root<PurchaseOrderRow> root = query.from(PurchaseOrderRow.class);
        CriteriaQuery<PurchaseOrderRow> select = query.select(root).distinct(true);
        
        List<Predicate> conditions = calculateMovablePurchaseOrderRowsConditions(cb, query.subquery(HandledItem.class), root, location);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));
        
        TypedQuery<PurchaseOrderRow> typedQuery = em.createQuery(select);
        if (pageSize > 0) {
            typedQuery.setMaxResults(pageSize);
            typedQuery.setFirstResult(first);
        }

        return typedQuery.getResultList();
    }
    
    public Long getMovablePurchaseOrderRowsCount(Location location) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PurchaseOrderRow> root = query.from(PurchaseOrderRow.class);
        CriteriaQuery<Long> select = query.select(cb.count(root));

        List<Predicate> conditions = calculateMovablePurchaseOrderRowsConditions(cb, query.subquery(HandledItem.class), root, location);

        if (!conditions.isEmpty())
            query.where(conditions.toArray(new Predicate[conditions.size()]));

        return em.createQuery(select).getSingleResult();
    }
    
    private List<Predicate> calculateMovablePurchaseOrderRowsConditions(CriteriaBuilder cb, Subquery<HandledItem> subquery, Root<PurchaseOrderRow> root, Location location) {
        List<Predicate> conditions = new ArrayList<>();
        
        //location
        if (location != null) {
            if (location instanceof SupplierLocation) {
                SupplierLocation tempSupplierLocation = (SupplierLocation) location;
                Join<PurchaseOrderRow, PurchaseOrder> purchaseOrderRoot = root.join(PurchaseOrderRow_.purchaseOrder);
                conditions.add(cb.equal(purchaseOrderRoot.get(PurchaseOrder_.supplier), tempSupplierLocation.getSupplier()));
                
                Path<Object> path = root.get(PurchaseOrderRow_.id.getName()); // field to map with sub-query
                Root<HandledItem> subRoot = subquery.from(HandledItem.class);
                subquery.select(subRoot.get(HandledItem_.purchaseOrderRow.getName()).get(PurchaseOrderRow_.id.getName())); // field to map with main-query
                conditions.add(cb.not(cb.in(path).value(subquery)));
            }
            if (location instanceof Warehouse)
                ;
        }
        
        return conditions;
    }
    
    public List<Stock> listStock(int first, int pageSize) {
        List<Stock> result = new ArrayList<>();
        Query query = queryListStock();
        
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        
        List<Object[]> rowList = query.getResultList();
        if (rowList != null && !rowList.isEmpty())
            for (Object[] objects : rowList) {
                Location location = em.find(Location.class, (Long) objects[0]);
                PurchaseOrderRow purchaseOrderRow = em.find(PurchaseOrderRow.class, (Long) objects[1]);
                result.add(new Stock(location, purchaseOrderRow, ((BigDecimal) objects[2]).intValue()));
            }
        
        return result;
    }
    
    public Long getStockCount() {
        /*Query query = queryListStock();
        
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            return new Long(0);
        }
        catch (NonUniqueResultException e) {
            List<Object> list = query.getResultList();
            return new Long(list.size());
        }*/
        return new Long(5);
    }
    
    public Query queryListStock() {
        
        /*String queryString ="SELECT " +
                "COALESCE(fromSupplier.location, fromLocation.location, toLocation.location) AS location, " +
                "COALESCE(fromSupplier.purchaseorderrow, fromLocation.purchaseorderrow, toLocation.purchaseorderrow) AS purchaseorderrow, " +
                "SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(toLocation.qty, 0)) AS qty " +
                "FROM " +
                "( " +
                    "SELECT location, purchaseorderrow, SUM(purchaseorderrow.quantity) AS qty " +
                    "FROM purchaseorderrow " +
                    "JOIN purchaseorder ON purchaseorderrow.purchaseorder_id = purchaseorder.id " +
                    "JOIN supplierlocation ON purchaseorder.supplier_id = supplierlocation.supplier_id " +
                    "JOIN location ON supplierlocation.id = location.id " +
                    "GROUP BY location, purchaseorderrow " +
                ") AS fromSupplier " +
                "FULL JOIN " +
                "( " +
                    "SELECT location, purchaseorderrow, SUM(hiFrom.quantity) as qty " +
                    "FROM handleditem hiFrom " +
                    "JOIN location ON hiFrom.fromlocation_id = location.id " +
                    "JOIN purchaseorderrow ON hiFrom.purchaseorderrow_id = purchaseorderrow.id " +
                    "GROUP BY location, purchaseorderrow " +
                ") AS fromLocation " +
                "ON fromSupplier.location = fromLocation.location AND fromSupplier.purchaseorderrow = fromLocation.purchaseorderrow " +
                "FULL JOIN " +
                "( " +
                    "SELECT location, purchaseorderrow, SUM(hiTo.quantity) as qty " +
                    "FROM handleditem hiTo " +
                    "JOIN location ON hiTo.tolocation_id = location.id " +
                    "JOIN purchaseorderrow ON hiTo.purchaseorderrow_id = purchaseorderrow.id " +
                    "GROUP BY location, purchaseorderrow " +
                ") AS toLocation " +
                "ON fromLocation.location = toLocation.location AND fromSupplier.location = toLocation.location AND fromLocation.purchaseorderrow = toLocation.purchaseorderrow AND fromSupplier.purchaseorderrow = toLocation.purchaseorderrow " +
                "GROUP BY COALESCE(fromSupplier.location, fromLocation.location, toLocation.location), COALESCE(fromSupplier.purchaseorderrow, fromLocation.purchaseorderrow, toLocation.purchaseorderrow) " +
                "HAVING SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(toLocation.qty, 0)) > 0 " +
                "ORDER BY location";*/
        
        String queryString = "SELECT COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc) AS loc, COALESCE(fromSupplier.idpurchaseorderrow, fromLocation.idpurchaseorderrow, inLocation.idpurchaseorderrow) AS idpurchaseorderrow, SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) AS qty " +
                "FROM " +
                "( " +
                    "SELECT supplierlocation.id AS loc, purchaseorderrow.id AS idpurchaseorderrow, SUM(purchaseorderrow.quantity) AS qty " +
                    "FROM purchaseorderrow " +
                    "JOIN purchaseorder ON purchaseorderrow.purchaseorder_id = purchaseorder.id " +
                    "JOIN supplierlocation ON purchaseorder.supplier_id = supplierlocation.supplier_id " +
                    "GROUP BY supplierlocation.id, purchaseorderrow.id " +
                ") AS fromSupplier " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiFrom.fromlocation_id as loc, hiFrom.purchaseorderrow_id as idpurchaseorderrow, SUM(hiFrom.quantity) as qty " +
                    "FROM handleditem hiFrom " +
                    "GROUP BY hiFrom.fromlocation_id, hiFrom.purchaseorderrow_id " +
                ") AS fromLocation " +
                "ON fromSupplier.loc = fromLocation.loc AND fromSupplier.idpurchaseorderrow = fromLocation.idpurchaseorderrow " +
                "FULL JOIN " +
                "( " +
                    "SELECT hiTo.tolocation_id as loc, hiTo.purchaseorderrow_id as idpurchaseorderrow, SUM(hiTo.quantity) as qty " +
                    "FROM handleditem hiTo " +
                    "GROUP BY hiTo.tolocation_id, hiTo.purchaseorderrow_id " +
                ") AS inLocation " +
                "ON fromLocation.loc = inLocation.loc AND fromSupplier.loc = inLocation.loc AND fromLocation.idpurchaseorderrow = inLocation.idpurchaseorderrow AND fromSupplier.idpurchaseorderrow = inLocation.idpurchaseorderrow " +
                "GROUP BY COALESCE(fromSupplier.loc, fromLocation.loc, inLocation.loc), COALESCE(fromSupplier.idpurchaseorderrow, fromLocation.idpurchaseorderrow, inLocation.idpurchaseorderrow) " +
                "HAVING SUM(COALESCE(fromSupplier.qty, 0) - COALESCE(fromLocation.qty, 0) + COALESCE(inLocation.qty, 0)) > 0 " +
                "ORDER BY loc";
        
        Query query = em.createNativeQuery(queryString);
        
        return query;
    }
}
