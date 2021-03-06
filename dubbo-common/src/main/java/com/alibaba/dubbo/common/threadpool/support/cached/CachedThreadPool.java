/*
 * Copyright 1999-2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.threadpool.support.cached;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.alibaba.dubbo.common.threadpool.support.AbortPolicyWithReport;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;

/**
 * CachedThreadPool
 *
 * @author william.liangf
 * @see java.util.concurrent.Executors#newCachedThreadPool()
 */
@Extension("cached")
public class CachedThreadPool implements ThreadPool {

  public Executor getExecutor(URL url) {
    String threadName = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
    int threads = url.getIntParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
    int queues = url.getIntParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
    int keepalive = url.getIntParameter(Constants.THREAD_ALIVE_KEY, Constants.DEFAULT_THREAD_ALIVE);
    return new ThreadPoolExecutor(0, threads, keepalive, TimeUnit.MILLISECONDS,
        queues <= 0 ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues),
        new NamedThreadFactory(threadName, true), new AbortPolicyWithReport(threadName, url));
  }

}