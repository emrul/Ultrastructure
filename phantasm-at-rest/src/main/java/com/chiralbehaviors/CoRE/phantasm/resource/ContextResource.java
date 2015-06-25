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

package com.chiralbehaviors.CoRE.phantasm.resource;

import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
@Path("facets/context")
public class ContextResource extends TransactionalResource {

    public ContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Produces({ "application/json", "text/json" })
    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProductContext(@PathParam("classifier") String relationship,
                                      @PathParam("classification") String ruleform) {
        UUID classifier;
        try {
            classifier = UUID.fromString(relationship);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        UUID classification;
        try {
            classification = UUID.fromString(ruleform);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        Aspect<Product> aspect;
        NetworkedModel<Product, ?, ?, ?> networkedModel = readOnlyModel.getProductModel();
        try {
            aspect = networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }

        return buildContext(aspect, networkedModel);
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> JsonNode buildContext(Aspect<RuleForm> aspect,
                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        ObjectNode context = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set("@context", context);
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(aspect)) {
            context.put(auth.getAuthorizedAttribute().getName(), iriFrom(auth.getAuthorizedAttribute()));
        }
        return context;
    }

    /**
     * @param authorizedAttribute
     * @return
     */
    private String iriFrom(Attribute authorizedAttribute) {
        // TODO Auto-generated method stub
        return null;
    }
}
