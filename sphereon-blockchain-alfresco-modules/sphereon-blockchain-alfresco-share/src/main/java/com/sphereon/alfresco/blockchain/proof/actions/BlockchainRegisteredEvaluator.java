/*
 * Copyright (c) 2018 Sphereon B.V. <https://sphereon.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sphereon.alfresco.blockchain.proof.actions;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by niels on 10-Jul-17.
 */
public class BlockchainRegisteredEvaluator extends BaseEvaluator {
    private static final String ASPECT_REGISTERED = "blockchain-aspect";

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try {
            JSONArray nodeAspects = getNodeAspects(jsonObject);
            if (nodeAspects == null) {
                return false;
            }
            JSONObject metadata = getMetadata();
            return nodeAspects.contains(ASPECT_REGISTERED);
        } catch (Exception err) {
            throw new RuntimeException("JSONException whilst running action evaluator: " + err.getMessage());
        }
    }
}
