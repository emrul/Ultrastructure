/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultRecordListenerProvider;
import org.jooq.util.postgres.PostgresDSL;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class ModelImpl implements Model {
    private final static ConcurrentMap<Class<? extends Phantasm>, PhantasmDefinition> cache = new ConcurrentHashMap<>();

    public static PhantasmDefinition cached(Class<? extends Phantasm> phantasm,
                                            Model model) {
        return cache.computeIfAbsent(phantasm,
                                     p -> new PhantasmDefinition(p, model));
    }

    public static void clearPhantasmCache() {
        cache.clear();
    }

    public static Configuration configuration() {
        Configuration configuration = new DefaultConfiguration().set(SQLDialect.POSTGRES_9_5);
        Settings settings = new Settings();
        settings.setExecuteWithOptimisticLocking(true);
        settings.withRenderFormatted(false);
        configuration.set(settings);
        return configuration;
    }

    public static Configuration configuration(Connection connection) throws SQLException {
        Configuration configuration = configuration();
        connection.setAutoCommit(false);
        configuration.set(connection);
        return configuration;
    }

    public static DSLContext newCreate(Connection connection) throws SQLException {
        return PostgresDSL.using(configuration(connection));
    }

    private final Animations     animations;
    private final DSLContext     create;
    private AuthorizedPrincipal  currentPrincipal;
    private final RecordsFactory factory;
    private final JobModel       jobModel;
    private final Kernel         kernel;
    private final PhantasmModel  phantasmModel;
    private final WorkspaceModel workspaceModel;

    public ModelImpl(Connection connection) throws SQLException {
        this(newCreate(connection));
    }

    public ModelImpl(DSLContext create) {
        animations = new Animations(this, new Inference() {
            @Override
            public Model model() {
                return ModelImpl.this;
            }
        });
        establish(create);
        this.create = create;
        factory = new RecordsFactory() {

            @Override
            public DSLContext create() {
                return create;
            }

            @Override
            public UUID currentPrincipalId() {
                return getCurrentPrincipal().getPrincipal()
                                            .getId();
            }
        };
        workspaceModel = new WorkspaceModelImpl(this);
        WorkspaceScope workspaceScope = workspaceModel.getScoped(WellKnownProduct.KERNEL_WORKSPACE.id());
        if (workspaceScope == null) {
            LoggerFactory.getLogger(ModelImpl.class)
                         .error("Cannot obtain kernel workspace.  Database is not bootstrapped");
            throw new IllegalStateException("Database has not been boostrapped");
        }
        kernel = workspaceScope.getWorkspace()
                               .getAccessor(Kernel.class);
        phantasmModel = new PhantasmModelImpl(this);
        jobModel = new JobModelImpl(this);
        initializeCurrentPrincipal();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R apply(Class<R> phantasm,
                                                                       Phantasm target) {
        PhantasmDefinition definition = (PhantasmDefinition) cached(phantasm,
                                                                    this);
        return (R) definition.construct(target.getRuleform(), this,
                                        getCurrentPrincipal().getPrincipal());
    }

    @Override
    public PhantasmDefinition cached(Class<? extends Phantasm> phantasm) {
        return cached(phantasm, this);
    }

    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R cast(T source,
                                                                      Class<R> phantasm) {
        return wrap(phantasm, source);
    }

    @Override
    public void close() {
        try {
            create.configuration()
                  .connectionProvider()
                  .acquire()
                  .rollback();
        } catch (DataAccessException | SQLException e) {
            LoggerFactory.getLogger(ModelImpl.class)
                         .error("error rolling back transaction during model close",
                                e);
        }
        create.close();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <T extends ExistentialRuleform, R extends Phantasm> R construct(Class<R> phantasm,
                                                                           ExistentialDomain domain,
                                                                           String name,
                                                                           String description) throws InstantiationException {
        PhantasmDefinition definition = (PhantasmDefinition) cached(phantasm,
                                                                    this);
        ExistentialRecord record = (ExistentialRecord) records().newExistential(domain);
        record.setName(name);
        record.setDescription(description);
        record.insert();
        return (R) definition.construct((ExistentialRuleform) record, this,
                                        getCurrentPrincipal().getPrincipal());
    }

    @Override
    public DSLContext create() {
        return create;
    }

    @Override
    public <V> V executeAs(AuthorizedPrincipal principal,
                           Callable<V> function) throws Exception {
        V value = null;
        AuthorizedPrincipal previous = currentPrincipal;
        currentPrincipal = principal;
        try {
            value = function.call();
        } finally {
            currentPrincipal = previous;
        }
        return value;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#flush()
     */
    @Override
    public void flush() {
        animations.flush();
    }

    @Override
    public void flushWorkspaces() {
        workspaceModel.flush();
        initializeCurrentPrincipal();
    }

    @Override
    public CoreInstance getCoreInstance() {
        return wrap(CoreInstance.class,
                    phantasmModel.getChild(getKernel().getCore(),
                                           factory.resolve(getKernel().getSingletonOf()
                                                                      .getInverse()),
                                           ExistentialDomain.Agency));
    }

    @Override
    public AuthorizedPrincipal getCurrentPrincipal() {
        AuthorizedPrincipal authorizedPrincipal = currentPrincipal;
        return authorizedPrincipal == null ? new AuthorizedPrincipal(kernel.getCoreAnimationSoftware())
                                           : authorizedPrincipal;
    }

    @Override
    public JobModel getJobModel() {
        return jobModel;
    }

    @Override
    public Kernel getKernel() {
        return kernel;
    }

    @Override
    public PhantasmModel getPhantasmModel() {
        return phantasmModel;
    }

    @Override
    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    @Override
    public void inferNetworks() {
        animations.inferNetworks();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#principalFrom(com.chiralbehaviors.CoRE.domain.Agency, java.util.List)
     */
    @Override
    public AuthorizedPrincipal principalFrom(Agency principal,
                                             List<UUID> capabilities) {
        return new AuthorizedPrincipal(principal, capabilities,
                                       capabilities.stream()
                                                   .map(uuid -> create().selectFrom(FACET)
                                                                        .where(FACET.ID.eq(uuid))
                                                                        .fetchOne())
                                                   .filter(auth -> auth != null)
                                                   .filter(auth -> phantasmModel.isAccessible(principal.getId(),
                                                                                              auth.getClassifier(),
                                                                                              auth.getClassification()))
                                                   .map(f -> records().resolve(f.getClassification()))
                                                   .map(e -> (Agency) e)
                                                   .collect(Collectors.toList()));
    }

    @Override
    public RecordsFactory records() {
        return factory;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#snapshot()
     */
    @Override
    public WorkspaceSnapshot snapshot() {
        return new StateSnapshot(create, excludeThisSingleton());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R wrap(Class<R> phantasm,
                                                                      ExistentialRuleform ruleform) {
        if (ruleform == null) {
            return null;
        }
        PhantasmDefinition definition = cached(phantasm, this);
        return (R) definition.wrap(ruleform.getRuleform(), this);
    }

    private void establish(DSLContext create) {
        Configuration configuration = create.configuration();
        configuration.set(new DefaultRecordListenerProvider(animations));
        configuration.settings()
                     .setExecuteWithOptimisticLocking(true);
        TransactionProvider inner = configuration.transactionProvider();
        configuration.set(new TransactionProvider() {

            @Override
            public void begin(TransactionContext ctx) throws DataAccessException {
                animations.begin();
                inner.begin(ctx);
            }

            @Override
            public void commit(TransactionContext ctx) throws DataAccessException {
                try {
                    animations.commit();
                    inner.commit(ctx);
                } finally {
                    configuration.set(inner);
                }
            }

            @Override
            public void rollback(TransactionContext ctx) throws DataAccessException {
                configuration.set(inner);
                animations.rollback();
                inner.rollback(ctx);
            }
        });
    }

    private Collection<UUID> excludeThisSingleton() {
        List<UUID> excluded = new ArrayList<>();
        Agency instance = (Agency) getCoreInstance().getRuleform();
        excluded.add(instance.getId());
        Relationship relationship = kernel.getSingletonOf();
        excluded.add(phantasmModel.getImmediateLink(instance, relationship,
                                                    kernel.getCore())
                                  .getId());
        excluded.add(phantasmModel.getImmediateLink(kernel.getCore(),
                                                    factory.resolve(relationship.getInverse()),
                                                    instance)
                                  .getId());

        relationship = kernel.getInstanceOf();
        excluded.add(phantasmModel.getImmediateLink(instance, relationship,
                                                    kernel.getCore())
                                  .getId());
        excluded.add(phantasmModel.getImmediateLink(kernel.getCore(),
                                                    factory.resolve(relationship.getInverse()),
                                                    instance)
                                  .getId());
        return excluded;
    }

    private void initializeCurrentPrincipal() {
        currentPrincipal = new AuthorizedPrincipal(create.selectFrom(EXISTENTIAL)
                                                         .where(EXISTENTIAL.ID.equal(WellKnownAgency.CORE_ANIMATION_SOFTWARE.id()))
                                                         .fetchOne()
                                                         .into(Agency.class));
    }
}
