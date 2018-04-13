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

import com.sphereon.libs.blockchain.commons.Digest;
import com.sphereon.sdk.blockchain.proof.model.CommittedEntry;
import com.sphereon.sdk.blockchain.proof.model.RegisterContentResponse;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class RegisterFileActionExecuter extends AbstractBlockchainActionExecuter {
    public static final String NAME = "blockchain-proof-register-file";
    @Autowired
    private BlockchainRegistrationNodeUpdater nodeUpdater;

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        paramList.add(Param.BLOCKCHAIN_FILE_REMARK.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_FILE_REMARK.getParamName()), false));

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

        String remark = Param.BLOCKCHAIN_FILE_REMARK.getValueAsString(ruleAction);


        String id = "register-" + actionedUponNodeRef.getId();
        byte[] hash = getHashValueFromNode(ruleAction, actionedUponNodeRef);
        String hexHash = digest.getHashAsString(Digest.Algorithm.SHA_256, hash, Digest.Encoding.HEX);
        RegisterContentResponse registerContentResponse = delegate.registerContent(hash, Optional.of(id));


        String msg;

        boolean existed = registerContentResponse.getPerHashProofChain().getRegistrationState() == CommittedEntry.RegistrationStateEnum.REGISTERED;
        boolean exists = existed || registerContentResponse.getPerHashProofChain().getRegistrationState() == CommittedEntry.RegistrationStateEnum.PENDING;
        if (existed) {
            msg = String.format("File for id %s was already registered for config %s, hash %s", id, delegate.getConfig().getConfigName(), hexHash);
        } else {
            msg = String.format("File registered for id %s in config %s with hash %s", id, delegate.getConfig().getConfigName(), hexHash);
        }
        log.info(msg);


        Param.BLOCKCHAIN_EXISTED.updateActionBooleanValue(ruleAction, existed);
        Param.BLOCKCHAIN_EXISTS.updateActionBooleanValue(ruleAction, exists);
        Param.BLOCKCHAIN_CHAIN_ID.updateActionStringValue(ruleAction, registerContentResponse.getPerHashProofChain().getChainId());
        Param.BLOCKCHAIN_HASH_VALUE.updateActionStringValue(ruleAction, hexHash);

        Param.BLOCKCHAIN_HASH_METHOD.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_HASH_VALUE.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_CHAIN_ID.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_CONTEXT.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);

        nodeUpdater.afterAnchoring(delegate, nodeService, ruleAction, actionedUponNodeRef, hash, hexHash);
        log.info(String.format("Started validation in background for id %s, config %s, hash %s", id, delegate.getConfig().getConfigName(), hexHash));
    }


    @Override
    public String getName() {
        return NAME;
    }
}
