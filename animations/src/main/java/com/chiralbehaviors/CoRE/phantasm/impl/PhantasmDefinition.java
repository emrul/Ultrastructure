/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.impl;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {
    private final List<StateDefinition<RuleForm>> facets = new ArrayList<>();
    private final Class<Phantasm<RuleForm>>       phantasm;

    @SuppressWarnings("unchecked")
    public PhantasmDefinition(Class<Phantasm<RuleForm>> phantasm) {
        if (!Phantasm.class.isAssignableFrom(phantasm))
            throw new IllegalArgumentException(
                                               String.format("Not a Phantasm: %s",
                                                             phantasm));
        this.phantasm = phantasm;
        if (phantasm.getAnnotation(State.class) != null) {
            StateDefinition<RuleForm> facet = new StateDefinition<RuleForm>(
                                                                            phantasm);
            facets.add(facet);
        }
        for (Class<?> iFace : phantasm.getInterfaces()) {
            if (iFace.getAnnotation(State.class) != null) {
                facets.add(new StateDefinition<RuleForm>(
                                                         (Class<Phantasm<RuleForm>>) iFace));
            }
        }
        if (facets.isEmpty()) {
            throw new IllegalArgumentException(
                                               String.format("Require at least one @State annotation on a Phantasm: %s",
                                                             phantasm));
        }
    }

    /**
     * @param ruleform
     * @param modelImpl
     * @param updatedBy
     * @return
     */
    @SuppressWarnings("unchecked")
    public Phantasm<?> construct(ExistentialRuleform<?, ?> ruleform,
                                 Model model, Agency updatedBy) {
        ExistentialRuleform<?, ?> form = ruleform;
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = (NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?>) model.getNetworkedModel(form);
        for (StateDefinition<RuleForm> facet : facets) {
            for (Aspect<RuleForm> aspect : facet.getAspects(model)) {
                networkedModel.initialize((RuleForm) form, aspect, updatedBy);
            }
        }
        return wrap(ruleform, model);
    }

    @SuppressWarnings("unchecked")
    public Phantasm<?> wrap(ExistentialRuleform<?, ?> ruleform, Model model) {
        Map<Class<?>, Object> stateMap = new HashMap<>();
        Object[] instances = new Object[facets.size()];
        int i = 0;
        stateMap.put(Phantasm.class, ruleform);
        for (StateDefinition<RuleForm> facet : facets) {
            Object state = facet.wrap((RuleForm) ruleform, model);
            instances[i++] = state;
            stateMap.put(facet.getStateInterface(), state);
            if (facet.getStateInterface().equals(phantasm)) {
                stateMap.put(phantasm, state);
            }
        }
        return (Phantasm<?>) Proxy.newProxyInstance(phantasm.getClassLoader(),
                                                    new Class[] { phantasm },
                                                    new PhantasmTwo(stateMap,
                                                                    ruleform));

    }
}
