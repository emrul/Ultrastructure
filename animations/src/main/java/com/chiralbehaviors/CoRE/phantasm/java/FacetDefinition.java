/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.java;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Edge;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Inferred;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;

/**
 * @author hhildebrand
 *
 */
public class FacetDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        extends Phantasmagoria<RuleForm, NetworkRuleform<RuleForm>> {

    private static final String GET = "get";
    private static final String SET = "set";

    public static NetworkAuthorization<?> facetFrom(Facet facetAnnotation) {
        return null;
    }

    private Map<Class<Phantasm<?>>, FacetDefinition<?>> CACHE = new HashMap<>();
    private final Facet                                facetAnnotation;
    private final Class<Phantasm<RuleForm>>             phantasm;
    private final UUID                                  workspace;

    protected final Map<Method, StateFunction<RuleForm>> methods = new HashMap<>();

    @SuppressWarnings("unchecked")
    public FacetDefinition(Model model,
                           Class<Phantasm<RuleForm>> stateInterface,
                           PhantasmTraversal<RuleForm, NetworkRuleform<RuleForm>> traverser) {
        super((NetworkAuthorization<RuleForm>) facetFrom(stateInterface.getAnnotation(Facet.class)),
              traverser);
        this.phantasm = stateInterface;
        facetAnnotation = stateInterface.getAnnotation(Facet.class);
        workspace = WorkspaceAccessor.uuidOf(facetAnnotation.workspace());
        for (Method method : stateInterface.getDeclaredMethods()) {
            if (!method.isDefault()) {
                process(method);
            }
        }
    }

    /**
     * Constrain the ruleform to have the required facets.
     * 
     * @param model
     * @param ruleform
     * @throws ClassCastException
     *             - if the ruleform is not classified as required by the facets
     *             of this state definition
     */
    public void constrain(Model model, RuleForm ruleform) {
        if (ruleform == null) {
            throw new IllegalStateException("Ruleform cannot be null");
        }
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(workspace);
        if (scope == null) {
            throw new IllegalStateException(String.format("Cannot obtain workspace for state interface %s",
                                                          phantasm));
        }
        if (!model.getNetworkedModel(ruleform)
                  .isAccessible(ruleform, facet.getClassifier(),
                                facet.getClassification())) {
            throw new ClassCastException(String.format("%s does not have required facet %s of state %s",
                                                       ruleform,
                                                       facet.toFacetString(),
                                                       phantasm));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Phantasm construct(ExistentialRuleform ruleform, Model model,
                              Agency updatedBy) {
        RuleForm form = (RuleForm) ruleform;

        model.getNetworkedModel(ruleform)
             .initialize(form, new Aspect(facet.getClassifier(),
                                          facet.getClassification()));
        return wrap(form, model);
    }

    public FacetDefinition<?> getCached(Class<? extends Phantasm<?>> returnPhantasm) {
        return CACHE.get(returnPhantasm);
    }

    public Map<Method, StateFunction<RuleForm>> getMethods() {
        return methods;
    }

    public Class<Phantasm<RuleForm>> getPhantasm() {
        return phantasm;
    }

    public UUID getWorkspace() {
        return workspace;
    }

    public Phantasm<?> wrap(@SuppressWarnings("rawtypes") ExistentialRuleform ruleform,
                            Model model) {
        @SuppressWarnings("unchecked")
        RuleForm form = (RuleForm) ruleform;
        constrain(model, form);
        PhantasmTwo<RuleForm> doppelgänger = new PhantasmTwo<RuleForm>(form,
                                                                       this,
                                                                       model);
        Phantasm<?> proxy = (Phantasm<?>) Proxy.newProxyInstance(phantasm.getClassLoader(),
                                                                 new Class[] { phantasm },
                                                                 doppelgänger);
        return proxy;

    }

    private void getInferred(Class<? extends Phantasm<?>> phantasm,
                             Method method, String fieldName,
                             Class<ExistentialRuleform<?, ?>> rulformClass) {
        if (!rulformClass.equals(getRuleformClass())) {
            throw new IllegalStateException(String.format("Use of @Inferred can only be applied to network relationship methods: %s",
                                                          method.toGenericString()));
        }
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.getChildren(facet,
                                                              state.getRuleform(),
                                                              children.get(fieldName))
                                                 .stream()
                                                 .map(r -> state.wrap(phantasm,
                                                                      r)));
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> getRuleformClass() {
        return (Class<RuleForm>) Model.getExistentialRuleform(phantasm);
    }

    private void process(Edge edge, Method method) {
        if (method.getName()
                  .startsWith("add")) {
            processAdd(edge, method);
        } else if (method.getName()
                         .startsWith("remove")) {
            processRemove(edge, method);
        } else if (method.getParameterTypes().length == 0
                   && List.class.isAssignableFrom(method.getReturnType())) {
            processGetList(edge, method);
        } else
            if (method.getParameterTypes().length == 1
                && List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            processSetList(edge, method);
        } else {
            processSingular(edge, method);
        }
    }

    private void process(Method method) {
        if (method.getName()
                  .equals("getScope")) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getScope(this));
            return;
        }
        if (method.getAnnotation(Edge.class) != null) {
            process(method.getAnnotation(Edge.class), method);
        } else if (method.getAnnotation(PrimitiveState.class) != null) {
            process(method.getAnnotation(PrimitiveState.class), method);
        }
    }

    private void process(PrimitiveState annotation, Method method) {
        if (method.getName()
                  .startsWith(GET)) {
            processPrimitiveGetter(annotation, method);
        } else if (method.getName()
                         .startsWith(SET)) {
            processPrimitiveSetter(annotation, method);
        } else {
            throw new IllegalStateException(String.format("The method is neither a primitive setter/getter: %s",
                                                          method.toGenericString()));
        }
    }

    /**
     * @param edge
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processAdd(Edge edge, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) edge.wrappedChildType();
            Class<?> ruleformClass = returnPhantasm.getAnnotation(Facet.class)
                                                   .ruleformClass();
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.addChild(facet,
                                                                   state.getRuleform(),
                                                                   children.get(edge.fieldName()),
                                                                   ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
            } else {
                processAddAuthorizations(edge, method, ruleformClass);
            }

        } else
            if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) edge.wrappedChildType();
            Class<?> ruleformClass = returnPhantasm.getAnnotation(Facet.class)
                                                   .ruleformClass();
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.addChild(facet,
                                                                   state.getRuleform(),
                                                                   children.get(edge.fieldName()),
                                                                   ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
            } else {
                processAddAuthorization(edge, method, ruleformClass);
            }
        }
    }

    private void processAddAuthorization(Edge annotation, Method method,
                                         Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.addChild(facet,
                                                           state.getRuleform(),
                                                           xdChildAuthorizations.get(annotation.fieldName()),
                                                           ((Phantasm<?>) arguments[0]).getRuleform()));
    }

    @SuppressWarnings({ "unchecked" })
    private void processAddAuthorizations(Edge annotation, Method method,
                                          Class<?> ruleformClass) {
        methods.put(method, (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> {
            return state.addChildren(facet, state.getRuleform(),
                                     xdChildAuthorizations.get(annotation.fieldName()),
                                     ((List<Phantasm<?>>) arguments[0]).stream()
                                                                       .map(inst -> inst.getRuleform())
                                                                       .collect(Collectors.toList()));
        });
    }

    /**
     * @param method
     * @param ruleformClass
     * @param returnPhantasm
     * @param annotation
     */
    private void processGetAuthorizations(String fieldName, Method method,
                                          Class<? extends Phantasm<?>> phantasmReturned,
                                          Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.getChildren(facet,
                                                              state.getRuleform(),
                                                              xdChildAuthorizations.get(fieldName))
                                                 .stream()
                                                 .map(r -> state.wrap(phantasmReturned,
                                                                      r))
                                                 .collect(Collectors.toList()));
    }

    /**
     * @param edge
     * @param method
     * @return
     */
    @SuppressWarnings("unchecked")
    private void processGetList(Edge edge, Method method) {
        Class<? extends Phantasm<RuleForm>> returnPhantasm = (Class<Phantasm<RuleForm>>) edge.wrappedChildType();
        Class<?> ruleformClass = returnPhantasm.getAnnotation(Facet.class)
                                               .ruleformClass();
        if (getRuleformClass().equals(ruleformClass)) {
            if (method.getAnnotation(Inferred.class) != null) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getChildren(facet,
                                                                      state.getRuleform(),
                                                                      children.get(edge.fieldName()))
                                                         .stream()
                                                         .map(ruleform -> state.wrap(returnPhantasm,
                                                                                     ruleform))
                                                         .collect(Collectors.toList()));
            } else {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getImmediateChildren(facet,
                                                                               state.getRuleform(),
                                                                               children.get(edge.fieldName()))
                                                         .stream()
                                                         .map(ruleform -> state.wrap(returnPhantasm,
                                                                                     ruleform))
                                                         .collect(Collectors.toList()));
            }
        } else {
            processGetAuthorizations(edge.fieldName(), method, returnPhantasm,
                                     ruleformClass);
        }
    }

    private void processGetSingularAuthorization(Method method,
                                                 Class<? extends Phantasm<?>> phantasmReturned,
                                                 String fieldName,
                                                 Class<ExistentialRuleform<?, ?>> ruleformClass) {
        methods.put(method, (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getModel()
                                                         .wrap(phantasmReturned,
                                                               state.getSingularChild(facet,
                                                                                      state.getRuleform(),
                                                                                      xdChildAuthorizations.get(fieldName))));
    }

    private void processPrimitiveGetter(PrimitiveState annotation,
                                        Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(String.format("getter method has arguments %s",
                                                          method.toGenericString()));
        }
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.getAttributeValue(facet,
                                                                    state.getRuleform(),
                                                                    attributes.get(annotation.fieldName())));
    }

    private void processPrimitiveSetter(PrimitiveState annotation,
                                        Method method) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.setAttributeValue(facet,
                                                                    state.getRuleform(),
                                                                    attributes.get(annotation.fieldName()),
                                                                    (List<?>) arguments[0]));
    }

    /**
     * @param edge
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processRemove(Edge edge, Method method) {
        Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) edge.wrappedChildType();
        Class<?> ruleformClass = returnPhantasm.getAnnotation(Facet.class)
                                               .ruleformClass();
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.removeChildren(facet,
                                                                         state.getRuleform(),
                                                                         childAuthorizations.get(edge.fieldName()),
                                                                         ((List<Phantasm<RuleForm>>) arguments[0]).stream()
                                                                                                                  .map(r -> r.getRuleform())
                                                                                                                  .collect(Collectors.toList())));
            } else {
                processRemoveAuthorizations(edge.fieldName(), method,
                                            ruleformClass);
            }
        } else
            if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.removeChild(facet,
                                                                      state.getRuleform(),
                                                                      children.get(edge.fieldName()),
                                                                      ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
            } else {
                processRemoveAuthorization(edge.fieldName(), method,
                                           ruleformClass);
            }
        }
    }

    /**
     * @param annotation
     * @param method
     * @param ruleformClass
     */
    private void processRemoveAuthorization(String fieldName, Method method,
                                            Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.removeChild(facet,
                                                              state.getRuleform(),
                                                              xdChildAuthorizations.get(fieldName),
                                                              ((Phantasm<?>) arguments[0]).getRuleform()));
    }

    /**
     * @param key
     * @param method
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processRemoveAuthorizations(String fieldName, Method method,
                                             Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.removeChildren(facet,
                                                                 state.getRuleform(),
                                                                 xdChildAuthorizations.get(fieldName),
                                                                 ((List<Phantasm<?>>) arguments[0]).stream()
                                                                                                   .map(r -> r.getRuleform())
                                                                                                   .collect(Collectors.toList())));
    }

    /**
     * @param annotation
     * @param method
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processSetAuthorizations(Edge edge, Method method,
                                          Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.setChildren(facet,
                                                              state.getRuleform(),
                                                              xdChildAuthorizations.get(edge.fieldName()),
                                                              ((List<Phantasm<?>>) arguments[0]).stream()
                                                                                                .map(r -> r.getRuleform())
                                                                                                .collect(Collectors.toList())));
    }

    /**
     * @param edge
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processSetList(Edge edge, Method method) {
        Class<?> ruleformClass = ((Class<Phantasm<?>>) edge.wrappedChildType()).getAnnotation(Facet.class)
                                                                               .ruleformClass();
        if (getRuleformClass().equals(ruleformClass)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setChildren(facet,
                                                                  state.getRuleform(),
                                                                  children.get(edge.fieldName()),
                                                                  ((List<Phantasm<RuleForm>>) arguments[0]).stream()
                                                                                                           .map(r -> r.getRuleform())
                                                                                                           .collect(Collectors.toList())));
        } else {
            processSetAuthorizations(edge, method, ruleformClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void processSetSingular(Method method, Edge edge) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(String.format("Not a valid Relationship setter: %s",
                                                             method));
        }
        Class<?> ruleformClass = ((Class<Phantasm<?>>) edge.wrappedChildType()).getAnnotation(Facet.class)
                                                                               .ruleformClass();
        if (ruleformClass.equals(getRuleformClass())) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setSingularChild(facet,
                                                                       state.getRuleform(),
                                                                       children.get(edge.fieldName()),
                                                                       ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
        } else {
            processSetSingularAuthorization(method, edge.fieldName(),
                                            ruleformClass);
        }
    }

    private void processSetSingularAuthorization(Method method,
                                                 String fieldName,
                                                 Class<?> ruleformClass) {
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.setSingularChild(facet,
                                                                   state.getRuleform(),
                                                                   xdChildAuthorizations.get(fieldName),
                                                                   ((Phantasm<?>) arguments[0]).getRuleform()));
    }

    @SuppressWarnings("unchecked")
    private void processSingular(Edge edge, Method method) {
        if (method.getReturnType()
                  .equals(Void.TYPE)) {
            processSetSingular(method, edge);
            return;
        }

        Class<? extends Phantasm<?>> phantasmReturned = (Class<Phantasm<?>>) edge.wrappedChildType();
        Class<ExistentialRuleform<?, ?>> ruleformClass = (Class<ExistentialRuleform<?, ?>>) phantasmReturned.getAnnotation(Facet.class)
                                                                                                            .ruleformClass();
        if (method.getAnnotation(Inferred.class) != null) {
            getInferred(phantasmReturned, method, edge.fieldName(),
                        ruleformClass);
        } else {
            if (ruleformClass.equals(getRuleformClass())) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.wrap(phantasmReturned,
                                                               state.getSingularChild(facet,
                                                                                      state.getRuleform(),
                                                                                      children.get(edge.fieldName()))));
            } else {
                processGetSingularAuthorization(method, phantasmReturned,
                                                edge.fieldName(),
                                                ruleformClass);
            }
        }
    }
}
