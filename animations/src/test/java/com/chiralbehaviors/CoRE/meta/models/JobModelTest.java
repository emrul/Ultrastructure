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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class JobModelTest extends AbstractModelTest {

    @BeforeClass
    public static void before() throws Exception {
        EntityTransaction txn = em.getTransaction();
        scenario = new OrderProcessingLoader(model);
        txn.begin();
        scenario.load();
        txn.commit();
        jobModel = model.getJobModel();
        // model.setLogConfiguration(Utils.getDocument(JobModelTest.class.getResourceAsStream("/test-log-db.xml")));
    }

    private static JobModel              jobModel;
    private static OrderProcessingLoader scenario;

    @Override
    @After
    public void after() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.clear();
    }

    @Test
    public void testDeliverWithoutMetaProtocol() {
        em.getTransaction().begin();

        StatusCode startState = new StatusCode("begin", kernel.getCore());
        // startState.setPropagateChildren(true);
        em.persist(startState);

        StatusCode delivered = new StatusCode("delivered", kernel.getCore());
        em.persist(delivered);

        StatusCode shipState = new StatusCode("shipping", kernel.getCore());
        em.persist(shipState);

        Product kiki = new Product("Kiki's Delivery Service", kernel.getCore());
        em.persist(kiki);

        Product shipping = new Product("Kiki's Shipping Service",
                                       kernel.getCore());
        em.persist(shipping);

        Product bento = new Product("Tonkatsu Bento Box", kernel.getCore());
        em.persist(bento);

        StatusCodeSequencing sequence = new StatusCodeSequencing(
                                                                 kiki,
                                                                 startState,
                                                                 delivered,
                                                                 kernel.getCore());
        em.persist(sequence);

        StatusCodeSequencing childSequence = new StatusCodeSequencing(
                                                                      shipping,
                                                                      shipState,
                                                                      delivered,
                                                                      kernel.getCore());
        em.persist(childSequence);

        Protocol p = jobModel.newInitializedProtocol(kiki, kernel.getCore());
        p.setProduct(bento);
        p.setRequester(scenario.core);
        p.setDeliverTo(kernel.getAnyLocation());
        p.setDeliverFrom(kernel.getAnyLocation());
        p.setAssignTo(scenario.core);
        p.setChildService(shipping);
        p.setChildProduct(bento);
        em.persist(p);

        em.getTransaction().commit();

        em.getTransaction().begin();

        Job job = model.getJobModel().newInitializedJob(kiki, scenario.core);
        job.setAssignTo(scenario.core);
        job.setProduct(bento);
        job.setDeliverTo(scenario.anyLocation);
        job.setDeliverFrom(scenario.anyLocation);
        job.setRequester(scenario.core);
        jobModel.changeStatus(job, scenario.unset, kernel.getAgency(),
                              "transition during test");
        em.persist(job);

        em.getTransaction().commit();

        em.getTransaction().begin();
        jobModel.changeStatus(job, startState, kernel.getAgency(),
                              "transition during test");
        em.getTransaction().commit();

        TypedQuery<Job> query = em.createQuery("select j from Job j where j.service = :service",
                                               Job.class);
        query.setParameter("service", shipping);
        Job j = query.getSingleResult();
        assertNotNull(j);
    }

    @Test
    public void testEuOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.rc31);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.cafleurBon);
        em.persist(order);
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.available, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testGenerateJobs() throws Exception {
        clearJobs();
        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(scenario.deliver,
                                                        scenario.core);
        job.setAssignTo(scenario.orderFullfillment);
        job.setProduct(scenario.abc486);
        job.setDeliverTo(scenario.rsb225);
        job.setDeliverFrom(scenario.factory1);
        job.setRequester(scenario.georgeTownUniversity);
        jobModel.changeStatus(job, scenario.available, kernel.getCore(),
                              "Test transition");

        List<Job> jobs = jobModel.generateImplicitJobs(job, kernel.getCore());
        TestDebuggingUtil.printJobs(jobs);
        em.getTransaction().rollback();
    }

    @Test
    public void testGenerateJobsFromProtocols() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Product service = new Product("test service", kernel.getCore());
        em.persist(service);
        MetaProtocol mp = jobModel.newInitializedMetaProtocol(service,
                                                              kernel.getCore());
        mp.setAssignTo(kernel.getDevelopedBy());
        mp.setDeliverTo(kernel.getGreaterThanOrEqual());
        em.persist(mp);
        Protocol p = jobModel.newInitializedProtocol(service, kernel.getCore());
        p.setAssignTo(kernel.getPropagationSoftware());
        em.persist(p);
        txn.commit();
        Job order = jobModel.newInitializedJob(service, kernel.getCore());
        order.setAssignTo(kernel.getCoreUser());
        Location loc = new Location("crap location", null, kernel.getCore());
        em.persist(loc);
        order.setDeliverTo(loc);
        em.persist(order);
        TestDebuggingUtil.printProtocolGaps(jobModel.findProtocolGaps(order));
        TestDebuggingUtil.printMetaProtocolGaps(jobModel.findMetaProtocolGaps(order));
        List<Protocol> protocols = model.getJobModel().getProtocolsFor(order.getService());
        assertEquals(1, protocols.size());
        List<Job> jobs = model.getJobModel().generateImplicitJobs(order,
                                                                  kernel.getCore());
        for (Job j : jobs) {
            assertNotNull(j.getAssignToAttribute());
            assertNotNull(j.getAssignTo());
            assertNotNull(j.getService());
            assertNotNull(j.getServiceAttribute());
            assertNotNull(j.getProduct());
            assertNotNull(j.getProductAttribute());
            assertNotNull(j.getDeliverTo());
            assertNotNull(j.getDeliverToAttribute());
            assertNotNull(j.getDeliverFrom());
            assertNotNull(j.getDeliverFromAttribute());
            assertNotNull(j.getRequester());
            assertNotNull(j.getRequesterAttribute());
            assertNotNull(j.getUpdatedBy());

        }

    }

    @Test
    public void testGetActiveJobs() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.rsb225);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.georgeTownUniversity);
        em.persist(order);
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.available, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();

        List<StatusCode> states = Arrays.asList(scenario.active,
                                                scenario.available,
                                                scenario.abandoned);
        List<Job> active = jobModel.getActiveJobsFor(scenario.orderFullfillment,
                                                     states);
        assertEquals(1, active.size());
    }

    @Test
    public void testIteration() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Product service = new Product("test service", kernel.getCore());
        em.persist(service);
        Product childService = new Product("child test service",
                                           kernel.getCore());
        em.persist(childService);
        Product parent = new Product("Parent", kernel.getCore());
        em.persist(parent);
        Product child1 = new Product("Child 1", kernel.getCore());
        em.persist(child1);
        Product child2 = new Product("Child 2", kernel.getCore());
        em.persist(child2);
        Product child3 = new Product("Child 3", kernel.getCore());
        em.persist(child3);
        Relationship childRelationship = model.getRelationshipModel().create("child of",
                                                                             "test relationship",
                                                                             "parentOf",
                                                                             "test relationship inverse");
        parent.link(childRelationship, child1, kernel.getCore(),
                    kernel.getCore(), em);
        parent.link(childRelationship, child2, kernel.getCore(),
                    kernel.getCore(), em);
        parent.link(childRelationship, child3, kernel.getCore(),
                    kernel.getCore(), em);

        List<Tuple<StatusCode, StatusCode>> sequencings = new ArrayList<>();
        sequencings.add(new Tuple<StatusCode, StatusCode>(scenario.available,
                                                          scenario.completed));
        model.getJobModel().createStatusCodeSequencings(service, sequencings,
                                                        kernel.getCore());
        model.getJobModel().createStatusCodeSequencings(childService,
                                                        sequencings,
                                                        kernel.getCore());

        ProductChildSequencingAuthorization auth = new ProductChildSequencingAuthorization(
                                                                                           service,
                                                                                           scenario.available,
                                                                                           childService,
                                                                                           scenario.available,
                                                                                           kernel.getCore());
        em.persist(auth);
        Protocol p = jobModel.newInitializedProtocol(service, kernel.getCore());
        p.setChildrenRelationship(childRelationship);
        p.setChildService(childService);
        em.persist(p);
        Job order = jobModel.newInitializedJob(service, kernel.getCore());
        order.setProduct(parent);
        order.setStatus(kernel.getUnset());
        em.persist(order);
        List<Protocol> protocols = model.getJobModel().getProtocolsFor(order.getService());
        assertEquals(1, protocols.size());
        List<Job> jobs = model.getJobModel().insert(order, protocols.get(0));
        assertEquals(3, jobs.size());
        em.getTransaction().commit();

        em.getTransaction().begin();
        em.refresh(order);
        jobModel.changeStatus(order, scenario.available, kernel.getCore(), null);
        em.getTransaction().commit();

        for (Job j : jobs) {
            em.refresh(j);
            assertEquals(scenario.available, j.getStatus());
        }
    }

    @Test
    public void testJobChronologyOnStatusUpdate() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        alterTriggers(false);
        try {
            Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                              scenario.core);
            order.setAssignTo(scenario.orderFullfillment);
            order.setProduct(scenario.abc486);
            order.setDeliverTo(scenario.rsb225);
            order.setDeliverFrom(scenario.factory1);
            order.setRequester(scenario.georgeTownUniversity);
            jobModel.changeStatus(order, scenario.available, kernel.getCore(),
                                  "Test transition");
            em.persist(order);
            txn.commit();
            em.refresh(order);
            List<JobChronology> chronologies = model.getJobModel().getChronologyForJob(order);
            assertEquals(String.format("Invalid number of chronologies: %s",
                                       chronologies), 2, chronologies.size());
            List<String> fieldErrors = verifyChronologyFields(order,
                                                              chronologies.get(1));

            assertEquals(fieldErrors.toString(), 0, fieldErrors.size());
            txn.begin();
            model.getJobModel().changeStatus(order, scenario.active,
                                             kernel.getCore(), null);
            txn.commit();
            chronologies = model.getJobModel().getChronologyForJob(order);
            assertEquals(3, chronologies.size());
            for (JobChronology c : chronologies) {
                fieldErrors = verifyChronologyFields(order, c);
                if (fieldErrors == null || fieldErrors.size() == 0) {
                    break;
                }
            }
            System.out.println(String.format("Errors: %s", fieldErrors));
            assertEquals(0, fieldErrors.size());
        } finally {
            alterTriggers(true);
        }
    }

    @Test
    public void testJobGenerationAndSequencing() throws Exception {
        em.getTransaction().begin();
        Product pushit = new Product("Pushit Service", null, scenario.core);
        em.persist(pushit);

        Product shoveit = new Product("Shoveit Service", null, scenario.core);
        em.persist(shoveit);

        Product pullIt = new Product("Pullit Service", null, scenario.core);
        em.persist(pullIt);

        StatusCode pushingMe = new StatusCode("Pushing Me", null, scenario.core);
        pushingMe.setPropagateChildren(true);
        em.persist(pushingMe);

        StatusCode shovingMe = new StatusCode("Shoving Me", null, scenario.core);
        em.persist(shovingMe);

        Protocol p = model.getJobModel().newInitializedProtocol(pushit,
                                                                scenario.core);
        p.setProduct(pushit);
        p.setChildService(shoveit);
        em.persist(p);
        model.getJobModel().createStatusCodeChain(pushit,
                                                  new StatusCode[] { pushingMe,
                                                          shovingMe,
                                                          scenario.completed },
                                                  scenario.core);

        ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization(
                                                                                         pushit,
                                                                                         pushingMe,
                                                                                         shovingMe,
                                                                                         scenario.core);
        em.persist(auth);
        em.getTransaction().commit();
        em.getTransaction().begin();

        Job push = model.getJobModel().newInitializedJob(pushit, scenario.core);

        em.getTransaction().commit();

        List<Job> children = model.getJobModel().getAllChildren(push);
        assertEquals(0, children.size());

        em.getTransaction().begin();
        model.getJobModel().changeStatus(push, pushingMe, scenario.core, null);
        push.setProduct(pushit);
        em.getTransaction().commit();
        em.refresh(push);
        children = model.getJobModel().getAllChildren(push);
        assertEquals(1, children.size());
        assertEquals(shovingMe, push.getStatus());
    }

    @Test
    public void testMetaProtocols() throws Exception {
        clearJobs();
        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(scenario.deliver,
                                                        scenario.core);
        job.setAssignTo(scenario.orderFullfillment);
        job.setProduct(scenario.abc486);
        job.setDeliverTo(scenario.rsb225);
        job.setDeliverFrom(scenario.factory1);
        job.setRequester(scenario.georgeTownUniversity);
        jobModel.changeStatus(job, scenario.available, kernel.getCore(),
                              "Test transition");
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> txfm = jobModel.getProtocols(job);
        assertEquals(2, txfm.size());
        List<Protocol> protocols = new ArrayList<>(txfm.keySet());
        assertEquals(scenario.deliver, protocols.get(0).getService());
        assertEquals(scenario.deliver, protocols.get(1).getService());
        assertEquals(scenario.anyAgency, protocols.get(0).getRequester());
        assertEquals(scenario.anyAgency, protocols.get(1).getRequester());
        assertEquals(scenario.anyProduct, protocols.get(0).getProduct());
        assertEquals(scenario.anyProduct, protocols.get(1).getProduct());
        assertEquals(scenario.anyLocation, protocols.get(0).getDeliverFrom());
        assertEquals(scenario.anyLocation, protocols.get(1).getDeliverFrom());
        assertEquals(scenario.anyLocation, protocols.get(0).getDeliverTo());
        assertEquals(scenario.anyLocation, protocols.get(1).getDeliverTo());
        assertEquals(scenario.factory1Agency,
                     protocols.get(0).getChildAssignTo());
        assertEquals(scenario.factory1Agency,
                     protocols.get(1).getChildAssignTo());
        if (protocols.get(0).getChildService().equals(scenario.pick)) {
            assertEquals(scenario.ship, protocols.get(1).getChildService());
        } else {
            assertEquals(scenario.ship, protocols.get(0).getChildService());
            assertEquals(scenario.pick, protocols.get(1).getChildService());
        }

        job = model.getJobModel().newInitializedJob(scenario.printPurchaseOrder,
                                                    scenario.core);
        job.setAssignTo(scenario.orderFullfillment);
        job.setProduct(scenario.abc486);
        job.setDeliverTo(scenario.rsb225);
        job.setDeliverFrom(scenario.factory1);
        job.setRequester(scenario.cafleurBon);
        jobModel.changeStatus(job, scenario.unset, kernel.getCore(),
                              "Transition from test");
        metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        txfm = jobModel.getProtocols(job);
        assertEquals(1, txfm.size());

        List<Job> jobs = jobModel.generateImplicitJobs(job, kernel.getCore());
        assertEquals(1, jobs.size());
        Job derived = jobs.get(0);
        assertEquals(scenario.fee, derived.getService());
        assertEquals(scenario.abc486, derived.getProduct());
        assertEquals(scenario.billingComputer, derived.getAssignTo());
        assertEquals(scenario.cafleurBon, derived.getRequester());
        assertEquals(scenario.rsb225, derived.getDeliverTo());
        assertEquals(scenario.factory1, derived.getDeliverFrom());
        assertEquals(scenario.unset, derived.getStatus());
        assertEquals(job, derived.getParent());
        em.getTransaction().rollback();
    }

    @Test
    public void testNonExemptOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.bht378);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.orgA);
        em.persist(order);
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.available, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.rsb225);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.georgeTownUniversity);
        em.persist(order);
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.available, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(order, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(5, jobs.size());

        txn.begin();
        TypedQuery<Job> query = em.createQuery("select j from Job j where j.service = :service",
                                               Job.class);
        query.setParameter("service", scenario.checkCredit);
        Job creditCheck = query.getSingleResult();
        assertEquals(scenario.available, creditCheck.getStatus());
        jobModel.changeStatus(creditCheck, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(creditCheck, scenario.completed,
                              kernel.getAgency(), "transition during test");
        txn.commit();
        em.clear();
        txn.begin();
        query.setParameter("service", scenario.pick);
        Job pick = query.getSingleResult();
        assertEquals(scenario.available, pick.getStatus());
        jobModel.changeStatus(pick, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(pick, scenario.completed, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.pick);
        pick = query.getSingleResult();
        query.setParameter("service", scenario.ship);
        Job ship = query.getSingleResult();
        List<Job> pickSiblings = jobModel.getActiveSubJobsForService(pick.getParent(),
                                                                     scenario.ship);
        assertEquals(1, pickSiblings.size());
        assertEquals(scenario.waitingOnPurchaseOrder, ship.getStatus());
        query.setParameter("service", scenario.fee);
        Job fee = query.getSingleResult();
        jobModel.changeStatus(fee, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(fee, scenario.completed, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.printPurchaseOrder);
        Job printPO = query.getSingleResult();
        assertEquals(scenario.available, printPO.getStatus());
        jobModel.changeStatus(printPO, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(printPO, scenario.completed, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.ship);
        ship = query.getSingleResult();
        assertEquals(scenario.available, ship.getStatus());
        jobModel.changeStatus(ship, scenario.active, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        jobModel.changeStatus(ship, scenario.completed, kernel.getAgency(),
                              "transition during test");
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.deliver);
        Job deliver = query.getSingleResult();
        assertEquals(scenario.completed, deliver.getStatus());
    }

    public void testSelfSequencing() {
        em.getTransaction().begin();
        Product service = new Product("My Service", null, kernel.getCore());
        em.persist(service);
        StatusCode a = new StatusCode("A", null, kernel.getCore());
        em.persist(a);
        StatusCode b = new StatusCode("B", null, kernel.getCore());
        em.persist(b);
        StatusCode c = new StatusCode("C", null, kernel.getCore());
        em.persist(c);

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<>();
        sequences.add(new Tuple<StatusCode, StatusCode>(a, b));
        sequences.add(new Tuple<StatusCode, StatusCode>(b, c));
        model.getJobModel().createStatusCodeSequencings(service, sequences,
                                                        kernel.getCore());

        ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization();
        auth.setService(service);
        auth.setStatusCode(b);
        auth.setStatusToSet(c);
        auth.setUpdatedBy(kernel.getCore());
        em.persist(auth);

        em.getTransaction().commit();

        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(service,
                                                        kernel.getCore());
        em.persist(job);
        model.getJobModel().changeStatus(job, a, kernel.getCore(), null);
        em.getTransaction().commit();

        em.getTransaction().begin();
        model.getJobModel().changeStatus(job, b, kernel.getCore(), null);
        em.getTransaction().commit();

        em.refresh(job);

        assertEquals(c, job.getStatus());

    }

    @Test
    public void testSelfSequencingAuthorization() {
        em.getTransaction().begin();
        Product service = new Product("Kick ass", null, kernel.getCore());
        em.persist(service);

        StatusCode kickingAss = new StatusCode("Kicking Ass", null,
                                               kernel.getCore());
        em.persist(kickingAss);

        StatusCode takingNames = new StatusCode("Taking Names", null,
                                                kernel.getCore());
        em.persist(takingNames);

        StatusCodeSequencing sequence = new StatusCodeSequencing(
                                                                 service,
                                                                 kickingAss,
                                                                 takingNames,
                                                                 kernel.getCore());
        em.persist(sequence);

        ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization(
                                                                                         service,
                                                                                         kickingAss,
                                                                                         takingNames,
                                                                                         kernel.getCore());
        em.persist(auth);
        em.getTransaction().commit();
        em.getTransaction().begin();

        Job job = model.getJobModel().newInitializedJob(service,
                                                        kernel.getCore());
        em.persist(job);

        em.getTransaction().commit();
        em.getTransaction().begin();
        model.getJobModel().changeStatus(job, kickingAss, kernel.getCore(),
                                         "taking names");

        em.getTransaction().commit();

        em.refresh(job);

        assertEquals(takingNames, job.getStatus());
    }

    @Test
    public void testTerminateChildrenParent() throws IOException {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/logback-test.xml")));
        em.getTransaction().begin();
        Product pushit = new Product("Pushit Service", null, scenario.core);
        em.persist(pushit);

        Product shoveit = new Product("shoveit Service", null, scenario.core);
        em.persist(shoveit);

        Product pullit = new Product("Pullit Service", null, scenario.core);
        em.persist(pullit);

        StatusCode pushingMe = new StatusCode("Pushing Me", null, scenario.core);
        pushingMe.setPropagateChildren(true);
        em.persist(pushingMe);

        StatusCode shovingMe = new StatusCode("Shoving Me", null, scenario.core);
        em.persist(shovingMe);

        Protocol p = model.getJobModel().newInitializedProtocol(pushit,
                                                                scenario.core);
        p.setChildService(shoveit);
        em.persist(p);

        Protocol p2 = model.getJobModel().newInitializedProtocol(shoveit,
                                                                 scenario.core);
        p2.setChildService(pullit);
        em.persist(p2);

        model.getJobModel().createStatusCodeChain(pushit,
                                                  new StatusCode[] { pushingMe,
                                                          shovingMe },
                                                  scenario.core);
        model.getJobModel().createStatusCodeChain(shoveit,
                                                  new StatusCode[] { pushingMe,
                                                          shovingMe },
                                                  scenario.core);
        model.getJobModel().createStatusCodeChain(pullit,
                                                  new StatusCode[] { pushingMe,
                                                          shovingMe },
                                                  scenario.core);

        ProductChildSequencingAuthorization auth = new ProductChildSequencingAuthorization(
                                                                                           shoveit,
                                                                                           shovingMe,
                                                                                           pullit,
                                                                                           shovingMe,
                                                                                           scenario.core);
        em.persist(auth);
        ProductParentSequencingAuthorization auth2 = new ProductParentSequencingAuthorization(
                                                                                              shoveit,
                                                                                              shovingMe,
                                                                                              pushit,
                                                                                              shovingMe,
                                                                                              scenario.core);
        em.persist(auth2);
        em.getTransaction().commit();
        em.getTransaction().begin();

        Job push = model.getJobModel().newInitializedJob(pushit, scenario.core);

        em.getTransaction().commit();
        em.refresh(push);
        List<Job> children = model.getJobModel().getAllChildren(push);
        assertEquals(2, children.size());

        em.getTransaction().begin();
        for (Job j : children) {
            model.getJobModel().changeStatus(j, pushingMe, kernel.getCore(),
                                             null);
        }
        model.getJobModel().changeStatus(push, pushingMe, scenario.core, null);
        em.getTransaction().commit();

        Job shovingJob = model.getJobModel().getActiveSubJobsForService(push,
                                                                        shoveit).get(0);
        em.getTransaction().begin();

        model.getJobModel().changeStatus(shovingJob, shovingMe, null, null);
        em.getTransaction().commit();
        em.refresh(push);
        assertEquals(shovingMe, push.getStatus());
    }

    private void clearJobs() throws SQLException {
        Connection connection = em.unwrap(Connection.class);
        boolean prev = connection.getAutoCommit();
        connection.setAutoCommit(false);
        alterTriggers(false);
        String query = "DELETE FROM ruleform.job";
        connection.createStatement().execute(query);
        alterTriggers(true);
        connection.commit();
        connection.setAutoCommit(prev);
    }

    /**
     * Returns a list of fields that do not match between job and chronology
     *
     * @param job
     * @param jobChronology
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private List<String> verifyChronologyFields(Job job,
                                                JobChronology jobChronology)
                                                                            throws Exception {
        String[] fieldsToMatch = new String[] { "status", "requester",
                "assignTo", "deliverFrom", "deliverTo" };
        List<String> unmatchedFields = new LinkedList<>();
        if (!jobChronology.getJob().equals(job)) {
            unmatchedFields.add("job");
            return unmatchedFields;
        }
        for (String field : fieldsToMatch) {
            ExistentialRuleform<?, ?> jobRf = (ExistentialRuleform<?, ?>) PropertyUtils.getSimpleProperty(job,
                                                                                                          field);
            System.out.println(String.format("Job %s: %s", field, jobRf));
            ExistentialRuleform<?, ?> chronoRf = (ExistentialRuleform<?, ?>) PropertyUtils.getSimpleProperty(jobChronology,
                                                                                                             field);

            System.out.println(String.format("Chronology %s: %s", field,
                                             chronoRf));
            if (chronoRf == null && jobRf == null) {
                continue;
            }
            if (!chronoRf.equals(jobRf)) {
                System.out.println(String.format("%s: job: %s, chronology: %s",
                                                 field,
                                                 jobRf == null ? "null"
                                                              : jobRf.getName(),
                                                 chronoRf == null ? "null"
                                                                 : chronoRf.getName()));
                unmatchedFields.add(field);
            }
        }

        return unmatchedFields;
    }
}
