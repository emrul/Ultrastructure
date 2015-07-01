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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/node")
@Produces({ "application/json", "text/json" })
public class FacetNodeResource extends TransactionalResource {

    @Context
    private UriInfo uriInfo;

    public FacetNodeResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetNodeResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public List<FacetNode<?, ?>> getAllInstances(@PathParam("ruleform-type") String ruleformType,
                                                 @PathParam("classifier") String relationship,
                                                 @PathParam("classification") String ruleform) {
        Aspect aspect = getAspect(ruleformType, relationship, ruleform);
        return getFacetInstances(aspect);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("{ruleform-type}/{instance}/{classifier}/{classification}")
    @GET
    public FacetNode<?, ?> getInstance(@PathParam("ruleform-type") String ruleformType,
                                       @PathParam("instance") String facetInstance,
                                       @PathParam("classifier") String relationship,
                                       @PathParam("classification") String ruleform) {
        Aspect aspect = getAspect(ruleformType, relationship, ruleform);
        return createFacet(facetInstance, aspect);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetNode<RuleForm, Network> createFacet(String facetInstance,
                                                                                                                                                          Aspect<RuleForm> aspect) {
        UUID existential = toUuid(facetInstance);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        RuleForm instance = networkedModel.find(existential);
        if (instance == null) {
            throw new WebApplicationException(String.format("node %s is not found [%s:%s] (%s:%s)",
                                                            Status.NOT_FOUND));
        }
        return new FacetNode(instance, aspect, readOnlyModel, uriInfo);
    }

    /**
     * @param aspect
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<FacetNode<RuleForm, Network>> getFacetInstances(Aspect<RuleForm> aspect) {
        List<FacetNode<RuleForm, Network>> facets = new ArrayList<>();
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        for (ExistentialRuleform ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                                       aspect.getClassifier().getInverse())) {
            facets.add(new FacetNode(ruleform, aspect, readOnlyModel, uriInfo));
        }
        return facets;
    }
}
