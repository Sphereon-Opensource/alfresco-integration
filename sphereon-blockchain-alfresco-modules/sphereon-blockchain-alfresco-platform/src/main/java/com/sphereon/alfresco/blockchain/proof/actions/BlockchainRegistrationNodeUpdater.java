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
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BlockchainRegistrationNodeUpdater {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    private static final int MAX_COUNT = 75; // Allow 10-11 minutes Factom as wel
    private static final int INTERVAL_SEC = 10;

    public void afterAnchoring(BlockchainActionDelegate delegate, NodeService nodeService, Action ruleAction, NodeRef actionedUponNodeRef, byte[] hash, String hexHash) {
        AfterAnchorExecutor executor = new AfterAnchorExecutor(delegate, nodeService, ruleAction, actionedUponNodeRef, hash, hexHash);
        EXECUTOR_SERVICE.submit(executor);
    }


    // We cannot use @Async support because is messes up with Alfresco professional (rather the <task:annotation-driven/> in service-context.xml does)
    private class AfterAnchorExecutor implements Runnable {
        private AtomicInteger count = new AtomicInteger();
        private final BlockchainActionDelegate delegate;
        private final NodeService nodeService;
        private final Action ruleAction;
        private final NodeRef actionedUponNodeRef;
        private final byte[] hash;
        private final String hexHash;


        public AfterAnchorExecutor(BlockchainActionDelegate delegate, NodeService nodeService, Action ruleAction, NodeRef actionedUponNodeRef, byte[] hash, String hexHash) {
            this.delegate = delegate;
            this.nodeService = nodeService;
            this.ruleAction = ruleAction;
            this.actionedUponNodeRef = actionedUponNodeRef;
            this.hash = hash;
            this.hexHash = hexHash;
        }

        @Override
        public void run() {
            int run = count.incrementAndGet();
            String id = "verify-after-register-" + actionedUponNodeRef.getId();
            RegisterFileActionExecuter.log.debug(String.format("Verify after register count %d for id %s for config %s, hash %s", run, id, delegate.getConfig().getConfigName(), hexHash));
            VerifyContentResponse verifyContentResponse = delegate.verifyContent(hash, Optional.of(id));

            if (verifyContentResponse != null && verifyContentResponse.getRegistrationState() == VerifyContentResponse.RegistrationStateEnum.REGISTERED) {
                String regTime = verifyContentResponse.getRegistrationTime().format(DateTimeFormatter.ISO_DATE_TIME);
                Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateActionStringValue(ruleAction, regTime);
                Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
                RegisterFileActionExecuter.log.info(String.format("File for id %s was registered at try %d for config %s at %s, hash %s", id, run, delegate.getConfig().getConfigName(), regTime, hexHash));
                return;
            } else if (run >= MAX_COUNT) {
                RegisterFileActionExecuter.log.warn(String.format("File for id %s could not be verified after %d tries for config %s, state %s,  hash %s", id, run, delegate.getConfig().getConfigName(), verifyContentResponse == null ? "unknown" : verifyContentResponse.getRegistrationState(), hexHash));
                return;
            }

            RegisterFileActionExecuter.log.debug(String.format("File for id %s was not registered at try %d, state %s, hash %s. Will retry validation in %d sec", id, run, delegate.getConfig().getConfigName(), verifyContentResponse == null ? "unknown" : verifyContentResponse.getRegistrationState(), hexHash, INTERVAL_SEC));
            try {
                Thread.sleep(INTERVAL_SEC * 1000);
                EXECUTOR_SERVICE.submit(this);
            } catch (InterruptedException e) {
                RegisterFileActionExecuter.log.warn(e.getMessage(), e);
            }
        }
    }


}