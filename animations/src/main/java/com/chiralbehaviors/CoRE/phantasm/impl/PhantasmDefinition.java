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

import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.janus.CompositeAssembler;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {
    private final List<StateDefinition<RuleForm>> facets = new ArrayList<>();
    private final Class<PhantasmBase<RuleForm>>   phantasm;

    public PhantasmDefinition(Class<PhantasmBase<RuleForm>> phantasm) {
        this.phantasm = phantasm;
    }

    public PhantasmBase<RuleForm> construct(RuleForm ruleform, Model model) {
        CompositeAssembler<PhantasmBase<RuleForm>> assembler = new CompositeAssembler<>(
                                                                                        phantasm);
        Object[] instances = new Object[facets.size()];
        int i = 0;
        for (StateDefinition<RuleForm> facet : facets) {
            instances[i++] = facet.construct(ruleform, model);
        }
        return assembler.construct(instances);
    }
}