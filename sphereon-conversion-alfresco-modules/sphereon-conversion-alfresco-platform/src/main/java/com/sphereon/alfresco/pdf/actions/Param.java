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

package com.sphereon.alfresco.pdf.actions;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;


/**
 * Created by niels on 29-6-2017.
 */
public enum Param {
    TARGET_NODE("sphereon-conversion-target-node", DataTypeDefinition.ASSOC_REF, false),
    CONVERSION_ENGINE("sphereon-conversion-engine", DataTypeDefinition.TEXT, false),
//    OUTPUT_FILE_FORMAT("sphereon-conversion-output-file-format", DataTypeDefinition.TEXT, true),
    PDF_VERSION("sphereon-conversion-pdf-version", DataTypeDefinition.TEXT, false),
    OCR_MODE("sphereon-conversion-ocr-mode", DataTypeDefinition.TEXT, false),
    CONTAINER_CONVERSION("sphereon-conversion-container-conversion", DataTypeDefinition.TEXT, false),
    REGISTER_BLOCKCHAIN("sphereon-conversion-register-blockchain", DataTypeDefinition.BOOLEAN, false);

    private final String paramName;
    private final QName qName;
    private final boolean mandatory;

    Param(String paramName, QName qName, boolean isMandatory) {
        this.paramName = paramName;
        this.qName = qName;
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

    public QName getQName() {
        return qName;
    }

    public String getParamName() {
        return paramName;
    }


    protected ParameterDefinition toParameterDefinition(ConversionActionExecuter executer) {
        return toParameterDefinition(executer.getParamDisplayLabel(getParamName()), isMandatory());
    }

    protected ParameterDefinition toParameterDefinition(ConversionActionExecuter executer, boolean mandatory) {
        return toParameterDefinition(executer.getParamDisplayLabel(getParamName()), mandatory);
    }

    private ParameterDefinition toParameterDefinition(String displayLabel) {
        return toParameterDefinition(displayLabel, isMandatory());
    }

    private ParameterDefinition toParameterDefinition(String displayLabel, boolean mandatory) {
        return new ParameterDefinitionImpl(getParamName(), getQName(), mandatory, displayLabel);
    }
}
