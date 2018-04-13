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

package com.sphereon.alfresco.blockchain.proof.actions.executers;

import com.sphereon.alfresco.blockchain.proof.actions.Param;
import com.sphereon.alfresco.blockchain.proof.actions.RegisterFileActionExecuter;
import com.sphereon.alfresco.blockchain.proof.actions.VerifyFileActionExecuter;
import org.alfresco.model.ContentModel;
import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.transaction.annotation.Transactional;


@RunWith(value = AlfrescoTestRunner.class)
@Ignore
public class RegisterFileActionExecuterTestIT extends AbstractAlfrescoIT {
    private static final String ACCESS_TOKEN = "0dbd17f1-c108-350e-807e-42d13e543b32";
    private static final String MULTICHAIN_CONTEXT = "multichain";
    private static final long NOW = System.currentTimeMillis();
    private static final String FILENAME = "IT-file-" + NOW + ".txt";
    private static final String CONTENT = "Content-" + NOW;


    static Logger log = Logger.getLogger(RegisterFileActionExecuterTestIT.class);

    protected <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    protected <T> T getBean(QName name) {
        return getBean(name.getLocalName());
    }


    @Test
    public void testActionRegistration() {
        ActionService actionService = getBean(ServiceRegistry.ACTION_SERVICE);
        Action action = actionService.createAction(RegisterFileActionExecuter.NAME);
        Assert.assertNotNull(action);
        Assert.assertNotNull(action.getId());
        Assert.assertEquals(RegisterFileActionExecuter.NAME, action.getActionDefinitionName());
    }

    @Test
    @Transactional
    public void testExecuteAction() {
        NodeLocatorService nodeLocatorService = getBean(ServiceRegistry.NODE_LOCATOR_SERVICE);
        NodeRef companyHome = nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null);
        NodeService nodeService = getBean(ServiceRegistry.NODE_SERVICE);
        ContentService contentService = getBean(ServiceRegistry.CONTENT_SERVICE);
        ActionService actionService = getBean(ServiceRegistry.ACTION_SERVICE);
        FileFolderService fileFolderService = getBean(ServiceRegistry.FILE_FOLDER_SERVICE);

        NodeRef fileNode = null;

        try {

            FileInfo fileInfo = fileFolderService.create(companyHome, FILENAME, ContentModel.PROP_CONTENT);

            fileNode = fileInfo.getNodeRef();
            ContentWriter contentWriter = contentService.getWriter(fileNode, ContentModel.PROP_CONTENT, true);
            contentWriter.setEncoding("UTF-8");
            contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            contentWriter.putContent(CONTENT);


            // Validate pre registration
            Action verifyPreAction = actionService.createAction(VerifyFileActionExecuter.NAME);
//            verifyPreAction.setParameterValue(Param.API_ACCESS_TOKEN.getParamName(), ACCESS_TOKEN);
            verifyPreAction.setParameterValue(Param.BLOCKCHAIN_CONTEXT.getParamName(), MULTICHAIN_CONTEXT);
            verifyPreAction.setParameterValue(Param.BLOCKCHAIN_HASH_METHOD.getParamName(), "sha-256");
            actionService.executeAction(verifyPreAction, fileNode);

            Assert.assertEquals(Boolean.FALSE.toString(), verifyPreAction.getParameterValue(Param.BLOCKCHAIN_EXISTED.getParamName()));
            Assert.assertEquals(Boolean.FALSE.toString(), verifyPreAction.getParameterValue(Param.BLOCKCHAIN_EXISTS.getParamName()));
            Assert.assertNotNull(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName()));
            Assert.assertNotNull(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_HASH_VALUE.getParamName()));




            // Register hash
            Action registerAction = actionService.createAction(RegisterFileActionExecuter.NAME);
//            registerAction.setParameterValue(Param.API_ACCESS_TOKEN.getParamName(), ACCESS_TOKEN);
            registerAction.setParameterValue(Param.BLOCKCHAIN_CONTEXT.getParamName(), MULTICHAIN_CONTEXT);
            registerAction.setParameterValue(Param.BLOCKCHAIN_HASH_METHOD.getParamName(), "SHA-256");
            actionService.executeAction(registerAction, fileNode);

            Assert.assertEquals(Boolean.FALSE.toString(), registerAction.getParameterValue(Param.BLOCKCHAIN_EXISTED.getParamName()));
            Assert.assertEquals(Boolean.TRUE.toString(), registerAction.getParameterValue(Param.BLOCKCHAIN_EXISTS.getParamName()));
            Assert.assertEquals(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName()), registerAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName()));
            Assert.assertEquals(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_HASH_VALUE.getParamName()), registerAction.getParameterValue(Param.BLOCKCHAIN_HASH_VALUE.getParamName()));


            // Validate again
            Action verifyPostAction = actionService.createAction(VerifyFileActionExecuter.NAME);
//            verifyPostAction.setParameterValue(Param.API_ACCESS_TOKEN.getParamName(), ACCESS_TOKEN);
            verifyPostAction.setParameterValue(Param.BLOCKCHAIN_CONTEXT.getParamName(), MULTICHAIN_CONTEXT);
            verifyPostAction.setParameterValue(Param.BLOCKCHAIN_HASH_METHOD.getParamName(), "sha_256"); // deliberate change from above hash method
            actionService.executeAction(verifyPostAction, fileNode);

            Assert.assertEquals(Boolean.TRUE.toString(), verifyPostAction.getParameterValue(Param.BLOCKCHAIN_EXISTED.getParamName()));
            Assert.assertEquals(Boolean.TRUE.toString(), verifyPostAction.getParameterValue(Param.BLOCKCHAIN_EXISTS.getParamName()));
            Assert.assertEquals(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName()), verifyPostAction.getParameterValue(Param.BLOCKCHAIN_CHAIN_ID.getParamName()));
            Assert.assertEquals(verifyPreAction.getParameterValue(Param.BLOCKCHAIN_HASH_VALUE.getParamName()), verifyPostAction.getParameterValue(Param.BLOCKCHAIN_HASH_VALUE.getParamName()));


        } finally {
            if (fileNode != null) {
                nodeService.deleteNode(fileNode);
            }
        }
    }

}