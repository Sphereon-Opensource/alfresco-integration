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
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

public class RegisterFreeformActionExecuter extends AbstractBlockchainActionExecuter {
    public static final String NAME = "blockchainRegisterFreeform";


    private Digest digest = Digest.getInstance();


    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        paramList.add(Param.BLOCKCHAIN_CHAIN_ID.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_CHAIN_ID.getParamName()), false));
        /*paramList.add(Param.BLOCKCHAIN_ENTRY_BODY.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_ENTRY_BODY.getParamName()), false));
        paramList.add(Param.BLOCKCHAIN_ENTRY_METADATA.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_ENTRY_METADATA.getParamName()), false));
        paramList.add(Param.BLOCKCHAIN_INCLUDE_CONTENT_HASH.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_INCLUDE_CONTENT_HASH.getParamName()), false));*/
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

      /*  String accessToken = getAccessTokenAndClearAfterwards(ruleAction);
        String context = Param.BLOCKCHAIN_CONTEXT.getValue(ruleAction);
        String content = Param.BLOCKCHAIN_ENTRY_BODY.getValueAsString(ruleAction);
        Digest.Algorithm algorithm = Digest.Algorithm.from(Param.BLOCKCHAIN_HASH_METHOD.getValueAsString(ruleAction));

        BlockchainActionDelegate blockchainAction = actionFactory.getObject().setAccessToken(accessToken);

        String hashValue = digest.getHashAsString(algorithm, contentReader.getContentString(), Digest.Encoding.HEX);
        IdResponse idResponse = blockchainAction.verifyChain(context, hashValue, FILE_LINK);

        boolean existed = idResponse.getExists() == IdResponse.ExistsEnum.TRUE;
        String msg;
        if (existed) {
            msg = String.format("Chain for file link was already registered for id %s in context %s with hash %s", idResponse.getId(), context, hashValue);
            System.err.println(msg);
            log.info(msg);
        }
        idResponse = blockchainAction.registerChain(context, hashValue, FILE_LINK, content);
        msg = String.format("Chain for file link registered for id %s in context %s with hash %s. Existed already? %s", idResponse.getId(), context, hashValue, existed);
        log.info(msg);


        Param.BLOCKCHAIN_EXISTED.updateActionBooleanValue(ruleAction, existed);
        Param.BLOCKCHAIN_EXISTS.updateActionBooleanValue(ruleAction, idResponse.getExists() == IdResponse.ExistsEnum.TRUE);
        Param.BLOCKCHAIN_CHAIN_ID.updateActionStringValue(ruleAction, idResponse.getId());
        Param.BLOCKCHAIN_HASH_VALUE.updateActionStringValue(ruleAction, hashValue);


        AnchoredEntryResponse anchoredEntryResponse = blockchainAction.getFirstEntry(context, idResponse);
        if (anchoredEntryResponse != null && anchoredEntryResponse.getFirstAnchorTime() != null) {
            ruleAction.setParameterValue(Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getParamName(), anchoredEntryResponse.getFirstAnchorTime().format(DateTimeFormatter.ISO_DATE_TIME));
            nodeService.setProperty(actionedUponNodeRef, QName.createQName(Param.NS, Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getParamName()), Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getValueAsString(ruleAction));
        }

        Param.BLOCKCHAIN_HASH_METHOD.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_HASH_VALUE.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_CHAIN_ID.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
        Param.BLOCKCHAIN_CONTEXT.updateNodePropertyFromActionParam(nodeService, actionedUponNodeRef, ruleAction);
*/
    }


    @Override
    public String getName() {
        return NAME;
    }


}
