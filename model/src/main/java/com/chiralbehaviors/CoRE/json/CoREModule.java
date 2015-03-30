/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.json;

import com.chiralbehaviors.CoRE.Ruleform;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A jackson module for registering serializers and deserializers.
 *
 * @author hparry
 *
 */
public class CoREModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public CoREModule() {
        super("CoREModule");
    }

    @Override
    public void setupModule(SetupContext context) {

        context.setMixInAnnotations(Ruleform.class,
                                    PolymorphicRuleformMixin.class);
        super.setupModule(context);
    }
}
