/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.kernel;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAgency;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAttribute;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownLocation;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownProduct;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownRelationship;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * Repository of immutable kernal rules
 * 
 * @author hhildebrand
 * 
 */
public class KernelImpl implements Kernel {

    private final Agency       agency;
    private final Agency       anyAgency;
    private final Attribute    anyAttribute;
    private final Location     anyLocation;
    private final Product      anyProduct;
    private final Relationship anyRelationship;

    private final Attribute    attribute;
    private final Relationship contains;
    private final Agency       core;
    private final Agency       coreAnimationSoftware;
    private final Agency       coreModel;
    private final Agency       coreUser;

    private final Relationship developed;
    private final Relationship developedBy;
    private final Relationship equals;
    private final Relationship formerMemberOf;
    private final Relationship greaterThan;

    private final Relationship greaterThanOrEqual;
    private final Relationship hadMember;
    private final Relationship hasException;
    private final Relationship hasHead;
    private final Relationship hasMember;
    private final Relationship hasVersion;
    private final Relationship headOf;
    private final Relationship includes;
    private final Agency       inverseSoftware;
    private final Relationship inWorkspace;
    private final Relationship isA;
    private final Relationship isContainedIn;
    private final Relationship isExceptionTo;

    private final Relationship isLocationOf;
    private final Relationship lessThan;
    private final Relationship lessThanOrEqual;
    private final Location     location;
    private final Attribute    loginAttribute;
    private final Relationship mapsToLocation;
    private final Relationship memberOf;
    private final Agency       notApplicableAgency;
    private final Attribute    notApplicableAttribute;
    private final Location     notApplicableLocation;
    private final Product      notApplicableProduct;
    private final Relationship notApplicableRelationship;
    private final Agency       originalAgency;
    private final Attribute    originalAttribute;
    private final Location     originalLocation;
    private final Product      originalProduct;
    private final Relationship ownedBy;
    private final Relationship owns;
    private final Attribute    passwordHashAttribute;
    private final Product      product;
    private final Agency       propagationSoftware;
    private final Relationship prototype;
    private final Relationship prototypeOf;
    private final Agency       sameAgency;
    private final Location     sameLocation;
    private final Product      sameProduct;
    private final Relationship sameRelationship;
    private final Agency       specialSystemAgency;
    private final Agency       superUser;
    private final StatusCode   unset;
    private final Relationship versionOf;
    private final Product      workspace;

    private final Relationship workspaceOf;

    public KernelImpl(EntityManager em) {

        attribute = find(em, WellKnownAttribute.ATTRIBUTE);
        anyAttribute = find(em, WellKnownAttribute.ANY);
        notApplicableAttribute = find(em, WellKnownAttribute.NOT_APPLICABLE);
        originalAttribute = find(em, WellKnownAttribute.ORIGINAL);
        loginAttribute = find(em, WellKnownAttribute.LOGIN);
        passwordHashAttribute = find(em, WellKnownAttribute.PASSWORD_HASH);

        product = find(em, WellKnownProduct.ENTITY);
        anyProduct = find(em, WellKnownProduct.ANY);
        originalProduct = find(em, WellKnownProduct.ORIGINAL);
        sameProduct = find(em, WellKnownProduct.SAME);
        notApplicableProduct = find(em, WellKnownProduct.NOT_APPLICABLE);
        workspace = find(em, WellKnownProduct.WORKSPACE);

        location = find(em, WellKnownLocation.LOCATION);
        anyLocation = find(em, WellKnownLocation.ANY);
        originalLocation = find(em, WellKnownLocation.ORIGINAL);
        notApplicableLocation = find(em, WellKnownLocation.NOT_APPLICABLE);
        sameLocation = find(em, WellKnownLocation.SAME);

        coreUser = find(em, WellKnownAgency.CORE_USER);
        agency = find(em, WellKnownAgency.AGENCY);
        anyAgency = find(em, WellKnownAgency.ANY);
        originalAgency = find(em, WellKnownAgency.ORIGINAL);
        core = find(em, WellKnownAgency.CORE);
        coreAnimationSoftware = find(em,
                                     WellKnownAgency.CORE_ANIMATION_SOFTWARE);
        propagationSoftware = find(em, WellKnownAgency.PROPAGATION_SOFTWARE);
        specialSystemAgency = find(em, WellKnownAgency.SPECIAL_SYSTEM_AGENCY);
        coreModel = find(em, WellKnownAgency.CORE_MODEL);
        superUser = find(em, WellKnownAgency.SUPER_USER);
        inverseSoftware = find(em, WellKnownAgency.INVERSE_SOFTWARE);
        sameAgency = find(em, WellKnownAgency.SAME);
        notApplicableAgency = find(em, WellKnownAgency.NOT_APPLICABLE);

        anyRelationship = find(em, WellKnownRelationship.ANY);
        sameRelationship = find(em, WellKnownRelationship.SAME);
        isContainedIn = find(em, WellKnownRelationship.IS_CONTAINED_IN);
        contains = find(em, WellKnownRelationship.CONTAINS);
        isA = find(em, WellKnownRelationship.IS_A);
        includes = find(em, WellKnownRelationship.INCLUDES);
        hasException = find(em, WellKnownRelationship.HAS_EXCEPTION);
        isExceptionTo = find(em, WellKnownRelationship.IS_EXCEPTION_TO);
        isLocationOf = find(em, WellKnownRelationship.IS_LOCATION_OF);
        mapsToLocation = find(em, WellKnownRelationship.MAPS_TO_LOCATION);
        prototype = find(em, WellKnownRelationship.PROTOTYPE);
        prototypeOf = find(em, WellKnownRelationship.PROTOTYPE_OF);
        greaterThan = find(em, WellKnownRelationship.GREATER_THAN);
        lessThan = find(em, WellKnownRelationship.LESS_THAN);
        equals = find(em, WellKnownRelationship.EQUALS);
        lessThanOrEqual = find(em, WellKnownRelationship.LESS_THAN_OR_EQUAL);
        greaterThanOrEqual = find(em,
                                  WellKnownRelationship.GREATER_THAN_OR_EQUAL);
        developed = find(em, WellKnownRelationship.DEVELOPED);
        developedBy = find(em, WellKnownRelationship.DEVELOPED_BY);
        versionOf = find(em, WellKnownRelationship.VERSION_OF);
        hasVersion = find(em, WellKnownRelationship.HAS_VERSION);
        hasMember = find(em, WellKnownRelationship.HAS_MEMBER);
        memberOf = find(em, WellKnownRelationship.MEMBER_OF);
        headOf = find(em, WellKnownRelationship.HEAD_OF);
        hasHead = find(em, WellKnownRelationship.HAS_HEAD);
        hadMember = find(em, WellKnownRelationship.HAD_MEMBER);
        formerMemberOf = find(em, WellKnownRelationship.FORMER_MEMBER_OF);
        notApplicableRelationship = find(em,
                                         WellKnownRelationship.NOT_APPLICABLE);
        ownedBy = find(em, WellKnownRelationship.OWNED_BY);
        owns = find(em, WellKnownRelationship.OWNS);
        inWorkspace = find(em, WellKnownRelationship.IN_WORKSPACE);
        workspaceOf = find(em, WellKnownRelationship.WORKSPACE_OF);

        unset = find(em, WellKnownStatusCode.UNSET);

    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAgency()
     */
    @Override
    public Agency getAgency() {
        return agency;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAnyAgency()
     */
    @Override
    public Agency getAnyAgency() {
        return anyAgency;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAnyAttribute()
     */
    @Override
    public Attribute getAnyAttribute() {
        return anyAttribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAnyLocation()
     */
    @Override
    public Location getAnyLocation() {
        return anyLocation;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAnyProduct()
     */
    @Override
    public Product getAnyProduct() {
        return anyProduct;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAnyRelationship()
     */
    @Override
    public Relationship getAnyRelationship() {
        return anyRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getAttribute()
     */
    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getContains()
     */
    @Override
    public Relationship getContains() {
        return contains;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getCore()
     */
    @Override
    public Agency getCore() {
        return core;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getCoreAnimationProcedure()
     */
    @Override
    public Agency getCoreAnimationSoftware() {
        return coreAnimationSoftware;
    }

    @Override
    public Agency getCoreModel() {
        return coreModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getCoreUser()
     */
    @Override
    public Agency getCoreUser() {
        return coreUser;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getDeveloped()
     */
    @Override
    public Relationship getDeveloped() {
        return developed;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getDevelopedBy()
     */
    @Override
    public Relationship getDevelopedBy() {
        return developedBy;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getEquals()
     */
    @Override
    public Relationship getEquals() {
        return equals;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getFormerMemberOf()
     */
    @Override
    public Relationship getFormerMemberOf() {
        return formerMemberOf;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getGreaterThan()
     */
    @Override
    public Relationship getGreaterThan() {
        return greaterThan;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getGreaterThanOrEqual()
     */
    @Override
    public Relationship getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHadMember()
     */
    @Override
    public Relationship getHadMember() {
        return hadMember;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHasException()
     */
    @Override
    public Relationship getHasException() {
        return hasException;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHasHead()
     */
    @Override
    public Relationship getHasHead() {
        return hasHead;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHasMember()
     */
    @Override
    public Relationship getHasMember() {
        return hasMember;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHasVersion()
     */
    @Override
    public Relationship getHasVersion() {
        return hasVersion;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getHeadOf()
     */
    @Override
    public Relationship getHeadOf() {
        return headOf;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getIncludes()
     */
    @Override
    public Relationship getIncludes() {
        return includes;
    }

    @Override
    public Agency getInverseSoftware() {
        return inverseSoftware;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.kernel.Kernel#getInWorkspace()
     */
    @Override
    public Relationship getInWorkspace() {
        return inWorkspace;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getIsA()
     */
    @Override
    public Relationship getIsA() {
        return isA;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getIsContainedIn()
     */
    @Override
    public Relationship getIsContainedIn() {
        return isContainedIn;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getIsExceptionTo()
     */
    @Override
    public Relationship getIsExceptionTo() {
        return isExceptionTo;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getIsLocationOf()
     */
    @Override
    public Relationship getIsLocationOf() {
        return isLocationOf;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getLessThan()
     */
    @Override
    public Relationship getLessThan() {
        return lessThan;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getLessThanOrEqual()
     */
    @Override
    public Relationship getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getLocation()
     */
    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Attribute getLoginAttribute() {
        return loginAttribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getMapsToLocation()
     */
    @Override
    public Relationship getMapsToLocation() {
        return mapsToLocation;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getMemberOf()
     */
    @Override
    public Relationship getMemberOf() {
        return memberOf;
    }

    /**
     * @return the notApplicableAgency
     */
    @Override
    public Agency getNotApplicableAgency() {
        return notApplicableAgency;
    }

    /**
     * @return the notApplicableAttribute
     */
    @Override
    public Attribute getNotApplicableAttribute() {
        return notApplicableAttribute;
    }

    /**
     * @return the notApplicableLocation
     */
    @Override
    public Location getNotApplicableLocation() {
        return notApplicableLocation;
    }

    /**
     * @return the notApplicableProduct
     */
    @Override
    public Product getNotApplicableProduct() {
        return notApplicableProduct;
    }

    /**
     * @return the notApplicableRelationship
     */
    @Override
    public Relationship getNotApplicableRelationship() {
        return notApplicableRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getOriginalAgency()
     */
    @Override
    public Agency getOriginalAgency() {
        return originalAgency;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getOriginalAttribute()
     */
    @Override
    public Attribute getOriginalAttribute() {
        return originalAttribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getOriginalLocation()
     */
    @Override
    public Location getOriginalLocation() {
        return originalLocation;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getOriginalProduct()
     */
    @Override
    public Product getOriginalProduct() {
        return originalProduct;
    }

    /**
     * @return the ownedBy
     */
    @Override
    public Relationship getOwnedBy() {
        return ownedBy;
    }

    /**
     * @return the owns
     */
    @Override
    public Relationship getOwns() {
        return owns;
    }

    @Override
    public Attribute getPasswordHashAttribute() {
        return passwordHashAttribute;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getProduct()
     */
    @Override
    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getPropagationSoftware()
     */
    @Override
    public Agency getPropagationSoftware() {
        return propagationSoftware;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getPrototype()
     */
    @Override
    public Relationship getPrototype() {
        return prototype;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getPrototypeOf()
     */
    @Override
    public Relationship getPrototypeOf() {
        return prototypeOf;
    }

    /**
     * @return the sameAgency
     */
    @Override
    public Agency getSameAgency() {
        return sameAgency;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Kernel#getSameLocation()
     */
    @Override
    public Location getSameLocation() {
        return sameLocation;
    }

    /**
     * @return the sameProduct
     */
    @Override
    public Product getSameProduct() {
        return sameProduct;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getSameRelationship()
     */
    @Override
    public Relationship getSameRelationship() {
        return sameRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getSpecialSystemAgency()
     */
    @Override
    public Agency getSpecialSystemAgency() {
        return specialSystemAgency;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Kernel#getSuperUser()
     */
    @Override
    public Agency getSuperUser() {
        return superUser;
    }

    /* (non-Javadoc) 
     * @see com.hellblazer.CoRE.meta.Kernel#getUnset()
     */
    @Override
    public StatusCode getUnset() {
        return unset;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.models.Kernel#getVersionOf()
     */
    @Override
    public Relationship getVersionOf() {
        return versionOf;
    }

    @Override
    public Product getWorkspace() {
        return workspace;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.kernel.Kernel#getWorkspaceOf()
     */
    @Override
    public Relationship getWorkspaceOf() {
        return workspaceOf;
    }

    /**
     * 
     * @param wko
     * @return the {@link Agency} corresponding to the well known object
     */
    Agency find(EntityManager em, WellKnownAgency wko) {
        return em.find(Agency.class, wko.id());
    }

    /**
     * 
     * @param wko
     * @return the {@link Attribute} corresponding to the well known object
     */
    Attribute find(EntityManager em, WellKnownAttribute wko) {
        return em.find(Attribute.class, wko.id());
    }

    /**
     * 
     * @param wko
     * @return the {@link Location} corresponding to the well known object
     */
    Location find(EntityManager em, WellKnownLocation wko) {
        return em.find(Location.class, wko.id());
    }

    /**
     * 
     * @param wko
     * @return the {@link Product} corresponding to the well known object
     */
    Product find(EntityManager em, WellKnownProduct wko) {
        return em.find(Product.class, wko.id());
    }

    /**
     * 
     * @param wko
     * @return the {@link Relationship} corresponding to the well known object
     */
    Relationship find(EntityManager em, WellKnownRelationship wko) {
        return em.find(Relationship.class, wko.id());
    }

    /**
     * 
     * @param wko
     * @return the {@link StatusCode} corresponding to the well known object
     */
    StatusCode find(EntityManager em, WellKnownStatusCode wko) {
        return em.find(StatusCode.class, wko.id());
    }

}
