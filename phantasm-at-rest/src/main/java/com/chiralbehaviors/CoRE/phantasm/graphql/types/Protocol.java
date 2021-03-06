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

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.math.BigDecimal;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Unit;

import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Protocol {

    public static class ProtocolState {
        @GraphQLField
        public String assignTo;
        @GraphQLField
        public String authority;
        @GraphQLField
        public String childAssignTo;
        @GraphQLField
        public String childDeliverFrom;
        @GraphQLField
        public String childDeliverTo;
        @GraphQLField
        public String childProduct;
        @GraphQLField
        public Float  childQuantity;
        @GraphQLField
        public String childrenRelationship;
        @GraphQLField
        public String childService;
        @GraphQLField
        public String childStatus;
        @GraphQLField
        public String childUnit;
        @GraphQLField
        public String deliverFrom;
        @GraphQLField
        public String deliverTo;
        @GraphQLField
        public String notes;
        @GraphQLField
        public String product;
        @GraphQLField
        public Float  quantity;
        @GraphQLField
        public String requester;
        @GraphQLField
        public String service;
        @GraphQLField
        public String status;
        @GraphQLField
        public String unit;

        public void update(ProtocolRecord r) {
            if (authority != null) {
                r.setAuthority(UUID.fromString(authority));
            }
            if (assignTo != null) {
                r.setAssignTo(UUID.fromString(assignTo));
            }
            if (deliverFrom != null) {
                r.setDeliverFrom(UUID.fromString(deliverFrom));
            }
            if (deliverTo != null) {
                r.setDeliverTo(UUID.fromString(deliverTo));
            }
            if (notes != null) {
                r.setNotes(notes);
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (quantity != null) {
                r.setQuantity(BigDecimal.valueOf(quantity));
            }
            if (product != null) {
                r.setProduct(UUID.fromString(product));
            }
            if (requester != null) {
                r.setRequester(UUID.fromString(requester));
            }
            if (service != null) {
                r.setService(UUID.fromString(service));
            }
            if (status != null) {
                r.setStatus(UUID.fromString(status));
            }
            if (unit != null) {
                r.setQuantityUnit(UUID.fromString(unit));
            }
            if (childAssignTo != null) {
                r.setChildAssignTo(UUID.fromString(childAssignTo));
            }
            if (childDeliverFrom != null) {
                r.setChildDeliverFrom(UUID.fromString(childDeliverFrom));
            }
            if (childDeliverTo != null) {
                r.setChildDeliverTo(UUID.fromString(childDeliverTo));
            }
            if (childProduct != null) {
                r.setChildProduct(UUID.fromString(childProduct));
            }
            if (childQuantity != null) {
                r.setChildQuantity(BigDecimal.valueOf(childQuantity));
            }
            if (childProduct != null) {
                r.setChildProduct(UUID.fromString(childProduct));
            }
            if (childService != null) {
                r.setChildService(UUID.fromString(childService));
            }
            if (childStatus != null) {
                r.setChildStatus(UUID.fromString(childStatus));
            }
            if (childUnit != null) {
                r.setChildQuantityUnit(UUID.fromString(childUnit));
            }
            if (childrenRelationship != null) {
                r.setChildrenRelationship(UUID.fromString(childrenRelationship));
            }
        }
    }

    public static class ProtocolUpdateState extends ProtocolState {
        @GraphQLField
        public String id;
    }

    public static ProtocolRecord fetch(DataFetchingEnvironment env, UUID id) {
        return WorkspaceSchema.ctx(env)
                              .create()
                              .selectFrom(Tables.PROTOCOL)
                              .where(Tables.PROTOCOL.ID.equal(id))
                              .fetchOne();
    }

    private final ProtocolRecord record;

    public Protocol(ProtocolRecord record) {
        assert record != null;
        this.record = record;
    }

    @GraphQLField

    public Agency getAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getAssignTo()));
    }

    @GraphQLField
    public Agency getAuthority(DataFetchingEnvironment env) {
        ExistentialRecord a = resolve(env, record.getAuthority());
        if (a == null) {
            return null;
        }
        return new Agency(a);
    }

    @GraphQLField

    public Agency getChildAssignTo(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getChildAssignTo()));
    }

    @GraphQLField

    public Location getChildDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverFrom()));
    }

    @GraphQLField

    public Location getChildDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getChildDeliverTo()));
    }

    @GraphQLField

    public Product getChildProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildProduct()));
    }

    @GraphQLField
    public Long getChildQuantity() {
        if (record.getChildQuantity() == null) {
            return null;
        }
        return record.getChildQuantity()
                     .longValue();
    }

    @GraphQLField

    public Unit getChildQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getChildQuantityUnit()));
    }

    @GraphQLField
    public Relationship getChildrenRelationship(DataFetchingEnvironment env) {
        return new Relationship(resolve(env, record.getChildrenRelationship()));
    }

    @GraphQLField

    public Product getChildService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getChildService()));
    }

    @GraphQLField

    public StatusCode getChildStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getChildStatus()));
    }

    @GraphQLField

    public Location getDeliverFrom(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverFrom()));
    }

    @GraphQLField

    public Location getDeliverTo(DataFetchingEnvironment env) {
        return new Location(resolve(env, record.getDeliverTo()));
    }

    @GraphQLField
    public String getId() {
        return record.getId()
                     .toString();
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

    public Product getProduct(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getProduct()));
    }

    @GraphQLField
    public Long getQuantity() {
        if (record.getQuantity() == null) {
            return null;
        }
        return record.getQuantity()
                     .longValue();
    }

    @GraphQLField

    public Unit getQuantityUnit(DataFetchingEnvironment env) {
        return new Unit(resolve(env, record.getQuantityUnit()));
    }

    @GraphQLField

    public Agency getRequester(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getRequester()));
    }

    @GraphQLField

    public Product getService(DataFetchingEnvironment env) {
        return new Product(resolve(env, record.getService()));
    }

    @GraphQLField

    public StatusCode getStatus(DataFetchingEnvironment env) {
        return new StatusCode(resolve(env, record.getStatus()));
    }

    @GraphQLField

    public Agency getUpdatedBy(DataFetchingEnvironment env) {
        return new Agency(resolve(env, record.getUpdatedBy()));
    }

    @GraphQLField
    public Integer getVersion(DataFetchingEnvironment env) {
        return record.getVersion();
    }
}
