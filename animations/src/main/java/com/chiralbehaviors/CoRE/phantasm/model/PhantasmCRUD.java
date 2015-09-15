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

package com.chiralbehaviors.CoRE.phantasm.model;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.google.common.base.Function;

/**
 * CRUD for Phantasms. This class is the animation procedure that maintains and
 * mediates the Phantasm/Facet constructs in Ultrastructure. It's a bit
 * unwieldy, because of the type signatures required for erasure. Provides a
 * centralized implementation of Phantasm CRUD and the security model for such.
 * 
 * @author hhildebrand
 *
 */
public class PhantasmCRUD<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {
    private final Relationship apply;
    private final Relationship create;
    private final Relationship delete;
    private final Relationship invoke;
    private final Model        model;
    private final Relationship read;
    private final Relationship remove;
    private final Relationship update;

    public PhantasmCRUD(Model model) {
        this.model = model;
        create = model.getKernel()
                      .getCREATE();
        delete = model.getKernel()
                      .getDELETE();
        invoke = model.getKernel()
                      .getINVOKE();
        read = model.getKernel()
                    .getREAD();
        remove = model.getKernel()
                      .getREMOVE();
        update = model.getKernel()
                      .getUPDATE();
        apply = model.getKernel()
                     .getAPPLY();
    }

    /**
     * Add the child to the list of children of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm addChild(NetworkAuthorization<RuleForm> facet,
                             RuleForm instance,
                             NetworkAuthorization<RuleForm> auth,
                             RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }

        networkedModel.link(instance, model.getEntityManager()
                                           .merge(auth.getChildRelationship()),
                            child, model.getCurrentPrincipal()
                                        .getPrincipal());
        return instance;
    }

    public RuleForm addChild(NetworkAuthorization<RuleForm> facet,
                             RuleForm instance,
                             XDomainNetworkAuthorization<?, ?> auth,
                             @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        if (childAuthClassification instanceof Agency) {
            networkedModel.authorize(instance, model.getEntityManager()
                                                    .merge(auth.getConnection()),
                                     (Agency) child);
        } else if (childAuthClassification instanceof Location) {
            networkedModel.authorize(instance, model.getEntityManager()
                                                    .merge(auth.getConnection()),
                                     (Location) child);
        } else if (childAuthClassification instanceof Product) {
            networkedModel.authorize(instance, model.getEntityManager()
                                                    .merge(auth.getConnection()),
                                     (Product) child);
        } else if (childAuthClassification instanceof Relationship) {
            networkedModel.authorize(instance, model.getEntityManager()
                                                    .merge(auth.getConnection()),
                                     (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        return instance;
    }

    /**
     * Add the list of children to the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm addChildren(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        children.stream()
                .filter(child -> checkREAD(child, networkedModel))
                .forEach(child -> networkedModel.link(instance,
                                                      model.getEntityManager()
                                                           .merge(auth.getAuthorizedRelationship()),
                                                      child,
                                                      model.getCurrentPrincipal()
                                                           .getPrincipal()));
        return instance;
    }

    public RuleForm addChildren(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                @SuppressWarnings("rawtypes") List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        children.stream()
                .filter(child -> checkREAD(child,
                                           model.getUnknownNetworkedModel(child)))
                .forEach(child -> {
                    if (childAuthClassification instanceof Agency) {
                        networkedModel.authorize(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (Agency) child);
                    } else if (childAuthClassification instanceof Location) {
                        networkedModel.authorize(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (Location) child);
                    } else if (childAuthClassification instanceof Product) {
                        networkedModel.authorize(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (Product) child);
                    } else
                        if (childAuthClassification instanceof Relationship) {
                        networkedModel.authorize(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (Relationship) child);
                    } else {
                        throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                                         instance.getClass()
                                                                                 .getSimpleName(),
                                                                         childAuthClassification.getClass()
                                                                                                .getSimpleName()));
                    }
                });
        return instance;

    }

    /**
     * Apply the facet to the instance
     * 
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public RuleForm apply(NetworkAuthorization<RuleForm> facet,
                          RuleForm instance,
                          Function<RuleForm, RuleForm> constructor) throws SecurityException {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, getAPPLY())) {
            return instance;
        }
        networkedModel.initialize(instance, model.getEntityManager()
                                                 .merge(facet),
                                  model.getCurrentPrincipal()
                                       .getPrincipal());
        if (!checkInvoke(facet, instance)) {
            return null;
        }
        return constructor.apply(instance);
    }

    public boolean checkInvoke(NetworkAuthorization<RuleForm> facet,
                               RuleForm instance) {
        Agency principal = model.getCurrentPrincipal()
                                .getPrincipal();
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        Relationship invoke = getINVOKE();
        return networkedModel.checkCapability(principal, instance, invoke)
               && networkedModel.checkFacetCapability(principal, facet, invoke);
    }

    /**
     * Create a new instance of the facet
     * 
     * @param facet
     * @param name
     * @param description
     * @return
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @SuppressWarnings("unchecked")
    public RuleForm createInstance(NetworkAuthorization<RuleForm> facet,
                                   String name, String description,
                                   Function<RuleForm, RuleForm> constructor) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, getCREATE())) {
            return null;
        }
        RuleForm instance;
        try {
            instance = (RuleForm) Ruleform.initializeAndUnproxy(facet.getClassification())
                                          .getClass()
                                          .getConstructor(String.class,
                                                          String.class,
                                                          Agency.class)
                                          .newInstance(name, description,
                                                       model.getCurrentPrincipal()
                                                            .getPrincipal());
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Cannot construct instance of existential ruleform for %s",
                                                          new Aspect<RuleForm>(facet.getClassifier(),
                                                                               facet.getClassification())),
                                            e);
        }
        networkedModel.initialize(instance, model.getEntityManager()
                                                 .merge(facet),
                                  model.getCurrentPrincipal()
                                       .getPrincipal());
        if (!checkInvoke(facet, instance)) {
            return null;
        }
        return constructor.apply(instance);
    }

    public Relationship getAPPLY() {
        return apply;
    }

    /**
     * Answer the attribute value of the instance
     * 
     * @param facet
     * @param instance
     * @param stateAuth
     * 
     * @return
     */
    public Object getAttributeValue(NetworkAuthorization<RuleForm> facet,
                                    RuleForm instance,
                                    AttributeAuthorization<RuleForm, Network> stateAuth) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(stateAuth.getNetworkAuthorization()
                                                                                            .getClassification());
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(stateAuth, networkedModel)) {
            return null;
        }
        Attribute authorizedAttribute = stateAuth.getAuthorizedAttribute();
        if (authorizedAttribute.getIndexed()) {
            return getIndexedAttributeValue(instance, authorizedAttribute,
                                            networkedModel);
        } else if (authorizedAttribute.getKeyed()) {
            return getMappedAttributeValue(instance, authorizedAttribute,
                                           networkedModel);
        }
        Object value = networkedModel.getAttributeValue(instance,
                                                        authorizedAttribute)
                                     .getValue();
        if (value instanceof BigDecimal) {
            value = ((BigDecimal) value).floatValue();
        }
        return value;
    }

    /**
     * Answer the inferred and immediate network children of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * 
     * @return
     */
    public List<RuleForm> getChildren(NetworkAuthorization<RuleForm> facet,
                                      RuleForm instance,
                                      NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(auth, networkedModel)) {
            return Collections.emptyList();
        }
        return networkedModel.getChildren(instance, auth.getChildRelationship())
                             .stream()
                             .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                                  .getPrincipal(),
                                                                             child,
                                                                             getREAD()))
                             .collect(Collectors.toList());

    }

    /***
     * Answer the xd children of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * 
     * @return
     */
    public List<?> getChildren(NetworkAuthorization<RuleForm> facet,
                               RuleForm instance,
                               XDomainNetworkAuthorization<?, ?> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(auth, networkedModel)) {
            return Collections.emptyList();
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        @SuppressWarnings("rawtypes")
        List<? extends ExistentialRuleform> result;
        if (childAuthClassification instanceof Agency) {
            result = networkedModel.getAuthorizedAgencies(instance,
                                                          auth.getConnection());
        } else if (childAuthClassification instanceof Location) {
            result = networkedModel.getAuthorizedLocations(instance,
                                                           auth.getConnection());
        } else if (childAuthClassification instanceof Product) {
            result = networkedModel.getAuthorizedProducts(instance,
                                                          auth.getConnection());
        } else if (childAuthClassification instanceof Relationship) {
            result = networkedModel.getAuthorizedRelationships(instance,
                                                               auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        NetworkedModel<?, ?, ?, ?> childNetworkModel = model.getUnknownNetworkedModel(childAuthClassification);
        return result.stream()
                     .filter(child -> childNetworkModel.checkCapability(model.getCurrentPrincipal()
                                                                             .getPrincipal(),
                                                                        child,
                                                                        getREAD()))
                     .collect(Collectors.toList());
    }

    public Relationship getCREATE() {
        return create;
    }

    public Relationship getDELETE() {
        return delete;
    }

    /**
     * Answer the immediate, non inferred children of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * 
     * @return
     */
    public List<RuleForm> getImmediateChildren(NetworkAuthorization<RuleForm> facet,
                                               RuleForm instance,
                                               NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(auth, networkedModel)) {
            return Collections.emptyList();
        }
        return networkedModel.getImmediateChildren(instance,
                                                   auth.getChildRelationship())
                             .stream()
                             .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                                  .getPrincipal(),
                                                                             child,
                                                                             getREAD()))
                             .collect(Collectors.toList());
    }

    /**
     * Answer the list of instances of this facet.
     * 
     * @param facet
     * @return
     */
    public List<RuleForm> getInstances(NetworkAuthorization<RuleForm> facet) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, getREAD())) {
            return Collections.emptyList();
        }
        return networkedModel.getChildren(facet.getClassification(),
                                          facet.getClassifier()
                                               .getInverse())
                             .stream()
                             .filter(instance -> checkREAD(networkedModel,
                                                           instance))
                             .collect(Collectors.toList());
    }

    public Relationship getINVOKE() {
        return invoke;
    }

    public Model getModel() {
        return model;
    }

    public Relationship getREAD() {
        return read;
    }

    public Relationship getREMOVE() {
        return remove;
    }

    /**
     * Answer the singular network child of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * 
     * @return
     */
    public RuleForm getSingularChild(NetworkAuthorization<RuleForm> facet,
                                     RuleForm instance,
                                     NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(auth, networkedModel)) {
            return null;
        }
        RuleForm child = networkedModel.getImmediateChild(instance,
                                                          auth.getChildRelationship());
        return checkREAD(child, networkedModel) ? child : null;
    }

    /**
     * Answer the singular xd child of the instance
     * 
     * @param instance
     * @param auth
     * @param facet
     * @return
     */
    public Object getSingularChild(RuleForm instance,
                                   XDomainNetworkAuthorization<?, ?> auth,
                                   NetworkAuthorization<RuleForm> facet) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkREAD(facet, networkedModel)
            || !checkREAD(auth, networkedModel)) {
            return null;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        @SuppressWarnings("rawtypes")
        ExistentialRuleform child;
        if (childAuthClassification instanceof Agency) {
            child = networkedModel.getAuthorizedAgency(instance,
                                                       auth.getConnection());
        } else if (childAuthClassification instanceof Location) {
            child = networkedModel.getAuthorizedLocation(instance,
                                                         auth.getConnection());
        } else if (childAuthClassification instanceof Product) {
            child = networkedModel.getAuthorizedProduct(instance,
                                                        auth.getConnection());
        } else if (childAuthClassification instanceof Relationship) {
            child = networkedModel.getAuthorizedRelationship(instance,
                                                             auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        return checkREAD(child,
                         model.getUnknownNetworkedModel(childAuthClassification)) ? child
                                                                                  : null;
    }

    public Relationship getUPDATE() {
        return update;
    }

    @SuppressWarnings("rawtypes")
    public List<ExistentialRuleform> lookup(NetworkAuthorization auth,
                                            List<String> ids) {
        NetworkedModel<?, ?, ?, ?> networkedModel = model.getUnknownNetworkedModel(auth.getClassification());
        return ids.stream()
                  .map(id -> networkedModel.find(UUID.fromString(id)))
                  .filter(rf -> rf != null)
                  .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                       .getPrincipal(),
                                                                  child,
                                                                  getREAD()))
                  .collect(Collectors.toList());
    }

    @SuppressWarnings("rawtypes")
    public ExistentialRuleform lookup(NetworkAuthorization auth, String id) {
        NetworkedModel<?, ?, ?, ?> networkedModel = model.getUnknownNetworkedModel(auth.getClassification());
        return Optional.of(networkedModel.find(UUID.fromString(id)))
                       .filter(rf -> rf != null)
                       .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                            .getPrincipal(),
                                                                       child,
                                                                       getREAD()))
                       .get();
    }

    public List<RuleForm> lookupRuleForm(NetworkAuthorization<RuleForm> auth,
                                         List<String> ids) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        return ids.stream()
                  .map(id -> networkedModel.find(UUID.fromString(id)))
                  .filter(rf -> rf != null)
                  .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                       .getPrincipal(),
                                                                  child,
                                                                  getREAD()))
                  .collect(Collectors.toList());
    }

    /**
     * Remove the facet from the instance
     * 
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public RuleForm remove(NetworkAuthorization<RuleForm> facet,
                           RuleForm instance,
                           boolean deleteAttributes) throws SecurityException {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, getREMOVE())) {
            return instance;
        }
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
    }

    /**
     * Remove a child from the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm removeChild(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(instance,
                                                                         auth.getChildRelationship(),
                                                                         child.getRuleform());
        if (link != null) {
            model.getEntityManager()
                 .remove(link);
        }
        return instance;
    }

    public RuleForm removeChild(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        if (childAuthClassification instanceof Agency) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Agency) child);
        } else if (childAuthClassification instanceof Location) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Location) child);
        } else if (childAuthClassification instanceof Product) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Product) child);
        } else if (childAuthClassification instanceof Relationship) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        return instance;
    }

    /**
     * Remove the immediate child links from the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm removeChildren(NetworkAuthorization<RuleForm> facet,
                                   RuleForm instance,
                                   NetworkAuthorization<RuleForm> auth,
                                   List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        for (RuleForm child : children) {
            NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(instance,
                                                                             auth.getChildRelationship(),
                                                                             child.getRuleform());
            if (link != null) {
                model.getEntityManager()
                     .remove(link);
            }
        }
        return instance;
    }

    public RuleForm removeChildren(NetworkAuthorization<RuleForm> facet,
                                   RuleForm instance,
                                   XDomainNetworkAuthorization<?, ?> auth,
                                   @SuppressWarnings("rawtypes") List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        for (ExistentialRuleform<?, ?> child : children) {
            if (childAuthClassification instanceof Agency) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Agency) child);
            } else if (childAuthClassification instanceof Location) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Location) child);
            } else if (childAuthClassification instanceof Product) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Product) child);
            } else if (childAuthClassification instanceof Relationship) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Relationship) child);
            } else {
                throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                                 instance.getClass()
                                                                         .getSimpleName(),
                                                                 childAuthClassification.getClass()
                                                                                        .getSimpleName()));
            }
        }
        return instance;
    }

    public RuleForm setAttributeValue(NetworkAuthorization<RuleForm> facet,
                                      RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      List<Object> value) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(stateAuth, networkedModel)) {
            return instance;
        }
        setAttributeArray(instance, stateAuth.getAuthorizedAttribute(),
                          value.toArray(), networkedModel);
        return instance;
    }

    public RuleForm setAttributeValue(NetworkAuthorization<RuleForm> facet,
                                      RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      Map<String, Object> value) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(stateAuth, networkedModel)) {
            return instance;
        }
        setAttributeMap(instance, stateAuth.getAuthorizedAttribute(), value,
                        networkedModel);
        return instance;
    }

    public RuleForm setAttributeValue(NetworkAuthorization<RuleForm> facet,
                                      RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      Object value) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(stateAuth.getNetworkAuthorization()
                                                                                            .getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(stateAuth, networkedModel)) {
            return instance;
        }
        networkedModel.getAttributeValue(instance, model.getEntityManager()
                                                        .merge(stateAuth.getAuthorizedAttribute()))
                      .setValue(value);
        return instance;
    }

    /**
     * Set the immediate children of the instance to be the list of supplied
     * children. No inferred links will be explicitly added or deleted.
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm setChildren(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }

        for (NetworkRuleform<RuleForm> childLink : networkedModel.getImmediateChildrenLinks(instance,
                                                                                            auth.getChildRelationship())) {
            model.getEntityManager()
                 .remove(childLink);
        }
        Relationship merged = model.getEntityManager()
                                   .merge(auth.getChildRelationship());
        children.stream()
                .filter(child -> checkREAD(child, networkedModel))
                .forEach(child -> networkedModel.link(instance, merged, child,
                                                      model.getCurrentPrincipal()
                                                           .getPrincipal()));
        return instance;
    }

    /**
     * Set the xd children of the instance.
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    @SuppressWarnings({ "unchecked" })
    public RuleForm setChildren(NetworkAuthorization<RuleForm> facet,
                                RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                List<?> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        if (childAuthClassification instanceof Agency) {
            networkedModel.setAuthorizedAgencies(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (List<Agency>) children);
        } else if (childAuthClassification instanceof Location) {
            networkedModel.setAuthorizedLocations(instance,
                                                  model.getEntityManager()
                                                       .merge(auth.getConnection()),
                                                  (List<Location>) children);
        } else if (childAuthClassification instanceof Product) {
            networkedModel.setAuthorizedProducts(instance,
                                                 model.getEntityManager()
                                                      .merge(auth.getConnection()),
                                                 (List<Product>) children);
        } else if (childAuthClassification instanceof Relationship) {
            networkedModel.setAuthorizedRelationships(instance,
                                                      model.getEntityManager()
                                                           .merge(auth.getConnection()),
                                                      (List<Relationship>) children);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        return instance;
    }

    /**
     * @param description
     * @param id
     * @return
     */
    public RuleForm setDescription(RuleForm instance, String description) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(instance, networkedModel)) {
            return instance;
        }
        instance.setDescription(description);
        return instance;
    }

    /**
     * @param name
     * @param id
     * @return
     */
    public RuleForm setName(RuleForm instance, String name) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(instance, networkedModel)) {
            return instance;
        }
        instance.setName(name);
        return instance;
    }

    /**
     * Set the singular child of the instance.
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm setSingularChild(NetworkAuthorization<RuleForm> facet,
                                     RuleForm instance,
                                     NetworkAuthorization<RuleForm> auth,
                                     RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        networkedModel.setImmediateChild(instance, model.getEntityManager()
                                                        .merge(auth.getAuthorizedRelationship()),
                                         child, model.getCurrentPrincipal()
                                                     .getPrincipal());
        return instance;
    }

    /**
     * Set the singular xd child of the instance
     * 
     * @param facet
     * @param instance
     * @param auth
     * @param child
     * 
     * @return
     */
    public RuleForm setSingularChild(NetworkAuthorization<RuleForm> facet,
                                     RuleForm instance,
                                     XDomainNetworkAuthorization<?, ?> auth,
                                     @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        if (!checkUPDATE(facet, networkedModel)
            || !checkUPDATE(auth, networkedModel)) {
            return instance;
        }
        @SuppressWarnings("rawtypes")
        ExistentialRuleform childAuthClassification = auth.isForward() ? auth.getToParent()
                                                                       : auth.getFromParent();
        if (childAuthClassification instanceof Agency) {
            networkedModel.authorizeSingular(instance, model.getEntityManager()
                                                            .merge(auth.getConnection()),
                                             (Agency) child);
        } else if (childAuthClassification instanceof Location) {
            networkedModel.authorizeSingular(instance, model.getEntityManager()
                                                            .merge(auth.getConnection()),
                                             (Location) child);
        } else if (childAuthClassification instanceof Product) {
            networkedModel.authorizeSingular(instance, model.getEntityManager()
                                                            .merge(auth.getConnection()),
                                             (Product) child);
        } else if (childAuthClassification instanceof Relationship) {
            networkedModel.authorizeSingular(instance, model.getEntityManager()
                                                            .merge(auth.getConnection()),
                                             (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuthClassification.getClass()
                                                                                    .getSimpleName()));
        }
        return instance;
    }

    private boolean checkREAD(AttributeAuthorization<RuleForm, Network> stateAuth,
                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              stateAuth, getREAD());
    }

    private boolean checkREAD(@SuppressWarnings("rawtypes") ExistentialRuleform child,
                              NetworkedModel<?, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              child, getREAD());
    }

    private boolean checkREAD(NetworkAuthorization<RuleForm> auth,
                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        return networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                        .getPrincipal(),
                                                   auth, getREAD());
    }

    private boolean checkREAD(NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                              RuleForm instance) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              instance, getREAD());
    }

    private boolean checkREAD(XDomainNetworkAuthorization<?, ?> auth,
                              NetworkedModel<?, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              auth, getREAD());
    }

    private boolean checkUPDATE(AttributeAuthorization<RuleForm, Network> stateAuth,
                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              stateAuth, getUPDATE());
    }

    private boolean checkUPDATE(@SuppressWarnings("rawtypes") ExistentialRuleform child,
                                NetworkedModel<?, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              child, getUPDATE());
    }

    private boolean checkUPDATE(NetworkAuthorization<RuleForm> auth,
                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              auth, getUPDATE());
    }

    private boolean checkUPDATE(XDomainNetworkAuthorization<?, ?> auth,
                                NetworkedModel<?, ?, ?, ?> networkedModel) {
        return networkedModel.checkCapability(model.getCurrentPrincipal()
                                                   .getPrincipal(),
                                              auth, getUPDATE());
    }

    private Object[] getIndexedAttributeValue(RuleForm instance,
                                              Attribute authorizedAttribute,
                                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {

        AttributeValue<RuleForm>[] attributeValues = getValueArray(instance,
                                                                   authorizedAttribute,
                                                                   networkedModel);

        Object[] values = (Object[]) Array.newInstance(Object.class,
                                                       attributeValues.length);
        for (AttributeValue<RuleForm> value : attributeValues) {
            values[value.getSequenceNumber()] = value.getValue();
        }
        return values;
    }

    private Map<String, Object> getMappedAttributeValue(RuleForm instance,
                                                        Attribute authorizedAttribute,
                                                        NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, AttributeValue<RuleForm>> entry : getValueMap(instance,
                                                                             authorizedAttribute,
                                                                             networkedModel).entrySet()) {
            map.put(entry.getKey(), entry.getValue()
                                         .getValue());
        }
        return map;
    }

    private AttributeValue<RuleForm>[] getValueArray(RuleForm instance,
                                                     Attribute attribute,
                                                     NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<? extends AttributeValue<RuleForm>> values = networkedModel.getAttributeValues(instance,
                                                                                            attribute);
        int max = 0;
        for (AttributeValue<RuleForm> value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        @SuppressWarnings("unchecked")
        AttributeValue<RuleForm>[] returnValue = new AttributeValue[max];
        for (AttributeValue<RuleForm> form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private Map<String, AttributeValue<RuleForm>> getValueMap(RuleForm instance,
                                                              Attribute attribute,
                                                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : networkedModel.getAttributeValues(instance,
                                                                                attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private AttributeValue<RuleForm> newAttributeValue(RuleForm instance,
                                                       Attribute attribute,
                                                       int i,
                                                       NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        AttributeValue<RuleForm> value = networkedModel.create(instance,
                                                               attribute,
                                                               model.getCurrentPrincipal()
                                                                    .getPrincipal());
        value.setSequenceNumber(i);
        return value;
    }

    private void setAttributeArray(RuleForm instance,
                                   Attribute authorizedAttribute,
                                   Object[] values,
                                   NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        AttributeValue<RuleForm>[] old = getValueArray(instance,
                                                       authorizedAttribute,
                                                       networkedModel);
        authorizedAttribute = model.getEntityManager()
                                   .merge(authorizedAttribute);
        if (values == null) {
            if (old != null) {
                for (AttributeValue<RuleForm> value : old) {
                    model.getEntityManager()
                         .remove(value);
                }
            }
        } else if (old == null) {
            for (int i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, null, values[i],
                         networkedModel);
            }
        } else if (old.length == values.length) {
            for (int i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i],
                         networkedModel);
            }
        } else if (old.length < values.length) {
            int i;
            for (i = 0; i < old.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i],
                         networkedModel);
            }
            for (; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, null, values[i],
                         networkedModel);
            }
        } else if (old.length > values.length) {
            int i;
            for (i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i],
                         networkedModel);
            }
            for (; i < old.length; i++) {
                model.getEntityManager()
                     .remove(old[i]);
            }
        }
    }

    private void setAttributeMap(RuleForm instance,
                                 Attribute authorizedAttribute,
                                 Map<String, Object> values,
                                 NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, AttributeValue<RuleForm>> valueMap = getValueMap(instance,
                                                                     authorizedAttribute,
                                                                     networkedModel);
        values.keySet()
              .stream()
              .filter(keyName -> !valueMap.containsKey(keyName))
              .forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (AttributeValue<RuleForm> value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        authorizedAttribute = model.getEntityManager()
                                   .merge(authorizedAttribute);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            AttributeValue<RuleForm> value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(instance, authorizedAttribute,
                                          ++maxSeq, networkedModel);
                model.getEntityManager()
                     .persist(value);
                value.setKey(entry.getKey());
            }
            value.setValue(entry.getValue());
        }
    }

    private void setValue(RuleForm instance, Attribute attribute, int i,
                          AttributeValue<RuleForm> existing, Object newValue,
                          NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (existing == null) {
            existing = newAttributeValue(instance, attribute, i,
                                         networkedModel);
            model.getEntityManager()
                 .persist(existing);
        }
        existing.setValue(newValue);
    }
}
