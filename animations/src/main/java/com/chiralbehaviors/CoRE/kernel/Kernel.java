/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.kernel;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public interface Kernel {
    Agency getAgency();

    Agency getAnyAgency();

    Attribute getAnyAttribute();

    Interval getAnyInterval();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    StatusCode getAnyStatusCode();

    Unit getAnyUnit();

    Attribute getAttribute();

    Relationship getContains();

    Agency getCopyAgency();

    Attribute getCopyAttribute();

    Interval getCopyInterval();

    Location getCopyLocation();

    Product getCopyProduct();

    Relationship getCopyRelationship();

    StatusCode getCopyStatusCode();

    Unit getCopyUnit();

    Agency getCore();

    Agency getCoreAnimationSoftware();

    Agency getCoreModel();

    Agency getCoreUser();

    Unit getDays();

    Relationship getDeveloped();

    Relationship getDevelopedBy();

    Relationship getEquals();

    Relationship getFormerMemberOf();

    Relationship getGreaterThan();

    Relationship getGreaterThanOrEqual();

    Relationship getHadMember();

    Relationship getHasException();

    Relationship getHasHead();

    Relationship getHasMember();

    Relationship getHasVersion();

    Relationship getHeadOf();

    Unit getHours();

    Relationship getIncludes();

    Agency getInverseSoftware();

    Relationship getInWorkspace();

    Relationship getIsA();

    Relationship getIsContainedIn();

    Relationship getIsExceptionTo();

    Relationship getIsLocationOf();

    Product getKernelWorkspace();

    Relationship getLessThan();

    Relationship getLessThanOrEqual();

    Location getLocation();

    Attribute getLoginAttribute();

    Relationship getMapsToLocation();

    Relationship getMemberOf();

    Unit getMicroseconds();

    Unit getMilliseconds();

    Unit getMinutes();

    Unit getNanoseconds();

    Agency getNotApplicableAgency();

    Attribute getNotApplicableAttribute();

    Interval getNotApplicableInterval();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    StatusCode getNotApplicableStatusCode();

    Unit getNotApplicableUnit();

    Relationship getOwnedBy();

    Relationship getOwns();

    Attribute getPasswordHashAttribute();

    Product getProduct();

    Agency getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    AgencyNetwork getRootAgencyNetwork();

    AttributeNetwork getRootAttributeNetwork();

    IntervalNetwork getRootIntervalNetwork();

    LocationNetwork getRootLocationNetwork();

    ProductNetwork getRootProductNetwork();

    RelationshipNetwork getRootRelationshipNetwork();

    StatusCodeNetwork getRootStatusCodeNetwork();

    UnitNetwork getRootUnitNetwork();

    Agency getSameAgency();

    Attribute getSameAttribute();

    Interval getSameInterval();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    StatusCode getSameStatusCode();

    Unit getSameUnit();

    Unit getSeconds();

    Agency getSpecialSystemAgency();

    Agency getSuperUser();

    StatusCode getUnset();

    Unit getUnsetUnit();

    Relationship getVersionOf();

    Product getWorkspace();

    Relationship getWorkspaceOf();

}
