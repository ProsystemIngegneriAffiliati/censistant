/*
 * Copyright (C) 2018-2019 Prosystem Ingegneri Affiliati
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
package com.prosystemingegneri.censistant.business.sales.entity;

import com.prosystemingegneri.censistant.business.siteSurvey.entity.SiteSurveyReport;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author Davide Mainardi <ingmainardi at live.com>
 */
@StaticMetamodel(Offer.class)
public class Offer_ {
    public static volatile SingularAttribute<Offer, Date> creation;
    public static volatile SingularAttribute<Offer, Integer> number;
    public static volatile SingularAttribute<Offer, SiteSurveyReport> siteSurveyReport;
    public static volatile SingularAttribute<Offer, System> system;
    public static volatile SingularAttribute<Offer, JobOrder> jobOrder;
}
