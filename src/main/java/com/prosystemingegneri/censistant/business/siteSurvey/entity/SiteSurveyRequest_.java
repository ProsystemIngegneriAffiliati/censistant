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
package com.prosystemingegneri.censistant.business.siteSurvey.entity;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.CustomerSupplier;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@StaticMetamodel(SiteSurveyRequest.class)
public class SiteSurveyRequest_ {
    public static volatile SingularAttribute<SiteSurveyRequest, Long> id;
    public static volatile SingularAttribute<SiteSurveyRequest, Integer> number;
    public static volatile SingularAttribute<SiteSurveyRequest, Date> creation;
    public static volatile SingularAttribute<SiteSurveyRequest, CustomerSupplier> customer;
    public static volatile SingularAttribute<SiteSurveyRequest, SystemType> systemType;
    public static volatile SingularAttribute<SiteSurveyRequest, Boolean> isInfo;
    public static volatile SingularAttribute<SiteSurveyRequest, String> notes;
}
