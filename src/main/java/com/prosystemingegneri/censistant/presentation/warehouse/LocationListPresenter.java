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
package com.prosystemingegneri.censistant.presentation.warehouse;

import com.prosystemingegneri.censistant.business.warehouse.boundary.LocationService;
import com.prosystemingegneri.censistant.business.warehouse.control.LocationType;
import com.prosystemingegneri.censistant.business.warehouse.entity.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@Named
@ViewScoped
public class LocationListPresenter implements Serializable{
    @Inject
    LocationService service;
    
    private LocationLazyDataModel lazyLocations;
    
    private List<Location> locations;
    private LocationType locationType;
    
    @PostConstruct
    public void init() {
        lazyLocations = new LocationLazyDataModel(service);
        locations = new ArrayList<>();
    }
    
    public List<Location> completeLocations(String name) {
        locations = service.listLocations(0, 10, null, null, Arrays.asList(locationType), name);
        return locations;
    }
    
    /**
     * Useful only for 'omnifaces.ListConverter' used in 'p:autoComplete'
     * 
     * @param defaultLocation Needed when jsf page read not null autocomplete (when, for example, open an already saved entity)
     * @return 
     */
    public List<Location> getLocations(Location defaultLocation) {
        if (locations.isEmpty())
            locations.add(defaultLocation);
        return locations;
    }

    public LocationLazyDataModel getLazyLocations() {
        return lazyLocations;
    }

    public void setLazyLocations(LocationLazyDataModel lazyLocations) {
        this.lazyLocations = lazyLocations;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
    
}
