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
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.AgencyTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.LocationTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ProductTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.RelationshipTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCodeTypeFunction;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.UnitTypeFunction;

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
public class Protocol {

    class ProtocolTypeFunction implements TypeFunction {

        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ProtocolType;
        }
    }

    public static final GraphQLObjectType ProtocolType              = Existential.objectTypeOf(Protocol.class);

    private static final String           CREATE                    = "CreateProtocol";
    private static final String           DELETE                    = "DeleteProtocol";
    private static final String           ID                        = "id";
    private static final String           IDS                       = "ids";
    private static final String           INSTANCES_OF_QUERY        = "InstancesOfProtocol";
    private static final String           PROTOCOL_STATE            = "ProtocolState";
    private static final String           SET_ASSIGN_TO             = "setAssignTo";
    private static final String           SET_CHILD_ASSIGN_TO       = "setChildAssignTo";
    private static final String           SET_CHILD_DELIVER_FROM    = "setChildDeliverFrom";
    private static final String           SET_CHILD_DELIVER_TO      = "setChildDeliverTo";
    private static final String           SET_CHILD_PRODUCT         = "setChildProduct";
    private static final String           SET_CHILD_QUANTITY        = "setChildQuantity";
    private static final String           SET_CHILD_QUANTITY_UNIT   = "setChildQuantityUnit";
    private static final String           SET_CHILD_SERVICE         = "setChildService";
    private static final String           SET_CHILD_STATUS          = "setChildStatus";
    private static final String           SET_CHILDREN_RELATIONSHIP = "setChildrenRelationship";
    private static final String           SET_DELIVER_FROM          = "setDeliverFrom";
    private static final String           SET_DELIVER_TO            = "setDeliverTo";
    private static final String           SET_NAME                  = "setName";
    private static final String           SET_NOTES                 = "setNotes";
    private static final String           SET_PRODUCT               = "setProduct";
    private static final String           SET_QUANTITY              = "setQuantity";
    private static final String           SET_QUANTITY_UNIT         = "setQuantityUnit";
    private static final String           SET_REQUESTER             = "setRequester";
    private static final String           SET_SERVICE               = "setService";
    private static final String           SET_STATUS                = "setStatus";
    private static final String           STATE                     = "state";
    private static final String           UPDATE                    = "UpdateProtocol";

    public static void build(Builder query, Builder mutation,
                             ThreadLocal<Product> currentWorkspace) {
        Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate = buildUpdateTemplate();
        GraphQLInputObjectType stateType = buildStateType();

        query.field(instance());
        query.field(instances());
        mutation.field(create(stateType, updateTemplate));
        mutation.field(update(stateType, updateTemplate));
        mutation.field(remove());
    }

    private static GraphQLInputObjectType buildStateType() {
        graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(PROTOCOL_STATE)
                                                                                .description("Protocol creation/update state");
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_NOTES)
                                           .description("The notes of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_NAME)
                                           .description("The name of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_PRODUCT)
                                           .description("The product of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DELIVER_FROM)
                                           .description("The deliver from location of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_DELIVER_TO)
                                           .description("The deliver to location of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLFloat)
                                           .name(SET_QUANTITY)
                                           .description("The quqntity of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_QUANTITY_UNIT)
                                           .description("The quantity unit of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_REQUESTER)
                                           .description("The requester of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_SERVICE)
                                           .description("The service of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_STATUS)
                                           .description("The status of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_ASSIGN_TO)
                                           .description("The assigned agency of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CHILD_DELIVER_FROM)
                                           .description("The child deliver from location of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CHILD_DELIVER_TO)
                                           .description("The child deliver to location of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLString)
                                           .name(SET_CHILD_PRODUCT)
                                           .description("The child product of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(GraphQLFloat)
                                           .name(SET_CHILD_QUANTITY)
                                           .description("The child quqntity of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILD_QUANTITY_UNIT)
                                           .description("The child quantity unit of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILDREN_RELATIONSHIP)
                                           .description("The children relationship of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILD_SERVICE)
                                           .description("The child service of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILD_STATUS)
                                           .description("The child status of the  protocol")
                                           .build());
        builder.field(newInputObjectField().type(new GraphQLNonNull(GraphQLString))
                                           .name(SET_CHILD_ASSIGN_TO)
                                           .description("The child assigned agency of the  protocol")
                                           .build());
        return builder.build();
    }

    private static Map<String, BiConsumer<ProtocolRecord, Object>> buildUpdateTemplate() {
        Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate = new HashMap<>();
        updateTemplate.put(SET_NAME, (e, value) -> e.setName((String) value));
        updateTemplate.put(SET_NOTES, (e, value) -> e.setNotes((String) value));
        updateTemplate.put(SET_ASSIGN_TO,
                           (e,
                            value) -> e.setAssignTo(UUID.fromString((String) value)));
        updateTemplate.put(SET_QUANTITY,
                           (e,
                            value) -> e.setQuantity(BigDecimal.valueOf((float) value)));
        updateTemplate.put(SET_QUANTITY_UNIT,
                           (e,
                            value) -> e.setQuantityUnit(UUID.fromString((String) value)));
        updateTemplate.put(SET_DELIVER_FROM,
                           (e,
                            value) -> e.setDeliverFrom(UUID.fromString((String) value)));
        updateTemplate.put(SET_DELIVER_TO,
                           (e,
                            value) -> e.setDeliverTo(UUID.fromString((String) value)));
        updateTemplate.put(SET_PRODUCT,
                           (e,
                            value) -> e.setProduct(UUID.fromString((String) value)));
        updateTemplate.put(SET_REQUESTER,
                           (e,
                            value) -> e.setRequester(UUID.fromString((String) value)));
        updateTemplate.put(SET_SERVICE,
                           (e,
                            value) -> e.setService(UUID.fromString((String) value)));
        updateTemplate.put(SET_STATUS,
                           (e,
                            value) -> e.setStatus(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_QUANTITY,
                           (e,
                            value) -> e.setChildQuantity(BigDecimal.valueOf((float) value)));
        updateTemplate.put(SET_CHILD_QUANTITY_UNIT,
                           (e,
                            value) -> e.setChildQuantityUnit(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_DELIVER_FROM,
                           (e,
                            value) -> e.setChildDeliverFrom(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_DELIVER_TO,
                           (e,
                            value) -> e.setChildDeliverTo(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_PRODUCT,
                           (e,
                            value) -> e.setChildProduct(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILDREN_RELATIONSHIP,
                           (e,
                            value) -> e.setChildrenRelationship(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_SERVICE,
                           (e,
                            value) -> e.setChildService(UUID.fromString((String) value)));
        updateTemplate.put(SET_CHILD_STATUS,
                           (e,
                            value) -> e.setChildStatus(UUID.fromString((String) value)));
        return updateTemplate;
    }

    private static GraphQLFieldDefinition create(GraphQLInputObjectType createType,
                                                 Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(CREATE)
                                   .description("Create an instance of  protocol")
                                   .type(new GraphQLTypeReference(ProtocolType.getName()))
                                   .argument(newArgument().name(STATE)
                                                          .description("the initial state of the  protocol")
                                                          .type(new GraphQLNonNull(createType))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> createState = (Map<String, Object>) env.getArgument(STATE);
                                       return newAuth(env, createState,
                                                      updateTemplate);
                                   })
                                   .build();
    }

    private static ProtocolRecord fetch(DataFetchingEnvironment env) {
        return fetch(env, (String) env.getArgument(ID));
    }

    private static ProtocolRecord fetch(DataFetchingEnvironment env,
                                        String id) {
        return ctx(env).create()
                       .selectFrom(Tables.PROTOCOL)
                       .where(Tables.PROTOCOL.ID.equal(UUID.fromString(id)))
                       .fetchOne();
    }

    private static GraphQLFieldDefinition instance() {
        return newFieldDefinition().name(ProtocolType.getName())
                                   .type(ProtocolType)
                                   .argument(newArgument().name(ID)
                                                          .description("id of the protocol")
                                                          .type(new GraphQLNonNull(GraphQLString))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return new Protocol(fetch(env));
                                   })
                                   .build();
    }

    @SuppressWarnings("unchecked")
    private static GraphQLFieldDefinition instances() {
        return newFieldDefinition().name(INSTANCES_OF_QUERY)
                                   .type(new GraphQLList(ProtocolType))
                                   .argument(newArgument().name(IDS)
                                                          .description("protocol ids")
                                                          .type(new GraphQLNonNull(new GraphQLList(GraphQLString)))
                                                          .build())
                                   .dataFetcher(env -> {
                                       return ((List<String>) env.getArgument(IDS)).stream()
                                                                                   .map(id -> Protocol.fetch(env,
                                                                                                             id))
                                                                                   .collect(Collectors.toList());
                                   })
                                   .build();
    }

    private static Object newAuth(DataFetchingEnvironment env,
                                  Map<String, Object> createState,
                                  Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate) {
        ProtocolRecord record = ctx(env).records()
                                        .newProtocol();
        createState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(record,
                                                            createState.get(k)));
        record.insert();
        return new Protocol(record);
    }

    private static GraphQLFieldDefinition remove() {
        return newFieldDefinition().name(DELETE)
                                   .type(new GraphQLTypeReference(ProtocolType.getName()))
                                   .description("Delete the %s protocol")
                                   .argument(newArgument().name(ID)
                                                          .description("the id of the protocol instance")
                                                          .type(GraphQLString)
                                                          .build())
                                   .dataFetcher(env -> fetch(env).delete())
                                   .build();
    }

    private static Protocol update(DataFetchingEnvironment env,
                                   Map<String, Object> updateState,
                                   Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate) {
        ProtocolRecord auth = ctx(env).create()
                                      .selectFrom(Tables.PROTOCOL)
                                      .where(Tables.PROTOCOL.ID.equal(UUID.fromString((String) updateState.get(ID))))
                                      .fetchOne();
        updateState.remove(ID);
        if (auth == null) {
            return null;
        }
        updateState.forEach((k, v) -> updateTemplate.get(k)
                                                    .accept(auth,
                                                            updateState.get(k)));
        auth.update();
        return new Protocol(auth);
    }

    private static GraphQLFieldDefinition update(GraphQLInputObjectType type,
                                                 Map<String, BiConsumer<ProtocolRecord, Object>> updateTemplate) {
        return newFieldDefinition().name(UPDATE)
                                   .type(new GraphQLTypeReference(ProtocolType.getName()))
                                   .description("Update the instance of a protocol")
                                   .argument(newArgument().name(STATE)
                                                          .description("the update state to apply")
                                                          .type(new GraphQLNonNull(type))
                                                          .build())
                                   .dataFetcher(env -> {
                                       @SuppressWarnings("unchecked")
                                       Map<String, Object> updateState = (Map<String, Object>) env.getArgument(STATE);
                                       return update(env, updateState,
                                                     updateTemplate);
                                   })
                                   .build();
    }

    private final ProtocolRecord record;

    public Protocol(ProtocolRecord record) {
        this.record = record;
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getChildAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getChildAssignTo()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getChildDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getChildDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverTo()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getChildProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildProduct()));
    }

    @GraphQLField
    public Long getChildQuantity() {
        return record.getChildQuantity()
                     .longValue();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getChildQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getChildQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(RelationshipTypeFunction.class)
    public Relationship getChildrenRelationship(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getChildrenRelationship()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getChildService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildService()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getChildStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChildStatus()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField
    @GraphQLType(LocationTypeFunction.class)
    public Location getDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverTo()));
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
    @GraphQLType(ProductTypeFunction.class)
    public Product getProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getProduct()));
    }

    @GraphQLField
    public Long getQuantity() {
        return record.getQuantity()
                     .longValue();
    }

    @GraphQLField
    @GraphQLType(UnitTypeFunction.class)
    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getRequester(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getRequester()));
    }

    @GraphQLField
    @GraphQLType(ProductTypeFunction.class)
    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getService()));
    }

    @GraphQLField
    @GraphQLType(StatusCodeTypeFunction.class)
    public StatusCode getStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getStatus()));
    }

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return record.getVersion();
    }
}
