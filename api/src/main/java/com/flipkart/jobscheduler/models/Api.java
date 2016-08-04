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

package com.flipkart.jobscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.jobscheduler.services.JobResponseHandler;
import com.ning.http.client.AsyncHttpClient;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

/**
 * Represents an action to be invoked by the Job Executor
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name="api")
public abstract class Api {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    protected Long id;

    @NotBlank
    @JsonProperty
    protected String url;


    public abstract void executeAsync(AsyncHttpClient asyncHttpClient, JobResponseHandler jobResponseHandler);

}