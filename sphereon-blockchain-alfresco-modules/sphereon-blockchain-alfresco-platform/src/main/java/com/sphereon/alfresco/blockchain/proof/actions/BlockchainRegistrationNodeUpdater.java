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

import com.sphereon.alfresco.blockchain.proof.BlockchainActionDelegate;
import com.sphereon.sdk.blockchain.proof.model.VerifyContentResponse;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@Scope("prototype")
public class BlockchainRegistrationNodeUpdater {
    @Async
    public void afterAnchoring(BlockchainActionDelegate delegate, NodeService nodeService, Action ruleAction, NodeRef actionedUponNodeRef, byte[] hash, String hexHash) {
        String id = "verify-after-register-" + actionedUponNodeRef.getId();
        VerifyContentResponse verifyContentResponse = delegate.verifyContent(hash, Optional.of(id));

        int loop = 0;
        while (++loop < 30 && verifyContentResponse != null && verifyContentResponse.getRegistrationState() != VerifyContentResponse.RegistrationStateEnum.REGISTERED) {
            RegisterFileActionExecuter.log.debug(String.format("Verify after register step %d for id %s for config %s, hash %s", loop, id, delegate.getConfig().getConfigName(), hexHash));
            verifyContentResponse = delegate.verifyContent(hash, Optional.of(id));
            try {
                Thread.sleep((loop + 1) * 1000);
            } catch (InterruptedException e) {
                RegisterFileActionExecuter.log.warn(e.getMessage(), e);
                break;
            }

        }
        if (verifyContentResponse != null && verifyContentResponse.getRegistrationState() == VerifyContentResponse.RegistrationStateEnum.REGISTERED) {
            Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateActionStringValue(ruleAction, verifyContentResponse.getRegistrationTime().format(DateTimeFormatter.ISO_DATE_TIME));
            Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
            RegisterFileActionExecuter.log.info(String.format("File for id %s was registered for config %s at %s, hash %s", id, delegate.getConfig().getConfigName(), verifyContentResponse.getRegistrationTime().format(DateTimeFormatter.ISO_DATE_TIME), hexHash));
        } else {
            RegisterFileActionExecuter.log.warn(String.format("File for id %s could not be verified for config %s, state %s,  hash %s", id, delegate.getConfig().getConfigName(), verifyContentResponse == null ? "unknown" : verifyContentResponse.getRegistrationState(), hexHash));
        }
    }
}