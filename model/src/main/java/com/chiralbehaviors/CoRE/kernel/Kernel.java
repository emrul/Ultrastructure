/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.kernel;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.coordinate.CoordinateNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 * 
 */
public interface Kernel {

    Agency getAgency();

    Agency getAnyAgency();

    Attribute getAnyAttribute();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    Attribute getAttribute();

    Relationship getContains();

    Agency getCore();

    Agency getCoreAnimationSoftware();

    Agency getCoreModel();

    Agency getCoreUser();

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

    Relationship getIncludes();

    Agency getInverseSoftware();

    Relationship getInWorkspace();

    Relationship getIsA();

    Relationship getIsContainedIn();

    Relationship getIsExceptionTo();

    Relationship getIsLocationOf();

    Relationship getLessThan();

    Relationship getLessThanOrEqual();

    Location getLocation();

    Attribute getLoginAttribute();

    Relationship getMapsToLocation();

    Relationship getMemberOf();

    Agency getNotApplicableAgency();

    Attribute getNotApplicableAttribute();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    Agency getOriginalAgency();

    Attribute getOriginalAttribute();

    Location getOriginalLocation();

    Product getOriginalProduct();

    Relationship getOwnedBy();

    Relationship getOwns();

    Attribute getPasswordHashAttribute();

    Product getProduct();

    Agency getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    AgencyNetwork getRootAgencyNetwork();

    AttributeNetwork getRootAttributeNetwork();

    CoordinateNetwork getRootCoordinateNetwork();

    IntervalNetwork getRootIntervalNetwork();

    LocationNetwork getRootLocationNetwork();

    ProductNetwork getRootProductNetwork();

    RelationshipNetwork getRootRelationshipNetwork();

    StatusCodeNetwork getRootStatusCodeNetwork();

    UnitNetwork getRootUnitNetwork();

    Agency getSameAgency();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    Agency getSpecialSystemAgency();

    Agency getSuperUser();

    StatusCode getUnset();

    Relationship getVersionOf();

    Product getWorkspace();

    Relationship getWorkspaceOf();

}