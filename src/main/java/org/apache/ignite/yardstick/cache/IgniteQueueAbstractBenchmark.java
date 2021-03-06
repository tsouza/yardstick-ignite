/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.yardstick.cache;

import org.apache.ignite.IgniteQueue;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.yardstick.IgniteAbstractBenchmark;
import org.yardstickframework.BenchmarkConfiguration;

/**
 * Abstract class for Ignite benchmarks which use cache.
 */
public abstract class IgniteQueueAbstractBenchmark extends IgniteAbstractBenchmark {
    /** Cache. */
    protected IgniteQueue<Integer> queue;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheName(args.cacheName());

        queue = ignite().queue("queue", 0, colCfg);
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        if (queue != null)
            ignite().destroyCache(queue.name());

        super.tearDown();
    }
}
