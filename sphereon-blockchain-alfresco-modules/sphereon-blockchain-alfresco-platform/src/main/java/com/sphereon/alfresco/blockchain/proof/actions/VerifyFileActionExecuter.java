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

import com.sphereon.sdk.blockchain.proof.model.VerifyContentResponse;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class VerifyFileActionExecuter extends AbstractBlockchainActionExecuter {
    public static final String NAME = "blockchain-proof-verify-file";

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    @Override
    public void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) {
        if (!nodeExists(actionedUponNodeRef)) {
            return;
        }
        ContentReader contentReader = getContentReaderForExistingNode(actionedUponNodeRef);
        if (contentReader == null) {
            return;
        }
        updateMissingParams(ruleAction);

        String id = "verify-" + actionedUponNodeRef.getId();
        byte[] hash = getHashValueFromNode(ruleAction, actionedUponNodeRef);
        VerifyContentResponse verifyContentResponse = delegate.verifyContent(hash, Optional.of(id));


        String msg;
        boolean exists = verifyContentResponse.getRegistrationState() == VerifyContentResponse.RegistrationStateEnum.REGISTERED;
        if (exists) {
            msg = String.format("File for id %s was registered for config %s, hash %s", id, delegate.getConfig().getConfigName(), verifyContentResponse.getHash());
        } else {
            msg = String.format("File was not registered for id %s in config %s with hash %s", id, delegate.getConfig().getConfigName(), verifyContentResponse.getHash());
        }
        log.info(msg);

        Param.BLOCKCHAIN_EXISTED.updateActionBooleanValue(ruleAction, exists);
        Param.BLOCKCHAIN_EXISTS.updateActionBooleanValue(ruleAction, exists);
        Param.BLOCKCHAIN_CHAIN_ID.updateActionStringValue(ruleAction, verifyContentResponse.getPerHashProofChain().getChainId());
        Param.BLOCKCHAIN_HASH_VALUE.updateActionStringValue(ruleAction, verifyContentResponse.getHash());
        if (verifyContentResponse.getRegistrationTime() != null) {
            Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateActionStringValue(ruleAction, verifyContentResponse.getRegistrationTime().format(DateTimeFormatter.ISO_DATE_TIME));
        }


        String prevLastVerify = Param.BLOCKCHAIN_LAST_VERIFY.getCurrentNodeProperty(nodeService, actionedUponNodeRef);
        log.info(String.format("Previous last verify was at: %s", prevLastVerify));
        // We update the last verify node value, but return the previous verify time to the user as this is the time you are interested in for displays/forms/behaviour
        Param.BLOCKCHAIN_LAST_VERIFY.updateActionStringValue(ruleAction, OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        Param.BLOCKCHAIN_LAST_VERIFY.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_LAST_VERIFY.updateActionStringValue(ruleAction, prevLastVerify);
    }




    @Override
    public String getName() {
        return NAME;
    }
}
