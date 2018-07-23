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

package com.sphereon.alfresco.blockchain.proof;

import com.sphereon.alfresco.base.auth.AbstractAuthConfig;
import com.sphereon.sdk.blockchain.proof.model.ContentRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BlockchainProofConfig extends AbstractAuthConfig {

    private String tempDir = System.getProperty("java.io.tmpdir");
    private String configName;
    private String hashProvider;
    private String encoding;
    private List<String> targetChains;


    public String getHashProvider() {
        return hashProvider;
    }

    public void setHashProvider(String hashProvider) {
        this.hashProvider = hashProvider;
    }

    public List<String> getTargetChains() {
        return targetChains;
    }

    public void setTargetChains(List<String> targetChains) {
        this.targetChains = targetChains;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public ContentRequest.HashProviderEnum getHashProviderEnum() {
        //// FIXME: 12-1-2018
        return ContentRequest.HashProviderEnum.CLIENT;
    }

    public boolean isHexEncoding() {
        return StringUtils.isEmpty(encoding) || "hex".equalsIgnoreCase(encoding);
    }

    public boolean isBase64Encoding() {
        return "base64".equalsIgnoreCase(encoding);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (!StringUtils.isEmpty(encoding) && !"hex".equalsIgnoreCase(encoding) && !"base64".equalsIgnoreCase(encoding)) {
            throw new AlfrescoRuntimeException("Invalid encoding for blockcain-proof. Should be base64 or hex. Provided: " + encoding);
        }
        this.encoding = encoding;
    }
}
