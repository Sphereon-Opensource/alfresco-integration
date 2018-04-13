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

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;


public enum Param {


    BLOCKCHAIN_CONTEXT("blockchain-context", DataTypeDefinition.TEXT, true),
    BLOCKCHAIN_CHAIN_ID("blockchain-chain-id", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_ENTRY_ID("blockchain-entry-id", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_HASH_VALUE("blockchain-hash-value", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_HASH_METHOD("blockchain-hash-method", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_FILE_REMARK("blockchain-file-remark", DataTypeDefinition.TEXT, false),
//    BLOCKCHAIN_ENTRY_BODY("blockchain-entry-body", DataTypeDefinition.ANY, false),
//    BLOCKCHAIN_ENTRY_METADATA("blockchain-entry-metadata", DataTypeDefinition.ANY, false),
//    BLOCKCHAIN_INCLUDE_CONTENT_HASH("blockchain-include-content-hash", DataTypeDefinition.BOOLEAN, false),
    BLOCKCHAIN_ANCHOR_TIME_FIRST("blockchain-anchor-time-first", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_LAST_VERIFY("blockchain-verify-time-last", DataTypeDefinition.TEXT, false),
    BLOCKCHAIN_EXISTED("blockchain-existed", DataTypeDefinition.BOOLEAN, false),
    BLOCKCHAIN_EXISTS("blockchain-exists", DataTypeDefinition.BOOLEAN, false);


    private final String paramName;
    private final QName dataType;
    private final boolean mandatory;

    public static final String NS = "http://www.sphereon.com/model/blockchain/content/1.0";

    Param(String paramName, QName dataType, boolean isMandatory) {
        this.paramName = paramName;
        this.dataType = dataType;
        this.mandatory = isMandatory;
    }

    public <T> T getValue(Action action) {
        return (T) action.getParameterValue(getParamName());
    }

    public String getValueAsString(Action action) {
        return getValue(action);
    }

    @Override
    public String toString() {
        return getParamName();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public QName getDataTypeQName() {
        return dataType;
    }

    public QName getQname() {
        return QName.createQName(Param.NS, getParamName());
    }

    public String getParamName() {
        return paramName;
    }

    public ParameterDefinition toParameterDefinition(String displayLabel) {
        return toParameterDefinition(displayLabel, isMandatory());
    }

    public ParameterDefinition toParameterDefinition(String displayLabel, boolean mandatory) {
        return new ParameterDefinitionImpl(getParamName(), getDataTypeQName(), mandatory, displayLabel);
    }

    public void updateActionBooleanValue(Action action, String value) {
        action.setParameterValue(getParamName(), Boolean.valueOf(value));
    }

    public void updateActionBooleanValue(Action action, Boolean value) {
        action.setParameterValue(getParamName(), value);
    }

    public void updateActionStringValue(Action action, String value) {
        action.setParameterValue(getParamName(), value);
    }

    public <T> T getCurrentNodeProperty(NodeService nodeService, NodeRef nodeRef) {
        return (T) nodeService.getProperty(nodeRef, getQname());
    }
    public void updateNodePropertyFromActionParam(NodeService nodeService, NodeRef nodeRef, Action action) {
        nodeService.setProperty(nodeRef, getQname(), getValue(action));
    }
}
