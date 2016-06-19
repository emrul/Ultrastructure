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

package com.chiralbehaviors.CoRE.phantasm.graphql.queries;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.SiblingSequencing;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface SiblingSequencingQueries {

    @GraphQLField
    default SiblingSequencing siblingSequencing(@NotNull @GraphQLName("id") String id,
                                                DataFetchingEnvironment env) {
        return SiblingSequencing.fetch(env, UUID.fromString(id));
    }

    @GraphQLField
    default List<SiblingSequencing> siblingSequencings(@GraphQLName("ids") List<String> ids,
                                                       DataFetchingEnvironment env) {
        if (ids == null) {
            Product workspace = ((WorkspaceContext) env.getContext()).getWorkspace();
            return WorkspaceSchema.ctx(env)
                                  .create()
                                  .selectFrom(Tables.SIBLING_SEQUENCING_AUTHORIZATION)
                                  .where(Tables.SIBLING_SEQUENCING_AUTHORIZATION.WORKSPACE.equal(workspace.getId()))
                                  .fetch()
                                  .into(SiblingSequencingAuthorizationRecord.class)
                                  .stream()
                                  .map(r -> new SiblingSequencing(r))
                                  .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(s -> UUID.fromString(s))
                  .map(id -> SiblingSequencing.fetch(env, id))
                  .collect(Collectors.toList());
    }
}
