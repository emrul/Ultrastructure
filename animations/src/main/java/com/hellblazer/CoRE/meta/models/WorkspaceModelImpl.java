/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.unit.Unit;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.WorkspaceModel;
import com.hellblazer.CoRE.meta.graph.Edge;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.meta.graph.Node;
import com.hellblazer.CoRE.meta.graph.impl.EdgeImpl;
import com.hellblazer.CoRE.meta.graph.impl.GraphImpl;
import com.hellblazer.CoRE.meta.graph.impl.NodeImpl;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductUnitAccessAuthorization;

/**
 * @author hhildebrand
 * 
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final Model model;

    public WorkspaceModelImpl(Model model) {
        this.model = model;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getAgencies(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Agency> getAgencies(Product workspace,
                                          Relationship relationship) {
        List<Agency> agencies = new ArrayList<Agency>();
        for (ProductAgencyAccessAuthorization auth : model.getProductModel().getAgencyAccessAuths(workspace,
                                                                                                  relationship)) {
            for (Agency agency : model.getAgencyModel().getChildren(auth.getChild(),
                                                                    auth.getChildTransitiveRelationship())) {
                agencies.add(agency);
            }
        }
        return agencies;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getAttributes(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Attribute> getAttributes(Product workspace,
                                               Relationship relationship) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (ProductAttributeAccessAuthorization auth : model.getProductModel().getAttributeAccessAuths(workspace,
                                                                                                        relationship)) {
            for (Attribute attribute : model.getAttributeModel().getChildren(auth.getChild(),
                                                                             auth.getChildTransitiveRelationship())) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getJobs(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Job> getJobs(Product workspace, Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getLocationss(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Location> getLocations(Product workspace,
                                             Relationship relationship) {
        List<Location> locations = new ArrayList<Location>();
        for (ProductLocationAccessAuthorization auth : model.getProductModel().getLocationAccessAuths(workspace,
                                                                                                      relationship)) {
            for (Location location : model.getLocationModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                locations.add(location);
            }
        }
        return locations;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getMetaProtocols(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<MetaProtocol> getMetaProtocols(Product workspace,
                                                     Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getProductChildSequencingAuthorizations(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductChildSequencingAuthorization> getProductChildSequencingAuthorizations(Product workspace,
                                                                                                   Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getProductParentSequencingAuthorizations(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductParentSequencingAuthorization> getProductParentSequencingAuthorizations(Product workspace,
                                                                                                     Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getProducts(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Product> getProducts(Product workspace,
                                           Relationship relationship) {
        return model.getProductModel().getChildren(workspace, relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getProductSiblingSequencingAuthorizations(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<ProductSiblingSequencingAuthorization> getProductSiblingSequencingAuthorizations(Product workspace,
                                                                                                       Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getProtocols(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Protocol> getProtocols(Product workspace,
                                             Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getRelationships(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Relationship> getRelationships(Product workspace,
                                                     Relationship relationship) {
        List<Relationship> relationships = new ArrayList<Relationship>();
        for (ProductRelationshipAccessAuthorization auth : model.getProductModel().getRelationshipAccessAuths(workspace,
                                                                                                              relationship)) {
            for (Relationship r : model.getRelationshipModel().getChildren(auth.getChild(),
                                                                           auth.getChildTransitiveRelationship())) {
                relationships.add(r);
            }
        }
        return relationships;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getStatusCodes(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<StatusCode> getStatusCodes(Product workspace,
                                                 Relationship relationship) {
        List<StatusCode> statusCodes = new ArrayList<StatusCode>();
        for (ProductStatusCodeAccessAuthorization auth : model.getProductModel().getStatusCodeAccessAuths(workspace,
                                                                                                          relationship)) {
            for (StatusCode code : model.getStatusCodeModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                statusCodes.add(code);
            }
        }
        return statusCodes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#findStatusCodeSequences(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<StatusCodeSequencing> getStatusCodeSequences(Product workspace,
                                                                   Relationship relationship) {
        Set<StatusCodeSequencing> sequences = new HashSet<StatusCodeSequencing>();
        for (ProductStatusCodeAccessAuthorization auth : model.getProductModel().getStatusCodeAccessAuths(workspace,
                                                                                                          relationship)) {
            for (StatusCode code : model.getStatusCodeModel().getChildren(auth.getChild(),
                                                                          auth.getChildTransitiveRelationship())) {
                sequences.addAll(model.getStatusCodeModel().getStatusCodeSequencingChild(code));
                sequences.addAll(model.getStatusCodeModel().getStatusCodeSequencingParent(code));
            }
        }
        return sequences;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.WorkspaceModel#getUnits(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<Unit> getUnits(Product workspace,
                                     Relationship relationship) {
        List<Unit> units = new ArrayList<Unit>();
        for (ProductUnitAccessAuthorization auth : model.getProductModel().getUnitAccessAuths(workspace,
                                                                                              relationship)) {
            for (Unit unit : model.getUnitModel().getChildren(auth.getChild(),
                                                              auth.getChildTransitiveRelationship())) {
                units.add(unit);
            }
        }
        return units;
    }

    @Override
    public Graph getStatusCodeGraph(Product product) {
        Map<StatusCode, Node<StatusCode>> nodes = new HashMap<StatusCode, Node<StatusCode>>();
        List<Edge<?>> edges = new ArrayList<Edge<?>>();
        for (StatusCode currentCode : model.getJobModel().getStatusCodesFor(product)) {
            Node<StatusCode> parent = new NodeImpl<StatusCode>(currentCode);
            nodes.put(currentCode, parent);
            for (StatusCodeSequencing sequence : model.getStatusCodeModel().getStatusCodeSequencingParent(product,
                                                                                                          currentCode)) {
                StatusCode childCode = sequence.getChildCode();
                Node<StatusCode> child = nodes.get(childCode);
                if (child == null) {
                    child = new NodeImpl<StatusCode>(childCode);
                    nodes.put(childCode, child);
                }
                Edge<StatusCodeSequencing> edge = new EdgeImpl<StatusCodeSequencing>(
                                                                                     parent,
                                                                                     sequence,
                                                                                     child);
                edges.add(edge);
            }
        }
        List<Node<?>> nodeList = new ArrayList<Node<?>>();
        nodeList.addAll(nodes.values());
        return new GraphImpl(nodeList, edges);
    }
}