/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.jsonld.resources;

import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContext;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/context")
@Produces({ "application/json", "text/json" })
public class FacetContextResource extends TransactionalResource {

    @Context
    private UriInfo uriInfo;

    public FacetContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetContextResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @Path("agency/{classifier}/{classification}")
    @GET
    public FacetContext<Agency, AgencyNetwork> getAgency(@PathParam("classifier") String relationship,
                                                         @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getAgencyModel());
    }

    @Path("attribute/{classifier}/{classification}")
    @GET
    public FacetContext<Attribute, AttributeNetwork> getAttribute(@PathParam("classifier") String relationship,
                                                                  @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getAttributeModel());
    }

    @Path("interval/{classifier}/{classification}")
    @GET
    public FacetContext<Interval, IntervalNetwork> getInterval(@PathParam("classifier") String relationship,
                                                               @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getIntervalModel());
    }

    @Path("location/{classifier}/{classification}")
    @GET
    public FacetContext<Location, LocationNetwork> getLocation(@PathParam("classifier") String relationship,
                                                               @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getLocationModel());
    }

    @Path("product/{classifier}/{classification}")
    @GET
    public FacetContext<Product, ProductNetwork> getProduct(@PathParam("classifier") String relationship,
                                                            @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getProductModel());
    }

    @Path("relationship/{classifier}/{classification}")
    @GET
    public FacetContext<Relationship, RelationshipNetwork> getRelationship(@PathParam("classifier") String relationship,
                                                                           @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getRelationshipModel());
    }

    @Path("statusCode/{classifier}/{classification}")
    @GET
    public FacetContext<StatusCode, StatusCodeNetwork> getStatusCode(@PathParam("classifier") String relationship,
                                                                     @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getStatusCodeModel());
    }

    @Path("unit/{classifier}/{classification}")
    @GET
    public FacetContext<Unit, UnitNetwork> getUnit(@PathParam("classifier") String relationship,
                                                   @PathParam("classification") String ruleform) {
        return createContext(relationship, ruleform,
                             readOnlyModel.getUnitModel());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> createContext(String relationship,
                                                                                                                                                               String ruleform,
                                                                                                                                                               NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        UUID classifier = toUuid(relationship);
        UUID classification = toUuid(ruleform);
        try {
            return new FacetContext(networkedModel.getAspect(classifier,
                                                             classification),
                                    readOnlyModel, uriInfo);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }
}
