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

package com.sphereon.alfresco.base.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.repo.workflow.activiti.BaseJavaDelegate;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class SphereonWorkflowAction extends BaseJavaDelegate implements ExecutionListener, TaskListener {
    private static Log log = LogFactory.getLog(SphereonWorkflowAction.class);

    private static final String BPM_PACKAGE = "bpm_package";
    private String actionName;

    public SphereonWorkflowAction() {
    }

    public void execute(DelegateExecution execution) {
        log.info(String.format("START of workflow action %s with execution id %s and activity id %s", actionName, execution.getId(), execution.getCurrentActivityId()));
        Action action = getServiceRegistry().getActionService().createAction(getActionName());
        List<FileInfo> fileInfos = getFileInfos(execution);

        for (FileInfo fileInfo : fileInfos) {
            log.info(String.format("Executing action %s (%s) for file info %s with activity id %s", actionName, action.getDescription(), fileInfo.getName(), execution.getCurrentActivityId()));
            getServiceRegistry().getActionService().executeAction(action, fileInfo.getNodeRef());
        }
        log.info(String.format("END of workflow action %s with execution id %s and activity id %s", actionName, execution.getId(), execution.getCurrentActivityId()));
    }

    private List<FileInfo> getFileInfos(DelegateExecution execution) {
        ActivitiScriptNode bpmPackage = (ActivitiScriptNode)execution.getVariables().get(BPM_PACKAGE);
        List<ChildAssociationRef> children = getServiceRegistry().getNodeService().getChildAssocs(bpmPackage.getNodeRef());
        List<FileInfo> fileInfos = new ArrayList();

        for (ChildAssociationRef child : children) {
            if (ContentModel.PROP_CONTENT.getNamespaceURI().equals(child.getQName().getNamespaceURI()) && child.getChildRef() != null) {
                FileInfo fileInfo = getServiceRegistry().getFileFolderService().getFileInfo(child.getChildRef());
                if (!fileInfo.isFolder() && !fileInfo.isLink()) {
                    fileInfos.add(fileInfo);
                }
            }
        }
        return fileInfos;
    }

    public void notify(DelegateExecution execution) {
        this.execute(execution);
    }

    public void notify(DelegateTask delegateTask) {
        this.notify(delegateTask.getExecution());
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
