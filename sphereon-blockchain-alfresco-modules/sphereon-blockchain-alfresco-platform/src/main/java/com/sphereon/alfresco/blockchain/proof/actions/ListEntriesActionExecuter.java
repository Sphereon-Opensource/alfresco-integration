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

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by niels on 29-6-2017.
 */
public class ListEntriesActionExecuter extends AbstractBlockchainActionExecuter {
    public static final String NAME = "blockchain-list-entries";



    private int count;
    private OffsetDateTime lastCheck = OffsetDateTime.now();


    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        paramList.add(Param.BLOCKCHAIN_CONTEXT.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_CONTEXT.getParamName()), false));
        paramList.add(Param.BLOCKCHAIN_CHAIN_ID.toParameterDefinition(getParamDisplayLabel(Param.BLOCKCHAIN_CHAIN_ID.getParamName()), false));

    }

    @Override
    public void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) {
     /*   updateMissingParams(ruleAction);
        String accessToken = getAccessTokenAndClearAfterwards(ruleAction);
        String context = Param.BLOCKCHAIN_CONTEXT.getValue(ruleAction);
        String chainId = (String)ruleAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName());
        if (StringUtils.isEmpty(chainId)) {
            String hashValue = getHashValueFromNode(ruleAction, actionedUponNodeRef, Digest.Encoding.HEX);
            IdResponse idResponse = getChainIdFromHash(ruleAction, accessToken, FILE_LINK, hashValue);
            if (idResponse != null) {
                chainId = idResponse.getId();
            }
        }

        if (StringUtils.isEmpty(chainId)) {
            log.warn("Could not determine chain Id to retrieve entries from.");
            return;
        }
*/

//
//            AnchoredEntryResponse anchoredEntryResponse = blockchainAction.getFirstEntry(context, idResponse);
//            if (anchoredEntryResponse.getFirstAnchorTime() == null) {
//                log.warn(String.format("Chain for file link registered id %s in context %s was not anchored yet. Anchor is pending", idResponse.getId(), context));
//                Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateActionStringValue(ruleAction, "<pending>");
//            } else {
//                ruleAction.setParameterValue(Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getParamName(), anchoredEntryResponse.getFirstAnchorTime().format(DateTimeFormatter.ISO_DATE_TIME));
//                // Update aspect value when current value is outdated
//                String currentValue = Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getValueAsString(ruleAction);
//                if (StringUtils.isEmpty(currentValue) || currentValue.toLowerCase().contains("pending") || currentValue.toLowerCase().contains("never")) {
//                    Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.updateActionStringValue(ruleAction, currentValue);
//                }
//            }
//        msg = String.format("Chain for file link %s id %s in context %s. Registered at %s", existed ? "registered" : "NOT REGISTERED", idResponse.getId(), context, ruleAction.getParameterValue(Param.BLOCKCHAIN_ANCHOR_TIME_FIRST.getParamName()));
//        log.info(msg);


    }




    @Override
    public String getName() {
        return NAME;
    }
}
