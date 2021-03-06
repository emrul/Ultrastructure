/**
 * Copyright 2016 Yurii Rashkovskii
 * Copyright 2016 Chiral Behaviors, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package com.chiralbehaviors.CoRE.phantasm.graphql;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Initializer;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.annotations.Connection;
import graphql.annotations.GraphQLConnection;
import graphql.annotations.GraphQLDataFetcher;
import graphql.annotations.GraphQLDefaultValue;
import graphql.annotations.GraphQLDeprecate;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLRelayMutation;
import graphql.annotations.RelayMutationMethodDataFetcher;
import graphql.annotations.TypeFunction;
import graphql.relay.Relay;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldDataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.TypeResolver;

/**
 * A utility class for extracting GraphQL data structures from annotated
 * elements.
 */
public class PhantasmProcessing {

    private static class ConnectionDataFetcher implements DataFetcher {
        private final DataFetcher             actualDataFetcher;
        private final Constructor<Connection> constructor;

        public ConnectionDataFetcher(Class<? extends Connection> connection,
                                     DataFetcher actualDataFetcher) {
            @SuppressWarnings("unchecked")
            Optional<Constructor<Connection>> constructor = Arrays.asList(connection.getConstructors())
                                                                  .stream()
                                                                  .filter(c -> c.getParameterCount() == 1)
                                                                  .map(c -> (Constructor<Connection>) c)
                                                                  .findFirst();
            if (constructor.isPresent()) {
                this.constructor = constructor.get();
            } else {
                throw new IllegalArgumentException(connection
                                                   + " doesn't have a single argument constructor");
            }
            this.actualDataFetcher = actualDataFetcher;
        }

        @Override
        public Object get(DataFetchingEnvironment environment) {
            // Exclude arguments
            DataFetchingEnvironment env = new DataFetchingEnvironment(environment.getSource(),
                                                                      new HashMap<>(),
                                                                      environment.getContext(),
                                                                      environment.getFields(),
                                                                      environment.getFieldType(),
                                                                      environment.getParentType(),
                                                                      environment.getGraphQLSchema());
            Connection conn;
            try {
                conn = constructor.newInstance(actualDataFetcher.get(env));
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
            return conn.get(environment);
        }
    }

    /**
     * Extract GraphQLInterfaceType from an interface
     *
     * @param iface
     *            interface
     * @param typeFunction
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     *             if <code>iface</code> is not an interface or doesn't have
     *             <code>@GraphTypeResolver</code> annotation
     */
    public static GraphQLInterfaceType iface(Class<?> iface,
                                             TypeResolver typeResolver,
                                             TypeFunction typeFunction) {
        GraphQLInterfaceType.Builder builder = ifaceBuilder(iface, typeResolver,
                                                            typeFunction);
        return builder.build();
    }

    public static GraphQLInterfaceType.Builder ifaceBuilder(Class<?> iface,
                                                            TypeResolver typeResolver,
                                                            TypeFunction typeFunction) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface + " is not an interface");
        }
        GraphQLInterfaceType.Builder builder = newInterface();

        GraphQLName name = iface.getAnnotation(GraphQLName.class);
        builder.name(name == null ? iface.getSimpleName() : name.value());
        GraphQLDescription description = iface.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        for (Method method : iface.getMethods()) {
            boolean valid = !Modifier.isStatic(method.getModifiers())
                            && method.getAnnotation(GraphQLField.class) != null;
            if (valid) {
                builder.field(field(method, typeFunction, null, null));
            }
        }
        builder.typeResolver(typeResolver);
        return builder;
    }

    public static GraphQLInputObjectType inputObject(GraphQLObjectType graphQLType) {
        GraphQLObjectType object = graphQLType;
        return new GraphQLInputObjectType(object.getName(),
                                          object.getDescription(),
                                          object.getFieldDefinitions()
                                                .stream()
                                                .map(field -> {
                                                    GraphQLOutputType type = field.getType();
                                                    GraphQLInputType inputType;
                                                    if (type instanceof GraphQLObjectType) {
                                                        inputType = inputObject((GraphQLObjectType) type);
                                                    } else {
                                                        inputType = (GraphQLInputType) type;
                                                    }

                                                    return new GraphQLInputObjectField(field.getName(),
                                                                                       field.getDescription(),
                                                                                       inputType,
                                                                                       null);
                                                })
                                                .collect(Collectors.toList()));
    }

    /**
     * Extract GraphQLObjectType from a class
     *
     * @param object
     * @param typeResolver
     * @param typeFunction
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     */
    public static GraphQLObjectType object(Class<?> object,
                                           TypeResolver typeResolver,
                                           TypeFunction typeFunction) {
        GraphQLObjectType.Builder builder = objectBuilder(object, typeResolver,
                                                          typeFunction);

        return builder.build();
    }

    public static GraphQLObjectType.Builder objectBuilder(Class<?> object,
                                                          TypeResolver typeResolver,
                                                          TypeFunction typeFunction) {
        GraphQLObjectType.Builder builder = newObject();
        GraphQLName name = object.getAnnotation(GraphQLName.class);
        builder.name(name == null ? object.getSimpleName() : name.value());
        GraphQLDescription description = object.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        for (Method method : object.getMethods()) {

            Class<?> declaringClass = getDeclaringClass(method);

            boolean valid;
            try {
                valid = !Modifier.isStatic(method.getModifiers())
                        && (method.getAnnotation(GraphQLField.class) != null
                            || declaringClass.getMethod(method.getName(),
                                                        method.getParameterTypes())
                                             .getAnnotation(GraphQLField.class) != null);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }

            if (valid) {
                builder.field(field(method, typeFunction, null, null));
            }
        }
        for (Field field : object.getFields()) {
            boolean valid = !Modifier.isStatic(field.getModifiers())
                            && field.getAnnotation(GraphQLField.class) != null;
            if (valid) {
                builder.field(field(field, typeFunction));
            }
        }
        Class<?> current = object;
        do {
            for (Class<?> iface : current.getInterfaces()) {
                if (iface.getAnnotation(GraphQLInterface.class) != null) {
                    builder.withInterface((GraphQLInterfaceType) typeFunction.apply(iface,
                                                                                    null));
                }
            }
            current = current.getSuperclass();
        } while (current != null);
        return builder;
    }

    public static List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> processPlugin(Class<?> plugin,
                                                                                               TypeResolver typeResolver,
                                                                                               TypeFunction typeFunction,
                                                                                               GraphQLObjectType.Builder builder) {
        Plugin annotation = plugin.getAnnotation(Plugin.class);
        if (annotation == null) {
            throw new IllegalArgumentException(String.format("Class not annotated with @Plugin: %s",
                                                             plugin.getCanonicalName()));
        }
        List<BiConsumer<DataFetchingEnvironment, ExistentialRuleform>> initializers = new ArrayList<>();
        Class<? extends Phantasm> phantasm = annotation.value();
        Object instance;
        try {
            instance = plugin.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Unable to instantiate: %s",
                                                             plugin),
                                               e);
        }
        for (Method method : plugin.getMethods()) {

            Class<?> declaringClass = getDeclaringClass(method);

            boolean valid;
            try {
                valid = !Modifier.isStatic(method.getModifiers())
                        && (method.getAnnotation(GraphQLField.class) != null
                            || declaringClass.getMethod(method.getName(),
                                                        method.getParameterTypes())
                                             .getAnnotation(GraphQLField.class) != null);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }

            if (valid) {
                builder.field(field(method, typeFunction, phantasm, instance));
            } else if (method.getAnnotation(Initializer.class) != null) {
                InlinedFunction initializer = new InlinedFunction(method,
                                                                  Collections.emptyMap(),
                                                                  phantasm,
                                                                  instance);
                initializers.add((env,
                                  rf) -> initializer.get(env,
                                                         WorkspaceSchema.ctx(env)
                                                                        .wrap(phantasm,
                                                                              rf),
                                                         true));
            }
        }
        return initializers;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T convert(Number from, Class<T> to) {
        if (from == null) {
            return null;
        }
        if (to.equals(Byte.class)) {
            return (T) Byte.valueOf(from.byteValue());
        }
        if (to.equals(Double.class)) {
            return (T) Double.valueOf(from.doubleValue());
        }
        if (to.equals(Float.class)) {
            return (T) Float.valueOf(from.floatValue());
        }
        if (to.equals(Integer.class)) {
            return (T) Integer.valueOf(from.intValue());
        }
        if (to.equals(Long.class)) {
            return (T) Long.valueOf(from.longValue());
        }
        if (to.equals(Short.class)) {
            return (T) Short.valueOf(from.shortValue());
        }
        return null;
    }

    private static Class<?> getDeclaringClass(Method method) {
        Class<?> object = method.getDeclaringClass();
        Class<?> declaringClass = object;
        for (Class<?> iface : object.getInterfaces()) {
            try {
                iface.getMethod(method.getName(), method.getParameterTypes());
                declaringClass = iface;
            } catch (NoSuchMethodException e) {
            }
        }

        try {
            if (object.getSuperclass() != null) {
                object.getSuperclass()
                      .getMethod(method.getName(), method.getParameterTypes());
                declaringClass = object.getSuperclass();
            }
        } catch (NoSuchMethodException e) {
        }
        return declaringClass;

    }

    private static GraphQLOutputType getGraphQLConnection(boolean isConnection,
                                                          AccessibleObject field,
                                                          GraphQLOutputType type,
                                                          GraphQLOutputType outputType,
                                                          GraphQLFieldDefinition.Builder builder) {
        if (isConnection) {
            if (type instanceof GraphQLList) {
                graphql.schema.GraphQLType wrappedType = ((GraphQLList) type).getWrappedType();
                assert wrappedType instanceof GraphQLObjectType;
                String annValue = field.getAnnotation(GraphQLConnection.class)
                                       .name();
                String connectionName = annValue.isEmpty() ? wrappedType.getName()
                                                           : annValue;
                Relay relay = new Relay();
                GraphQLObjectType edgeType = relay.edgeType(connectionName,
                                                            (GraphQLOutputType) wrappedType,
                                                            null,
                                                            Collections.<GraphQLFieldDefinition> emptyList());
                outputType = relay.connectionType(connectionName, edgeType,
                                                  Collections.emptyList());
                builder.argument(relay.getConnectionFieldArguments());
            }
        }
        return outputType;
    }

    @SuppressWarnings("unchecked")
    private static Function<Map<String, Object>, Object> inputTxfm(Class<?> t,
                                                                   GraphQLObjectType object) {
        List<BiConsumer<Map<String, Object>, Object>> txfms = new ArrayList<>();
        for (GraphQLFieldDefinition f : object.getFieldDefinitions()) {
            Field field;
            try {
                field = t.getField(f.getName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            field.setAccessible(true);
            if (Number.class.isAssignableFrom(field.getType())) {
                txfms.add((m, in) -> {
                    try {
                        field.set(in,
                                  convert((Number) m.get(f.getName()),
                                          (Class<? extends Number>) field.getType()));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            } else {
                txfms.add((m, in) -> {
                    try {
                        field.set(in, m.get(f.getName()));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        }
        return m -> {
            Object in;
            try {
                in = t.getConstructor()
                      .newInstance();
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            txfms.forEach(f -> f.accept(m, in));
            return in;
        };
    }

    private static boolean isConnection(AccessibleObject obj, Class<?> klass,
                                        GraphQLOutputType type) {
        return obj.isAnnotationPresent(GraphQLConnection.class)
               && type instanceof GraphQLList
               && ((GraphQLList) type).getWrappedType() instanceof GraphQLObjectType;
    }

    protected static GraphQLArgument argument(Parameter parameter,
                                              graphql.schema.GraphQLType t) throws IllegalAccessException,
                                                                            InstantiationException {
        GraphQLArgument.Builder builder = newArgument();
        builder.name(parameter.getName());
        builder.type(parameter.getAnnotation(NotNull.class) == null ? (GraphQLInputType) t
                                                                    : new graphql.schema.GraphQLNonNull(t));
        GraphQLDescription description = parameter.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }
        GraphQLDefaultValue defaultValue = parameter.getAnnotation(GraphQLDefaultValue.class);
        if (defaultValue != null) {
            builder.defaultValue(defaultValue.value()
                                             .newInstance()
                                             .get());
        }
        GraphQLName name = parameter.getAnnotation(GraphQLName.class);
        if (name != null) {
            builder.name(name.value());
        }
        return builder.build();
    }

    protected static GraphQLFieldDefinition field(Field field,
                                                  TypeFunction typeFunction) {
        GraphQLFieldDefinition.Builder builder = newFieldDefinition();
        GraphQLName name = field.getAnnotation(GraphQLName.class);
        builder.name(name == null ? field.getName() : name.value());
        GraphQLOutputType type = (GraphQLOutputType) typeFunction.apply(field.getType(),
                                                                        field.getAnnotatedType());

        GraphQLOutputType outputType = field.getAnnotation(NotNull.class) == null ? type
                                                                                  : new GraphQLNonNull(type);

        boolean isConnection = isConnection(field, field.getType(), type);
        outputType = getGraphQLConnection(isConnection, field, type, outputType,
                                          builder);

        builder.type(outputType);

        GraphQLDescription description = field.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }

        GraphQLDeprecate deprecate = field.getAnnotation(GraphQLDeprecate.class);
        if (deprecate != null) {
            builder.deprecate(deprecate.value());
        }
        if (field.getAnnotation(Deprecated.class) != null) {
            builder.deprecate("Deprecated");
        }

        GraphQLDataFetcher dataFetcher = field.getAnnotation(GraphQLDataFetcher.class);
        DataFetcher actualDataFetcher;
        try {
            actualDataFetcher = dataFetcher == null ? new FieldDataFetcher(field.getName())
                                                    : dataFetcher.value()
                                                                 .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        if (isConnection) {
            actualDataFetcher = new ConnectionDataFetcher(field.getAnnotation(GraphQLConnection.class)
                                                               .connection(),
                                                          actualDataFetcher);
        }

        builder.dataFetcher(actualDataFetcher);

        return builder.build();
    }

    protected static GraphQLFieldDefinition field(Method method,
                                                  TypeFunction typeFunction,
                                                  Class<?> phantasm,
                                                  Object instance) {
        GraphQLFieldDefinition.Builder builder = newFieldDefinition();

        String name = method.getName()
                            .replaceFirst("^(is|get|set)(.+)", "$2");
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        GraphQLName nameAnn = method.getAnnotation(GraphQLName.class);
        builder.name(nameAnn == null ? name : nameAnn.value());
        AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

        GraphQLOutputType type;
        if (method.getReturnType()
                  .isAnnotationPresent(Facet.class)) {
            type = new GraphQLTypeReference(WorkspacePresentation.toTypeName(method.getReturnType()
                                                                                   .getSimpleName()));
        } else {
            type = (GraphQLOutputType) typeFunction.apply(method.getReturnType(),
                                                          annotatedReturnType);
        }

        GraphQLOutputType outputType = method.getAnnotation(NotNull.class) == null ? type
                                                                                   : new GraphQLNonNull(type);

        boolean isConnection = isConnection(method, method.getReturnType(),
                                            type);
        outputType = getGraphQLConnection(isConnection, method, type,
                                          outputType, builder);

        builder.type(outputType);

        Map<Integer, Function<Map<String, Object>, Object>> inputTxfms = new HashMap<>();
        AtomicInteger i = new AtomicInteger(-1);
        List<GraphQLArgument> args = Arrays.asList(method.getParameters())
                                           .stream()
                                           .peek(e -> i.incrementAndGet())
                                           .filter(p -> !DataFetchingEnvironment.class.isAssignableFrom(p.getType()))
                                           .filter(p -> !PhantasmCRUD.class.isAssignableFrom(p.getType()))
                                           .filter(p -> !p.getType()
                                                          .isAnnotationPresent(Facet.class))
                                           .filter(p -> !p.getType()
                                                          .isAnnotationPresent(Facet.class))
                                           .map(new Function<Parameter, GraphQLArgument>() {
                                               @Override
                                               public GraphQLArgument apply(Parameter parameter) {
                                                   Class<?> t = parameter.getType();
                                                   graphql.schema.GraphQLType graphQLType = typeFunction.apply(t,
                                                                                                               parameter.getAnnotatedType());
                                                   if (graphQLType instanceof GraphQLObjectType) {
                                                       GraphQLObjectType objectType = (GraphQLObjectType) graphQLType;
                                                       GraphQLInputObjectType inputObject = inputObject(objectType);
                                                       graphQLType = inputObject;
                                                       inputTxfms.put(i.get(),
                                                                      inputTxfm(t,
                                                                                objectType));
                                                   }
                                                   try {
                                                       return argument(parameter,
                                                                       graphQLType);
                                                   } catch (
                                                           IllegalAccessException
                                                           | InstantiationException e) {
                                                       throw new IllegalStateException(e);
                                                   }
                                               }
                                           })
                                           .collect(Collectors.toList());

        GraphQLFieldDefinition relay = null;
        if (method.isAnnotationPresent(GraphQLRelayMutation.class)) {
            if (!(outputType instanceof GraphQLObjectType)) {
                throw new RuntimeException("outputType should be an object");
            }
            StringBuffer titleBuffer = new StringBuffer(method.getName());
            titleBuffer.setCharAt(0,
                                  Character.toUpperCase(titleBuffer.charAt(0)));
            String title = titleBuffer.toString();
            relay = new Relay().mutationWithClientMutationId(title,
                                                             method.getName(),
                                                             args.stream()
                                                                 .map(t -> new GraphQLInputObjectField(t.getName(),
                                                                                                       t.getType()))
                                                                 .collect(Collectors.toList()),
                                                             ((GraphQLObjectType) outputType).getFieldDefinitions(),
                                                             null);
            builder.argument(relay.getArguments());
            builder.type(relay.getType());
        } else {
            builder.argument(args);
        }

        GraphQLDescription description = method.getAnnotation(GraphQLDescription.class);
        if (description != null) {
            builder.description(description.value());
        }

        GraphQLDeprecate deprecate = method.getAnnotation(GraphQLDeprecate.class);
        if (deprecate != null) {
            builder.deprecate(deprecate.value());
        }
        if (method.getAnnotation(Deprecated.class) != null) {
            builder.deprecate("Deprecated");
        }

        GraphQLDataFetcher dataFetcher = method.getAnnotation(GraphQLDataFetcher.class);
        DataFetcher actualDataFetcher;
        try {
            actualDataFetcher = dataFetcher == null ? phantasm == null ? new ReflectiveDataFetcher(method,
                                                                                                   inputTxfms)
                                                                       : new InlinedFunction(method,
                                                                                             inputTxfms,
                                                                                             phantasm,
                                                                                             instance)
                                                    : dataFetcher.value()
                                                                 .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        if (method.isAnnotationPresent(GraphQLRelayMutation.class)
            && relay != null) {
            actualDataFetcher = new RelayMutationMethodDataFetcher(method, args,
                                                                   relay.getArgument("input")
                                                                        .getType(),
                                                                   relay.getType());
        }

        if (isConnection) {
            actualDataFetcher = new ConnectionDataFetcher(method.getAnnotation(GraphQLConnection.class)
                                                                .connection(),
                                                          actualDataFetcher);
        }

        builder.dataFetcher(actualDataFetcher);

        return builder.build();
    }
}
