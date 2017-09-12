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
package com.prosystemingegneri.censistant.business.siteSurvey.entity;

import com.prosystemingegneri.censistant.business.customerSupplier.entity.Plant;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi@live.com>
 */
@StaticMetamodel(SiteSurveyReport.class)
public class SiteSurveyReport_ {
    public static volatile SingularAttribute<SiteSurveyReport, Integer> number;
    public static volatile SingularAttribute<SiteSurveyReport, Date> expected;
    public static volatile SingularAttribute<SiteSurveyReport, Date> actual;
    public static volatile SingularAttribute<SiteSurveyReport, SiteSurveyRequest> request;
    public static volatile SingularAttribute<SiteSurveyReport, Plant> plant;
    public static volatile SingularAttribute<SiteSurveyReport, Worker> seller;
}
