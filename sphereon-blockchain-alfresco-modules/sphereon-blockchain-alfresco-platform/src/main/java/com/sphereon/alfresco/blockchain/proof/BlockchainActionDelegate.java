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


import com.sphereon.alfresco.base.auth.AccessTokenRequester;
import com.sphereon.sdk.blockchain.proof.api.RegistrationApi;
import com.sphereon.sdk.blockchain.proof.api.VerificationApi;
import com.sphereon.sdk.blockchain.proof.handler.ApiException;
import com.sphereon.sdk.blockchain.proof.model.ContentRequest;
import com.sphereon.sdk.blockchain.proof.model.RegisterContentResponse;
import com.sphereon.sdk.blockchain.proof.model.VerifyContentResponse;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Base64;
import java.util.Optional;

@Component
@Scope("prototype")
public class BlockchainActionDelegate {

    private Base64.Encoder base64Enc = Base64.getEncoder();

    private static Log log = LogFactory.getLog(BlockchainActionDelegate.class);
    private RegistrationApi registrationApi = new RegistrationApi();
    private VerificationApi verificationApi = new VerificationApi();

    @Autowired
    private AccessTokenRequester accessTokenRequester;


    @Autowired
    private BlockchainProofConfig config;

    private BlockchainActionDelegate() {
    }

    public BlockchainActionDelegate setConfig(BlockchainProofConfig config) {
        this.config = config;
        return this;
    }

    protected String getAccessToken() {
        return accessTokenRequester.get(config);
    }


    public BlockchainProofConfig getConfig() {
        return config;
    }

    public VerifyContentResponse verifyContent(byte[] content, Optional<String> requestId) {
        registrationApi.getApiClient().setAccessToken(getAccessToken());
        String id = requestId.orElse("register-" + content.hashCode());
        byte[] reqContent = getConfig().isHexEncoding() ? DatatypeConverter.printHexBinary(content).toLowerCase().getBytes() : base64Enc.encode(content);
        ContentRequest contentRequest = new ContentRequest().content(reqContent).hashProvider(config.getHashProviderEnum());
        try {
            log.info(String.format("Verify content for request '%s' using config %s, for hash %s...", id, getConfig().getConfigName(), new String(reqContent)));
            VerifyContentResponse verifyContentResponse = verificationApi.verifyUsingContent(getConfig().getConfigName(), contentRequest, id, null, null, null);
            log.info(String.format("Verified content for request '%s' using config %s. Registered: %s at %s with hash %s ", id, getConfig().getConfigName(), verifyContentResponse.getRegistrationState(), verifyContentResponse.getRegistrationTime(), verifyContentResponse.getHash()));
            return verifyContentResponse;
        } catch (ApiException e) {
            log.error(extractErrorMessage(e), e);
            throw new AlfrescoRuntimeException(extractErrorMessage(e), e);
        }
    }

    public RegisterContentResponse registerContent(byte[] content, Optional<String> requestId) {
        registrationApi.getApiClient().setAccessToken(getAccessToken());
        String id = requestId.orElse("register-" + content.hashCode());
        byte[] reqContent = getConfig().isHexEncoding() ? DatatypeConverter.printHexBinary(content).toLowerCase().getBytes() : base64Enc.encode(content);
        ContentRequest contentRequest = new ContentRequest().content(reqContent).hashProvider(config.getHashProviderEnum());
        try {
            log.info(String.format("Registering content for request '%s' using config %s, for hash %s...", id, getConfig().getConfigName(), new String(reqContent)));
            RegisterContentResponse registerContentResponse = registrationApi.registerUsingContent(getConfig().getConfigName(), contentRequest, id, null, null, null);
            log.info(String.format("Registered content for request '%s' using config %s. Hash: %s", id, getConfig().getConfigName(), registerContentResponse.getHash()));
            return registerContentResponse;
        } catch (ApiException e) {
            log.error(extractErrorMessage(e), e);
            throw new AlfrescoRuntimeException(extractErrorMessage(e), e);
        }
    }

    private String extractErrorMessage(ApiException e) {
        return String.format("%s, response: %s", e.getMessage(), e.getResponseBody());
    }
}
