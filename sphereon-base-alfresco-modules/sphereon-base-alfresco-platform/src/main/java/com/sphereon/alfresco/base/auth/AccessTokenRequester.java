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

package com.sphereon.alfresco.base.auth;

import com.sphereon.libs.authentication.api.AuthenticationApi;
import com.sphereon.libs.authentication.api.TokenRequest;
import com.sphereon.libs.authentication.api.TokenResponse;
import com.sphereon.libs.authentication.api.config.ApiConfiguration;
import com.sphereon.libs.authentication.api.config.PersistenceType;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Scope("prototype")
public class AccessTokenRequester {

    private static Log log = LogFactory.getLog(AccessTokenRequester.class);

    public String get(AuthConfig config) {
        try {
            if (config == null || !config.isEnabled()) {
                throw new AlfrescoRuntimeException("Sphereon authentication not enabled. Cannot continue");
            }

            if (config.getAuthMode() == AuthConfig.Mode.ACCESS_TOKEN) {
                return getFixedAccessToken(config);
            } else {
                return getOAuth2AccessToken(config);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }

    }


    private String getFixedAccessToken(AuthConfig config) {
        if (StringUtils.isEmpty(config.getAccessToken())) {
            throw new AlfrescoRuntimeException(String.format("Access Token not configured for application %s, but access-token authentication is enabled", config.getApplicationName()));
        }
        return config.getAccessToken();
    }

    private String getOAuth2AccessToken(AuthConfig config) {
        if (StringUtils.isEmpty(config.getConsumerKey()) || StringUtils.isEmpty(config.getConsumerSecret())) {
            throw new AlfrescoRuntimeException(String.format("Consumer key or secret not configured for application %s, but oAuth2 authentication is enabled", config.getApplicationName()));
        }
        ApiConfiguration apiConfiguration = new ApiConfiguration.Builder().withPersistenceType(PersistenceType.IN_MEMORY).withApplication(config.getApplicationName() != null ? config.getApplicationName() : config.getClass().getSimpleName().toLowerCase()).build();
        AuthenticationApi authenticationApi = new AuthenticationApi.Builder().withConfiguration(apiConfiguration).build();

        TokenRequest tokenRequest = authenticationApi.requestToken().withConsumerKey(config.getConsumerKey()).withConsumerSecret(config.getConsumerSecret()).build();
        TokenResponse tokenResponse = tokenRequest.execute();
        return tokenResponse.getAccessToken();
    }

}
