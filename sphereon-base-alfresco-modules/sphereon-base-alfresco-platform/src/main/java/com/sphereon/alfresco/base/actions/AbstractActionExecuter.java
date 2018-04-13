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

package com.sphereon.alfresco.base.actions;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

public abstract class AbstractActionExecuter extends ActionExecuterAbstractBase {
    protected ContentService contentService;
    protected NodeService nodeService;
    protected FileFolderService fileFolderService;

    protected static Log log = LogFactory.getLog(AbstractActionExecuter.class);


    protected void updateMissingParams(Action action) {
    }

    protected boolean isEmpty(Action action, Enum param) {
        Serializable value = action.getParameterValue(param.toString());
        if (value == null) {
            return true;
        }
        return StringUtils.isEmpty(value.toString());
    }

    protected boolean nodeExists(NodeRef actionedUponNodeRef) {
        if (!nodeService.exists(actionedUponNodeRef)) {
            log.warn(String.format("node with id '%s' doesn't exist anymore", actionedUponNodeRef.getId()));
            return false;
        }
        return true;
    }

    protected ContentReader getContentReaderForExistingNode(NodeRef actionedUponNodeRef) {
        ContentReader contentReader = fileFolderService.getReader(actionedUponNodeRef);

        if (contentReader == null || !contentReader.exists()) {
            log.warn(String.format("File with url '%s' doesn't exist anymore", contentReader == null ? "<unknown>" : contentReader.getContentData().getContentUrl()));
            contentReader = null;
        }
        return contentReader;
    }


    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public abstract String getName();
}
