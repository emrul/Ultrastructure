/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 *
 */
public class SecondOrderProcessingTest extends AbstractModelTest {

    @BeforeClass
    public static void initialize() throws Exception {
        em.getTransaction().begin();
        w = new OrderProcessingLoader(em);
        w.createAgencys();
        w.createAttributes();
        w.createLocations();
        w.createProducts();
        w.createServices();
        w.createRelationships();
        w.createNetworkInferences();
        w.createStatusCodes();
        w.createAgencyNetworks();
        w.createLocationNetworks();
        w.createProductNetworks();
        w.createStatusCodeSequencing();
        createProtocols();
        createMetaProtocols();
        createSequencingAuthorizations();
        em.getTransaction().commit();

    }

    /**
     *
     */
    private static void createMetaProtocols() {
        //create pick and ship jobs
        MetaProtocol mp1 = model.getJobModel().newInitializedMetaProtocol(w.deliver,
                                                                          w.core);
        mp1.setProductOrdered(w.anyRelationship);
        mp1.setRequestingAgency(w.anyRelationship);
        mp1.setDeliverTo(w.anyRelationship);
        mp1.setDeliverFrom(w.anyRelationship);
        em.persist(mp1);

        //if in US, check credit, if EU, check LOC
        MetaProtocol mp2 = model.getJobModel().newInitializedMetaProtocol(w.pick,
                                                                          w.core);
        mp2.setRequestingAgency(w.customerType);
        mp2.setDeliverFrom(w.area);
        mp2.setDeliverTo(w.area);
        em.persist(mp2);

        MetaProtocol mp3 = model.getJobModel().newInitializedMetaProtocol(w.ship,
                                                                          w.core);
        mp3.setRequestingAgency(w.customerType);
        mp3.setDeliverFrom(w.area);
        mp3.setDeliverTo(w.area);
        em.persist(mp3);

        //generate fee for georgetown
        MetaProtocol mp4 = model.getJobModel().newInitializedMetaProtocol(w.printPurchaseOrder,
                                                                          w.core);
        mp4.setRequestingAgency(w.customerType);
        mp4.setDeliverTo(w.city);
        mp4.setDeliverFrom(w.area);
        mp4.setSequenceNumber(4);
        mp4.setStopOnMatch(true);
        em.persist(mp4);

        //generate fee for everyone else
        MetaProtocol mp5 = model.getJobModel().newInitializedMetaProtocol(w.printPurchaseOrder,
                                                                          w.core);

        mp5.setDeliverTo(w.area);
        mp5.setDeliverFrom(w.anyRelationship);
        mp5.setSequenceNumber(5);
        em.persist(mp5);

        //create sales tax
        MetaProtocol mp6 = model.getJobModel().newInitializedMetaProtocol(w.fee,
                                                                          w.core);
        mp6.setProductOrdered(w.anyRelationship);
        mp6.setRequestingAgency(w.customerType);
        mp6.setDeliverTo(w.region);
        mp6.setDeliverFrom(w.anyRelationship);
        mp6.setSequenceNumber(6);
        em.persist(mp6);
    }

    /**
     *
     */
    private static void createProtocols() {
        //        Product requestedService, Agency requester,
        //        Product requestedProduct, Location deliverTo,
        //        Location deliverFrom, Agency assignTo, Product service,
        //        Product product, Agency updatedBy

        Product pick = w.pick;
        Product anyProduct = w.anyProduct;
        Location us = w.us;
        Agency cpu = w.cpu;
        Product checkCredit = w.checkCredit;
        Agency externalCust = w.externalCust;
        Location euro = w.euro;
        Agency creditDept = w.creditDept;
        Product checkLetterOfCredit = w.checkLetterOfCredit;
        Product ship = w.ship;
        Product abc486 = w.abc486;
        Location anyLocation = w.anyLocation;
        Product printPurchaseOrder = w.printPurchaseOrder;
        Product printCustomsDeclaration = w.printCustomsDeclaration;
        Product deliver = w.deliver;
        Agency anyAgency = w.anyAgency;
        Agency factory1Agency = w.factory1Agency;
        Agency billingComputer = w.billingComputer;
        Product fee = w.fee;
        Agency nonExemptAgency = w.nonExemptAgency;
        Product nonExempt = w.nonExempt;
        Location dc = w.dc;
        Product salesTax = w.salesTax;
        Product discount = w.discount;
        Agency georgeTownUniversity = w.georgeTownUniversity;

        Protocol checkCreditProtocol = newProtocol();
        checkCreditProtocol.setRequestedService(pick);
        checkCreditProtocol.setRequester(externalCust);
        checkCreditProtocol.setRequestedProduct(anyProduct);
        checkCreditProtocol.setDeliverTo(us);
        checkCreditProtocol.setDeliverFrom(us);
        checkCreditProtocol.setAssignTo(cpu);
        checkCreditProtocol.setService(checkCredit);
        checkCreditProtocol.setProduct(anyProduct);
        em.persist(checkCreditProtocol);

        Protocol checkLetterOfCreditProtocol = newProtocol();
        checkLetterOfCreditProtocol.setRequestedService(pick);
        checkLetterOfCreditProtocol.setRequester(externalCust);
        checkLetterOfCreditProtocol.setRequestedProduct(anyProduct);
        checkLetterOfCreditProtocol.setDeliverTo(euro);
        checkLetterOfCreditProtocol.setDeliverFrom(us);
        checkLetterOfCreditProtocol.setAssignTo(creditDept);
        checkLetterOfCreditProtocol.setService(checkLetterOfCredit);
        checkLetterOfCreditProtocol.setProduct(anyProduct);
        em.persist(checkLetterOfCreditProtocol);

        Protocol printPurchaseOrderProtocol = newProtocol();
        printPurchaseOrderProtocol.setRequestedService(ship);
        printPurchaseOrderProtocol.setRequester(externalCust);
        printPurchaseOrderProtocol.setRequestedProduct(abc486);
        printPurchaseOrderProtocol.setDeliverTo(anyLocation);
        printPurchaseOrderProtocol.setDeliverFrom(anyLocation);
        printPurchaseOrderProtocol.setAssignTo(creditDept);
        printPurchaseOrderProtocol.setService(printPurchaseOrder);
        printPurchaseOrderProtocol.setProduct(anyProduct);
        em.persist(printPurchaseOrderProtocol);

        Protocol printCustomsDeclProtocol = newProtocol();
        printCustomsDeclProtocol.setRequestedService(ship);
        printCustomsDeclProtocol.setRequester(externalCust);
        printCustomsDeclProtocol.setRequestedProduct(abc486);
        printCustomsDeclProtocol.setDeliverTo(euro);
        printCustomsDeclProtocol.setDeliverFrom(us);
        printCustomsDeclProtocol.setAssignTo(creditDept);
        printCustomsDeclProtocol.setService(printCustomsDeclaration);
        printCustomsDeclProtocol.setProduct(anyProduct);
        em.persist(printCustomsDeclProtocol);

        Protocol pickProtocol = newProtocol();
        pickProtocol.setRequestedService(deliver);
        pickProtocol.setRequester(anyAgency);
        pickProtocol.setRequestedProduct(anyProduct);
        pickProtocol.setDeliverTo(anyLocation);
        pickProtocol.setDeliverFrom(anyLocation);
        pickProtocol.setAssignTo(factory1Agency);
        pickProtocol.setService(pick);
        pickProtocol.setProduct(anyProduct);
        em.persist(pickProtocol);

        Protocol shipProtocol = newProtocol();
        shipProtocol.setRequestedService(deliver);
        shipProtocol.setRequester(anyAgency);
        shipProtocol.setRequestedProduct(anyProduct);
        shipProtocol.setDeliverTo(anyLocation);
        shipProtocol.setDeliverFrom(anyLocation);
        shipProtocol.setAssignTo(factory1Agency);
        shipProtocol.setService(ship);
        shipProtocol.setProduct(anyProduct);
        em.persist(shipProtocol);

        Protocol feeProtocol = newProtocol();
        feeProtocol.setRequestedService(printPurchaseOrder);
        feeProtocol.setRequester(externalCust);
        feeProtocol.setRequestedProduct(abc486);
        feeProtocol.setDeliverTo(anyLocation);
        feeProtocol.setDeliverFrom(us);
        feeProtocol.setAssignTo(billingComputer);
        feeProtocol.setService(fee);
        feeProtocol.setProduct(anyProduct);
        em.persist(feeProtocol);

        Protocol nonExemptProtocol = newProtocol();
        nonExemptProtocol.setRequestedService(fee);
        nonExemptProtocol.setRequester(nonExemptAgency);
        nonExemptProtocol.setRequestedProduct(nonExempt);
        nonExemptProtocol.setDeliverTo(dc);
        nonExemptProtocol.setDeliverFrom(anyLocation);
        nonExemptProtocol.setAssignTo(billingComputer);
        nonExemptProtocol.setService(salesTax);
        nonExemptProtocol.setProduct(nonExempt);
        em.persist(nonExemptProtocol);

        Protocol discountProtocol = newProtocol();
        discountProtocol.setRequestedService(fee);
        discountProtocol.setRequester(externalCust);
        discountProtocol.setRequestedProduct(abc486);
        discountProtocol.setDeliverTo(euro);
        discountProtocol.setDeliverFrom(us);
        discountProtocol.setAssignTo(billingComputer);
        discountProtocol.setService(discount);
        discountProtocol.setProduct(abc486);
        em.persist(discountProtocol);

        Protocol georgetownFeeProtocol = newProtocol();
        georgetownFeeProtocol.setRequestedService(printPurchaseOrder);
        georgetownFeeProtocol.setRequester(georgeTownUniversity);
        georgetownFeeProtocol.setRequestedProduct(abc486);
        georgetownFeeProtocol.setDeliverTo(dc);
        georgetownFeeProtocol.setDeliverFrom(us);
        georgetownFeeProtocol.setAssignTo(billingComputer);
        georgetownFeeProtocol.setService(fee);
        georgetownFeeProtocol.setProduct(abc486);
        em.persist(georgetownFeeProtocol);

    }

    /**
     *
     */
    private static void createSequencingAuthorizations() {
        ProductSiblingSequencingAuthorization pickToShip = new ProductSiblingSequencingAuthorization(
                                                                                                     w.pick,
                                                                                                     w.completed,
                                                                                                     w.ship,
                                                                                                     w.available,
                                                                                                     w.core);
        em.persist(pickToShip);
    }

    private static Protocol newProtocol() {
        Protocol p = new Protocol(kernel.getCore());
        p.setRequesterAttribute(kernel.getAnyAttribute());
        p.setDeliverToAttribute(kernel.getAnyAttribute());
        p.setDeliverFromAttribute(kernel.getAnyAttribute());
        p.setAssignToAttribute(kernel.getAnyAttribute());
        p.setServiceAttribute(kernel.getAnyAttribute());
        p.setProductAttribute(kernel.getAnyAttribute());
        p.setQuantityUnit(kernel.getAnyUnit());
        return p;
    }

    private static OrderProcessingLoader w;

    @Test
    public void testCreateGeorgetownWorkflow() {
        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(w.deliver, w.core);
        job.setAssignTo(w.orderFullfillment);
        job.setProduct(w.abc486);
        job.setDeliverTo(w.rsb225);
        job.setDeliverFrom(w.factory1);
        job.setRequester(w.georgeTownUniversity);
        em.persist(job);

        em.getTransaction().commit();
        List<Job> jobs = model.getJobModel().getAllChildren(job);
        assertEquals(5, jobs.size());
        boolean hasCorrectService = false;
        for (Job j : jobs) {
            if (j.getService().equals(w.fee)) {
                hasCorrectService = true;
            }
        }
        assertTrue("Did not find fee service in US job tree", hasCorrectService);
    }

    @Test
    public void testFirstSeqAuth() {
        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(w.deliver, w.core);
        job.setAssignTo(w.orderFullfillment);
        job.setProduct(w.abc486);
        job.setDeliverTo(w.rsb225);
        job.setDeliverFrom(w.factory1);
        job.setRequester(w.georgeTownUniversity);
        em.persist(job);

        em.getTransaction().commit();

        em.getTransaction().begin();
        List<Job> childJobsByService = model.getJobModel().getChildJobsByService(job,
                                                                                 w.pick);
        assertEquals(1, childJobsByService.size());
        Job pick = childJobsByService.get(0);

        //delete all child jobs so that we can move pick into a terminal state
        List<Job> childJobs = model.getJobModel().getAllChildren(pick);
        for (Job j : childJobs) {
            em.remove(j);
        }
        em.getTransaction().commit();
        em.getTransaction().begin();
        model.getJobModel().changeStatus(pick, w.available, kernel.getCore(),
                "Test transition");
        em.getTransaction().commit();
        em.getTransaction().begin();
        model.getJobModel().changeStatus(pick, w.active, kernel.getCore(),
                "Test transition");
        em.getTransaction().commit();
        em.getTransaction().begin();
        model.getJobModel().changeStatus(pick, w.completed, kernel.getCore(),
                "Test transition");
        em.getTransaction().commit();

        Job ship = model.getJobModel().getChildJobsByService(job, w.ship).get(0);
        assertEquals(w.waitingOnPurchaseOrder, ship.getStatus());
    }

    @Test
    public void testShipToEU() {
        em.getTransaction().begin();
        Job job = model.getJobModel().newInitializedJob(w.deliver, w.core);
        job.setAssignTo(w.factory1Agency);
        job.setProduct(w.abc486);
        job.setDeliverTo(w.paris);
        job.setDeliverFrom(w.factory1);
        job.setRequester(w.cafleurBon);
        em.persist(job);

        em.getTransaction().commit();
        List<Job> jobs = model.getJobModel().getAllChildren(job);
        assertEquals(6, jobs.size());
        boolean hasCorrectService = false;
        for (Job j : jobs) {
            if (j.getService().equals(w.checkLetterOfCredit)) {
                hasCorrectService = true;
            }
        }
        assertTrue("Did not find checkLetterOfCredit service in EU job tree",
                   hasCorrectService);
    }

}
