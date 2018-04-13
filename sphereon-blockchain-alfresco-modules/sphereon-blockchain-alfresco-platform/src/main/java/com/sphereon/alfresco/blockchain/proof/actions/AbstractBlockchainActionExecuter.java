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
import com.sphereon.alfresco.base.actions.AbstractActionExecuter;
import com.sphereon.libs.blockchain.commons.Digest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractBlockchainActionExecuter extends AbstractActionExecuter {

    protected Digest digest = Digest.getInstance();

    @Autowired
    protected BlockchainActionDelegate delegate;

    protected static Log log = LogFactory.getLog(AbstractBlockchainActionExecuter.class);

    protected void updateMissingParams(Action action) {
        if (isEmpty(action, Param.BLOCKCHAIN_CONTEXT)) {
            action.setParameterValue(Param.BLOCKCHAIN_CONTEXT.getParamName(), delegate.getConfig().getConfigName());
        }
        if (isEmpty(action, Param.BLOCKCHAIN_HASH_METHOD)) {
            action.setParameterValue(Param.BLOCKCHAIN_HASH_METHOD.getParamName(), Digest.Algorithm.SHA_256.getImplementation());
        }
    }


    protected byte[] getHashValueFromNode(Action ruleAction, NodeRef actionedUponNodeRef) {
        if (!nodeExists(actionedUponNodeRef)) {
            return null;
        }
        ContentReader contentReader = getContentReaderForExistingNode(actionedUponNodeRef);
        if (contentReader == null) {
            return null;
        }
        Digest.Algorithm algorithm = Digest.Algorithm.from(Param.BLOCKCHAIN_HASH_METHOD.getValueAsString(ruleAction));
        try (InputStream is = contentReader.getContentInputStream()) {
            return digest.getHash(algorithm, is);
        } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
    }

    protected ContentReader getContentReaderForExistingNode(NodeRef actionedUponNodeRef) {
        ContentReader contentReader = contentService.getReader(actionedUponNodeRef, org.alfresco.model.ContentModel.PROP_CONTENT);
        if (contentReader == null || !contentReader.exists()) {
            log.warn(String.format("Content for url '%s' doesn't exist anymore", contentReader == null ? "<unknown>" : contentReader.getContentData().getContentUrl()));
            contentReader = null;
        }
        return contentReader;
    }

}
