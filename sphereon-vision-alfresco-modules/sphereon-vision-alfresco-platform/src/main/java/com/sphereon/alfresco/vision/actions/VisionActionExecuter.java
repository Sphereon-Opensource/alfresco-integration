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

package com.sphereon.alfresco.vision.actions;

import com.sphereon.alfresco.blockchain.proof.actions.RegisterFileActionExecuter;
import com.sphereon.alfresco.base.actions.AbstractActionExecuter;
import com.sphereon.alfresco.vision.VisionConfiguration;
import com.sphereon.alfresco.vision.VisionDelegate;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class VisionActionExecuter extends AbstractActionExecuter {
    public static final String NAME = "vision-action-executer";
    private VisionDelegate delegate;

    public void setRegisterFileAction(RegisterFileActionExecuter registerFileAction) {
        this.registerFileAction = registerFileAction;
    }

    private RegisterFileActionExecuter registerFileAction;

    public void setMimetypeService(MimetypeService mimetypeService) {
        this.mimetypeService = mimetypeService;
    }

    private MimetypeService mimetypeService;

    public void init() {
        // We only supply the action when it is enabled
        if (getConfig().isEnabled()) {
            super.init();
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        paramList.add(Param.CONVERSION_ENGINE.toParameterDefinition(this, false));
//        paramList.add(Param.OUTPUT_FILE_FORMAT.toParameterDefinition(this, false));
        paramList.add(Param.PDF_VERSION.toParameterDefinition(this, false));
        paramList.add(Param.OCR_MODE.toParameterDefinition(this, false));
        paramList.add(Param.CONTAINER_CONVERSION.toParameterDefinition(this, false));
        paramList.add(Param.REGISTER_BLOCKCHAIN.toParameterDefinition(this, false));
    }

    private VisionConfiguration getConfig() {
        return getDelegate().getConfig();
    }


    @Override
    protected void updateMissingParams(Action action) {
        if (isEmpty(action, Param.CONVERSION_ENGINE)) {
            action.setParameterValue(Param.CONVERSION_ENGINE.getParamName(), getConfig().getConversionEngine());
        }
        /*if (isEmpty(action, Param.OUTPUT_FILE_FORMAT)) {
            action.setParameterValue(Param.OUTPUT_FILE_FORMAT.getParamName(), "PDF");
        }*/
        if (isEmpty(action, Param.PDF_VERSION)) {
            action.setParameterValue(Param.PDF_VERSION.getParamName(), getConfig().getPdfVersion());
        }
        if (isEmpty(action, Param.OCR_MODE)) {
            action.setParameterValue(Param.OCR_MODE.getParamName(), getConfig().getOcrMode());
        }
        if (isEmpty(action, Param.CONTAINER_CONVERSION)) {
            action.setParameterValue(Param.CONTAINER_CONVERSION.getParamName(), getConfig().getContainerConversion());
        }
        if (isEmpty(action, Param.REGISTER_BLOCKCHAIN)) {
            action.setParameterValue(Param.REGISTER_BLOCKCHAIN.getParamName(), getConfig().getConversionEngine());
        }
    }

    private void updateConfigFromParams(Action action) {
        updateMissingParams(action);
        getConfig().setConversionEngine(getParamValue(action, Param.CONVERSION_ENGINE));
        /*String outputFileFormat = getParamValue(action, Param.OUTPUT_FILE_FORMAT);
        if (StringUtils.isNotEmpty(outputFileFormat) && ! getConfig().getOutputFileFormats().contains(outputFileFormat)) {
            getConfig().addOutputFileFormat(outputFileFormat);
        }*/
        getConfig().setPdfVersion(getParamValue(action, Param.PDF_VERSION));
        getConfig().setOcrMode(getParamValue(action, Param.OCR_MODE));
        getConfig().setContainerConversion(getParamValue(action, Param.CONTAINER_CONVERSION));
        getConfig().setRegisterBlockchain(getParamValue(action, Param.REGISTER_BLOCKCHAIN));
    }

    private <T> T getParamValue(Action action, Param param) {
        return (T) action.getParameterValue(param.getParamName());
    }

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        try {
            VisionConfiguration config = delegate.getConfig();
            if (!config.getActionEnabled() || !nodeExists(actionedUponNodeRef)) {
                return;
            }
            ContentReader contentReader = getContentReaderForExistingNode(actionedUponNodeRef);
            if (contentReader == null) {
                return;
            }
            updateConfigFromParams(action);
            NodeRef targetNode = actionedUponNodeRef;
            if (!isEmpty(action, Param.TARGET_NODE)) {
                targetNode = (NodeRef) action.getParameterValue(Param.TARGET_NODE.getParamName());
            }
            if (!nodeExists(targetNode)) {
                throw new AlfrescoRuntimeException("Target node does not exist (anymore)");
            }
            ContentWriter contentWriter = fileFolderService.getWriter(targetNode);

            TransformationOptions transformationOptions = new TransformationOptions();
//            transformationOptions.set
            getDelegate().transform(contentReader, contentWriter, new TransformationOptions(actionedUponNodeRef, null, targetNode, null));

            String name = (String)nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
            String baseName = FilenameUtils.getBaseName(name);
            String ext = FilenameUtils.getExtension(name);
            String convertedName;
            if (!StringUtils.isEmpty(name)) {
                convertedName = FilenameUtils.getBaseName(name) + ".pdf"; // +  mimetypeService.getExtension(contentWriter.getMimetype());
            } else {
                convertedName = "converted.pdf";
            }
            nodeService.setProperty(targetNode, ContentModel.PROP_NAME, convertedName);
            ContentData origContentData = (ContentData) nodeService.getProperty(targetNode, ContentModel.PROP_CONTENT);
            ContentData updatedContentData = ContentData.setMimetype(origContentData, "application/pdf");
            nodeService.setProperty(targetNode, ContentModel.PROP_CONTENT, updatedContentData);
nodeService.setProperty(targetNode, ContentModel.ASPECT_TITLED, convertedName);


            if (!Boolean.FALSE.equals(getParamValue(action, Param.REGISTER_BLOCKCHAIN))) {
                registerFileAction.execute(action, targetNode);

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }

    }

    @Override
    protected String getParamDisplayLabel(String param) {
        return super.getParamDisplayLabel(param);
    }


    public VisionDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(VisionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
