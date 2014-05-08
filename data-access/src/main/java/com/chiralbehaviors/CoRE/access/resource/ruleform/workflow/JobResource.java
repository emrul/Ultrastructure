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
package com.chiralbehaviors.CoRE.access.resource.ruleform.workflow;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobAttribute;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.JobModelImpl;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform/Job")
public class JobResource {

    private EntityManager em;
    private JobModel      model;

    public JobResource(Model model) {
        this.model = new JobModelImpl(model);
        em = model.getEntityManager();
    }

    @PUT
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public void changeJobStatus(@PathParam("id") long jobId, StatusCode status,
                                String notes) {
        Job job = em.find(Job.class, jobId);
        if (job == null) {
            throw new InvalidParameterException(
                                                String.format("No job with %d exists",
                                                              jobId));
        }

        em.getTransaction().begin();

        model.changeStatus(job, status, notes);
        em.getTransaction().commit();

    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public AttributedJob getJob(@PathParam("id") long id) {
        Job job = em.find(Job.class, id);

        return new AttributedJob(job, null);
    }

    @GET
    @Path("/{id}/subjobs")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public List<AttributedJob> getActiveSubJobs(@PathParam("id") long jobId,
                                                @QueryParam("agencyId") Long agencyId) {
        List<Job> jobs;
        List<AttributedJob> attributedJobs = new LinkedList<>();
        Job parentJob = em.find(Job.class, jobId);
        if (agencyId == null) {
            jobs = model.getActiveSubJobsOf(parentJob);
        } else {
            Agency agency = em.find(Agency.class, agencyId);
            if (agency == null) {
                throw new InvalidParameterException(
                                                    String.format("Agency with id %d does not exist",
                                                                  agencyId));
            }
            jobs = model.getAllActiveSubJobsOf(parentJob, agency);
        }
        for (Job job : jobs) {
            attributedJobs.add(new AttributedJob(
                                                 job,
                                                 createAttributeMap(model.getAttributesForJob(job))));
        }
        return attributedJobs;
    }

    @GET
    @Path("/{id}/chronology")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public List<JobChronology> getChronology(@PathParam("id") long jobId) {
        Job job = em.find(Job.class, jobId);
        if (job == null) {
            throw new InvalidParameterException(
                                                String.format("No job with %d exists",
                                                              jobId));
        }

        return model.getChronologyForJob(job);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public List<AttributedJob> getJobsForAgency(@QueryParam("agencyId") Long agencyId) {
        List<Job> jobs;
        List<AttributedJob> attributedJobs = new LinkedList<>();
        if (agencyId == null) {
            TypedQuery<Job> query = em.createQuery("select j from Job j",
                                                   Job.class);

            jobs = query.getResultList();
        } else {
            Agency agency = em.find(Agency.class, agencyId);
            if (agency == null) {
                throw new InvalidParameterException(
                                                    String.format("Agency with id %d does not exist",
                                                                  agencyId));
            }
            jobs = model.getActiveJobsFor(agency);
        }
        for (Job job : jobs) {
            attributedJobs.add(new AttributedJob(
                                                 job,
                                                 createAttributeMap(model.getAttributesForJob(job))));
        }
        return attributedJobs;
    }

    // TODO get chronology of all sub jobs
    // get jobs for parent - loop until you get no more jobs. Then get the
    // chronologies for all jobs in that set

    /**
     * @param attributesForJob
     * @return
     */
    private Map<String, JobAttribute> createAttributeMap(List<JobAttribute> attributesForJob) {
        Map<String, JobAttribute> map = new HashMap<>();
        for (JobAttribute attribute : attributesForJob) {
            map.put(attribute.getAttribute().getName(), attribute);
        }
        return map;
    }

    @GET
    @Path("/status/{id}/next")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public List<StatusCode> getNextStatuses(@PathParam("id") long statusId,
                                            @QueryParam("service") long serviceId) {
        StatusCode status = em.find(StatusCode.class, statusId);
        Product service = em.find(Product.class, serviceId);
        return model.getNextStatusCodes(service, status);
    }

    @GET
    @Path("/{id}/terminal-statuses")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public List<StatusCode> getTerminalStates(@PathParam("id") long jobId) {
        Job job = em.find(Job.class, jobId);
        return model.getTerminalStates(job);
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public Job insertJob(AttributedJob job) {
        Job jobToInsert = job.getJob();

        em.getTransaction().begin();
        em.persist(jobToInsert);
        for (Map.Entry<String, JobAttribute> e : job.getAttributes().entrySet()) {
            em.persist(e.getValue());
        }
        em.getTransaction().commit();
        em.refresh(jobToInsert);
        return jobToInsert;

    }

}
