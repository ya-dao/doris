// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.memo;

import org.apache.doris.nereids.datasets.tpch.TPCHTestBase;
import org.apache.doris.nereids.datasets.tpch.TPCHUtils;
import org.apache.doris.nereids.properties.PhysicalProperties;
import org.apache.doris.nereids.trees.plans.physical.PhysicalPlan;
import org.apache.doris.nereids.util.PlanChecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class RankTest extends TPCHTestBase {
    @Test
    void testRank() throws NoSuchFieldException, IllegalAccessException {
        for (int i = 1; i < 22; i++) {
            Field field = TPCHUtils.class.getField("Q" + String.valueOf(i));
            System.out.println("Q" + String.valueOf(i));
            Memo memo = PlanChecker.from(connectContext)
                    .analyze(field.get(null).toString())
                    .rewrite()
                    .optimize()
                    .getCascadesContext()
                    .getMemo();
            memo.rank(1);
        }
    }

    @Test
    void testUnrank() throws NoSuchFieldException, IllegalAccessException {
        for (int i = 1; i < 22; i++) {
            Field field = TPCHUtils.class.getField("Q" + String.valueOf(i));
            System.out.println("Q" + String.valueOf(i));
            Memo memo = PlanChecker.from(connectContext)
                    .analyze(field.get(null).toString())
                    .rewrite()
                    .optimize()
                    .getCascadesContext()
                    .getMemo();
            PhysicalPlan plan1 = memo.unrank(memo.rank(1));
            PhysicalPlan plan2 = PlanChecker.from(connectContext)
                    .analyze(field.get(null).toString())
                    .rewrite()
                    .optimize()
                    .getBestPlanTree(PhysicalProperties.GATHER);
            Assertions.assertEquals(plan1.treeString(), plan2.treeString());
        }
    }
}
