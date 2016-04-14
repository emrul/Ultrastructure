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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.lang.reflect.AnnotatedType;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.ExistentialResolver;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.GraphQLDataFetcher;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.GraphQLTypeResolver;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

/**
 * @author hhildebrand
 *
 */
@GraphQLTypeResolver(ExistentialResolver.class)
public interface Existential {

    @GraphQLDescription("The Agency existential ruleform")
    abstract class Agency implements Existential {
    }

    class AgencyTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return AgencyType;
        }
    }

    @GraphQLDescription("The Attribute existential ruleform")
    abstract class Attribute implements Existential {
    }

    class ExistentialResolver implements TypeResolver {

        @Override
        public GraphQLObjectType getType(Object object) {
            ExistentialRecord record = (ExistentialRecord) object; // If this doesn't succeed, it's a bug, so no catch
            switch (record.getDomain()) {
                case Agency:
                    return AgencyType;
                case Attribute:
                    return AttributeType;
                case Interval:
                    return IntervalType;
                case Location:
                    return LocationType;
                case Product:
                    return ProductType;
                case Relationship:
                    return RelationshipType;
                case StatusCode:
                    return StatusCodeType;
                case Unit:
                    return UnitType;
                default:
                    throw new IllegalStateException(String.format("invalid domain: %s",
                                                                  record.getDomain()));
            }
        }

    }

    @GraphQLDescription("The Interval existential ruleform")
    abstract class Interval implements Existential {
    }

    @GraphQLDescription("The Location existential ruleform")
    abstract class Location implements Existential {
    }

    class LocationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return LocationType;
        }
    }

    @GraphQLDescription("The Product existential ruleform")
    abstract class Product implements Existential {
    }

    class ProductTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ProductType;
        }
    }

    @GraphQLDescription("The Relationship existential ruleform")
    abstract class Relationship implements Existential {
    }

    class RelationshipTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return RelationshipType;
        }
    }

    @GraphQLDescription("The StatusCode existential ruleform")
    abstract class StatusCode implements Existential {
    }

    class StatusCodeTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return StatusCodeType;
        }
    }

    @GraphQLDescription("The Unit existential ruleform")
    abstract class Unit implements Existential {
    }

    class UnitTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return UnitType;
        }
    }

    class UpdatedByFetcher implements DataFetcher {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            return ctx(environment).records()
                                   .resolve(((ExistentialRecord) environment.getSource()).getUpdatedBy());
        }

    }

    GraphQLObjectType AgencyType       = objectTypeOf(Agency.class);

    GraphQLObjectType AttributeType    = objectTypeOf(Attribute.class);

    GraphQLObjectType IntervalType     = objectTypeOf(Interval.class);

    GraphQLObjectType LocationType     = objectTypeOf(Location.class);

    GraphQLObjectType ProductType      = objectTypeOf(Product.class);

    GraphQLObjectType RelationshipType = objectTypeOf(Relationship.class);

    GraphQLObjectType StatusCodeType   = objectTypeOf(StatusCode.class);

    GraphQLObjectType UnitType         = objectTypeOf(Unit.class);

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    public static GraphQLObjectType objectTypeOf(Class<?> clazz) {
        try {
            return GraphQLAnnotations.object(clazz);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Unable to create object type for %s",
                                                          clazz.getSimpleName()));
        }
    }

    @GraphQLField
    String getDescription();

    @GraphQLField
    UUID getId();

    @GraphQLField
    String getName();

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    @GraphQLDataFetcher(UpdatedByFetcher.class)
    Agency getUpdatedBy(DataFetchingEnvironment env);

}
