/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.graphql.mutations;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ChildSequencing;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ChildSequencing.ChildSequencingState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.ChildSequencing.ChildSequencingUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface ChildSequencingMutations {

    @GraphQLField
    default ChildSequencing createChildSequencing(@NotNull @GraphQLName("state") ChildSequencingState state,
                                                  DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        ChildSequencingAuthorizationRecord record = model.records()
                                                         .newChildSequencingAuthorization();
        if (!model.checkCreate(record)) {
            return null;
        }

        state.update(record);
        record.insert();
        return new ChildSequencing(record);
    }

    @GraphQLField
    default Boolean removeChildSequencing(@NotNull @GraphQLName("id") String id,
                                          DataFetchingEnvironment env) {
        ChildSequencing.fetch(env, UUID.fromString(id))
                       .getRecord()
                       .delete();
        return true;
    }

    @GraphQLField
    default ChildSequencing updateChildSequencing(@NotNull @GraphQLName("state") ChildSequencingUpdateState state,
                                                  DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        ChildSequencingAuthorizationRecord record = model.create()
                                                         .selectFrom(Tables.CHILD_SEQUENCING_AUTHORIZATION)
                                                         .where(Tables.CHILD_SEQUENCING_AUTHORIZATION.ID.equal(UUID.fromString(state.id)))
                                                         .fetchOne();
        state.update(record);
        record.update();
        return new ChildSequencing(record);
    }
}
