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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ctx;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.wrap;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeAuthorization.AttributeAuthorizationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization.NeworkAuthorizationTypeFunction;

import graphql.annotations.DefaultTypeFunction;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLTypeReference;

/**
 * @author hhildebrand
 *
 */
public class Facet {

    class FacetTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return FacetType;
        }
    }

    public static final GraphQLObjectType FacetType;

    private static final String           FACET_STATE        = "FacetState";
    private static final String           ID                 = "id";
    private static final String           INSTANCES_OF_QUERY = "InstancesOfFacet";
    private static final String           SET_CLASSIFICATION = "setClassification";
    private static final String           SET_CLASSIFIER     = "setClassifier";
    private static final String           SET_NAME           = "setName";
    private static final String           STATE              = "state";

    static {
        DefaultTypeFunction.register(Cardinality.class,
                                     (u, t) -> GraphQLString);
        FacetType = Existential.objectTypeOf(Facet.class);
    }

    public static void build(Builder query, Builder mutation) {
        Map<String, BiConsumer<FacetRecord, Object>> updateTemplate = new HashMap<>();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    public static Facet fetch(DataFetchingEnvironment env, UUID id) {
        return new Facet(ctx(env).create()
                                 .selectFrom(Tables.FACET)
                                 .where(Tables.FACET.ID.equal(id))
                                 .fetchOne());
    }

    public static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(FacetType.getName())
                                   .type(FacetType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the job")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new Facet(fetch(env));
                                   })
                                   .build();
    }

    public static GraphQLFieldDefinition instances(GraphQLObjectType type) {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(type)
                                   .argument(newArgument().name(ID)
                                                          .description("facet ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return fetch(env);
                                   })
                                   .build();
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(FACET_STATE)
                                                                                .description("Facet creation/update state");
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
                                           .description("The name of the facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CLASSIFIER)
                                           .description("The relationship classifier of the facet")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CLASSIFICATION)
                                           .description("The existential classification of the facet")
                                           .build());
        return builder.build();
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        return newFieldDefinition().name("CreateFacet")
                                   .description("Create an instance of Facet")
                                   .type(new GraphQLTypeReference("Facet"))
                                   .argument(newArgument().name(FACET_STATE)
                                                          .description("the initial state of the job")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newFacet(env, createState,
                                                       updateTemplate);
                                   })
                                   .build();
    }

    private static FacetRecord fetch(DataFetchingEnvironment env) {
        return ctx(env).create()
                       .selectFrom(Tables.FACET)
                       .where(Tables.FACET.ID.equal(UUID.fromString((String) env.getArgument(ID))))
                       .fetchOne();
    }

    private static Object newFacet(DataFetchingEnvironment env,
                                   Map<String, Object> createState,
                                   Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        FacetRecord record = ctx(env).records()
                                     .newFacet();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new Facet(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name("DeleteFacet")
                                   .type(new GraphQLTypeReference(FacetType.getName()))
                                   .description("Delete the %s facet")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the facet instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        return newFieldDefinition().name("UpdateFacet")
                                   .type(new GraphQLTypeReference(FacetType.getName()))
                                   .description("Update the instance of a facet")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(type))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return updateFacet(env, updateState,
                                                          updateTemplate);
                                   })
                                   .build();
    }

    private static Facet updateFacet(DataFetchingEnvironment env,
                                     Map<String, Object> updateState,
                                     Map<String, BiConsumer<FacetRecord, Object>> updateTemplate) {
        FacetRecord facet = ctx(env).create()
                                    .selectFrom(Tables.FACET)
                                    .where(Tables.FACET.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                    .fetchOne();
        updateState.remove(ID);
        if (facet == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(facet,
                                                            updateState.get(k)));
        facet.update();
        return new Facet(facet);
    }

    private final FacetRecord record;

    public Facet(FacetRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AttributeAuthorizationTypeFunction.class)
    public List<AttributeAuthorization> getAttributes(DataFetchingEnvironment env) {
        return ctx(env).getPhantasmModel()
                       .getAttributeAuthorizations(record, false)
                       .stream()
                       .map(r -> new AttributeAuthorization(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAuthority(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAuthority()));
    }

    @GraphQLField
    @GraphQLType(NeworkAuthorizationTypeFunction.class)
    public List<NetworkAuthorization> getChildren(DataFetchingEnvironment env) {
        return ctx(env).getPhantasmModel()
                       .getNetworkAuthorizations(record, false)
                       .stream()
                       .map(r -> new NetworkAuthorization(r))
                       .collect(Collectors.toList());
    }

    @GraphQLField
    @GraphQLType(ExistentialTypeFunction.class)
    public Existential getClassification(DataFetchingEnvironment env) {
        ExistentialRuleform resolved = resolve(env, record.getClassifier());
        return wrap(resolved);
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getClassifier(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getClassifier()));
    }

    @GraphQLField
    public UUID getId() {
        return record.getId();
    }

    @GraphQLField
    public String getName() {
        return record.getName();
    }

    @GraphQLField
    public String getNotes() {
        return record.getNotes();
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersin() {
        return record.getVersion();
    }
}
