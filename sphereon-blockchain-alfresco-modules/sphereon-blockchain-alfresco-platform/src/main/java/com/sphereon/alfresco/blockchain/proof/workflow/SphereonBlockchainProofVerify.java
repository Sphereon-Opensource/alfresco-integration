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

package com.sphereon.alfresco.blockchain.proof.workflow;

import com.sphereon.alfresco.base.workflow.SphereonWorkflowAction;
import com.sphereon.alfresco.blockchain.proof.actions.VerifyFileActionExecuter;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;

public class SphereonBlockchainProofVerify extends SphereonWorkflowAction implements ExecutionListener, TaskListener {

    public SphereonBlockchainProofVerify() {
        setActionName(VerifyFileActionExecuter.NAME);
    }


}
