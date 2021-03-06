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

package com.flipkart.jobscheduler.utils;

import com.flipkart.jobscheduler.HttpApi;
import com.flipkart.jobscheduler.OneTimeJob;
import com.flipkart.jobscheduler.Schedule;
import com.flipkart.jobscheduler.ScheduledJob;

public class JobApiUtil {
    public static ScheduledJob createTestScheduledJob(String name, String url, long interval) {
        return new ScheduledJob(name, new HttpApi(HttpApi.Method.POST, url, "", null), new Schedule(System.currentTimeMillis(), TestConstants.fiveMinsFromNow(), interval));
    }

    public static OneTimeJob createTestOneTimeJob(String name, String url, long triggerTime) {
        return new OneTimeJob(name, triggerTime, new HttpApi(HttpApi.Method.POST, url, "", null));
    }
}
