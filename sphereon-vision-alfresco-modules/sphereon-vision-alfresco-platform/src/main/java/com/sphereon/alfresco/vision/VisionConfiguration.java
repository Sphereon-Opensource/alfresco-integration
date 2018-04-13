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

package com.sphereon.alfresco.vision;

import com.sphereon.alfresco.base.auth.AbstractAuthConfig;
import com.sphereon.sdk.pdf.model.ConversionSettings;
import com.sphereon.sdk.pdf.model.ConversionSettings.ContainerConversionEnum;
import com.sphereon.sdk.pdf.model.ConversionSettings.EngineEnum;
import com.sphereon.sdk.pdf.model.ConversionSettings.OcrModeEnum;
import com.sphereon.sdk.pdf.model.ConversionSettings.VersionEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.EnumSet;

public class VisionConfiguration extends AbstractAuthConfig {
    private String tempDir = System.getProperty("java.io.tmpdir");
    private String conversionEngine = EngineEnum.PREMIUM.name();
    private String containerConversion = ContainerConversionEnum.ALL.name();
    private String ocrMode = OcrModeEnum.AUTO.name();
//    private List<String> outputFileFormats = new ArrayList<>();
    private String pdfVersion = "PDF_A_1b";
    private Boolean actionEnabled = true;

    private Boolean registerBlockchain = false;

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        if (StringUtils.isNotEmpty(tempDir)) {
            this.tempDir = tempDir;
            new File(tempDir).mkdirs();
        }
    }

    public String getConversionEngine() {
        return conversionEngine;
    }

    public void setConversionEngine(String conversionEngine) {
        this.conversionEngine = conversionEngine;
    }

    public String getContainerConversion() {
        return containerConversion;
    }

    public void setContainerConversion(String containerConversion) {
        this.containerConversion = containerConversion;
    }

    public String getOcrMode() {
        return ocrMode;
    }

    public void setOcrMode(String ocrMode) {
        this.ocrMode = ocrMode;
    }

//    public List<String> getOutputFileFormats() {
//        return outputFileFormats;
//    }

//    public void addOutputFileFormat(String outputFileFormat) {
//        if (!outputFileFormats.contains(outputFileFormat)) {
//            outputFileFormats.add(outputFileFormat);
//        }
//    }

//    public void setOutputFileFormats(List<String> outputFileFormats) {
//        this.outputFileFormats = outputFileFormats;
//    }

    public String getPdfVersion() {
        return pdfVersion;
    }

    public void setPdfVersion(String pdfVersion) {
        this.pdfVersion = pdfVersion;
    }


    protected ConversionSettings updateSettings(ConversionSettings settings) {
        settings.containerConversion(getValueOrDefault(ContainerConversionEnum.class, containerConversion, ContainerConversionEnum.ALL));
        settings.engine(getValueOrDefault(EngineEnum.class, conversionEngine, EngineEnum.PREMIUM));
        settings.ocrMode(getValueOrDefault(OcrModeEnum.class, ocrMode, OcrModeEnum.AUTO));
        settings.version(getValueOrDefault(VersionEnum.class, pdfVersion, VersionEnum.A_1B));
        return settings;
    }

    private <T extends Enum<T>> T getValueOrDefault(Class<T> enumClass, String id, T defaultValue) {
        EnumSet<T> enumerations = EnumSet.allOf(enumClass);
        if (enumerations == null || enumerations.isEmpty() || StringUtils.isEmpty(id)) {
            return defaultValue;
        }
        for (T enumeration : enumerations) {
            if (enumeration.name().equalsIgnoreCase(id) || enumeration.toString().equalsIgnoreCase(id)) {
                return enumeration;
            }
        }
        return defaultValue;
    }

    public boolean getActionEnabled() {
        return !Boolean.FALSE.equals(actionEnabled);
    }

    public void setActionEnabled(boolean actionEnabled) {
        this.actionEnabled = actionEnabled;
    }


    public Boolean getRegisterBlockchain() {
        return !Boolean.FALSE.equals(registerBlockchain);
    }

    public void setRegisterBlockchain(Boolean registerBlockchain) {
        this.registerBlockchain = registerBlockchain;
    }

}
