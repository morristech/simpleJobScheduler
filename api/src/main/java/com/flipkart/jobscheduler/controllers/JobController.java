/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.flipkart.jobscheduler.controllers;


import com.flipkart.jobscheduler.models.Job;
import com.flipkart.jobscheduler.models.OneTimeJob;
import com.flipkart.jobscheduler.models.ScheduledJob;
import com.flipkart.jobscheduler.repositories.Repository;
import com.flipkart.jobscheduler.services.MasterJobScheduler;
import com.flipkart.jobscheduler.util.JobHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/jobs")
public class JobController {
    public static final int MAX_NOT_FOUND_ERRORS_BEFORE_SELF_DELETE = 3;
    private final Repository repository;
    private final MasterJobScheduler masterJobScheduler;

    @Autowired
    public JobController(Repository repository, MasterJobScheduler masterJobScheduler) {
        this.repository = repository;
        this.masterJobScheduler = masterJobScheduler;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/scheduled")
    @ResponseBody
    @Transactional
    public Job createJob(@RequestBody ScheduledJob job) {
        Job newJob = repository.findByName(job.getName());

        if(newJob == null) {
            newJob = repository.save(job);
            masterJobScheduler.addJob(job);
        }

        return newJob;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/oneTime")
    @ResponseBody
    @Transactional
    public Job createJob(@RequestBody OneTimeJob oneTimeJob) {
        Job newJob = repository.findByName(oneTimeJob.getName());
        if(newJob == null) {
            newJob = repository.save(oneTimeJob);
            masterJobScheduler.addJob(oneTimeJob);
        }
        return newJob;
    }


    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    @ResponseBody
    public Job getJob(@PathVariable Long jobId) {
        return fetchJob(jobId);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Job getJob(@RequestParam(value = "name") String jobName) {
        return fetchJobByName(jobName);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void deleteJob(@RequestParam(value = "name") String jobName) {
        Job job = repository.findByName(jobName);

        if(job != null) {
            masterJobScheduler.removeJob(job);
            repository.delete(job);
        }
    }

    private Job fetchJobByName(String jobName) {
        Job job = repository.findByName(jobName);

        if(job == null) {
            throw new NotFoundException("Job", jobName);
        }

        return job;
    }

    private Job fetchJob(Long jobId) {
        Job job = repository.findOne(jobId);

        if(job == null) {
            throw new NotFoundException("Job", jobId.toString());
        }
        return job;
    }

    @RequestMapping(value = "/{jobName}/unsideline", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void unSideline(@PathVariable("jobName") String name) {
        final Job job = fetchJobByName(name);
        if (JobHelper.isScheduled(job) && job.isSidelined()) {
            ScheduledJob scheduledJob = (ScheduledJob) job;
            scheduledJob.unSideline();
            masterJobScheduler.addJob(job); //TODO: This is dicey, what if the job is there in the jobInstances queue due to a future bug.
            repository.save(job);
        }
    }
}
