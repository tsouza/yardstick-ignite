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
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteReducer;
import org.yardstickframework.BenchmarkConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Ignite benchmark that performs transactional put and get operations.
 */
public class IgniteQueuePutTakeBenchmark extends IgniteQueueAbstractBenchmark {

    /** Jobs for run */
    private List<IgniteCallable<Integer>> jobs;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        assert args.jobs() > 0;

        jobs = new ArrayList<>(args.jobs());

        for (int i = 0; i < args.jobs(); ++i)
            jobs.add(new Consumer(queue));

        for (int i = 0; i < args.batchSize(); i++)
            queue.put(nextRandom(0, args.range() / 2));

    }
    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {

        int count = ignite().compute().call(jobs, new IgniteReducer<Integer, Integer>() {
            private int count = 0;
            @Override
            public boolean collect(Integer integer) {
                count += integer;
                return true;
            }

            @Override
            public Integer reduce() {
                return count;
            }
        });

        assert queue.isEmpty() && count == args.batchSize();

        return true;
    }


    public static class Consumer implements IgniteCallable<Integer> {

        private final IgniteQueue<Integer> queue;

        public Consumer(IgniteQueue<Integer> queue) {
            this.queue = queue;
        }


        @Override
        public Integer call() throws Exception {
            int count = 0;
            while (queue.take() != null) count++;
            return count;
        }
    }
}
