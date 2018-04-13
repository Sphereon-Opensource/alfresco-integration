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

import org.springframework.util.StringUtils;

public abstract class AbstractAuthConfig implements AuthConfig {
    private String accessToken;
    private String consumerKey;
    private String consumerSecret;
    private String applicationName;
    private String authMethod;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getAuthMethod() {
        return authMethod;
    }

    @Override
    public Mode getAuthMode() {
        if (StringUtils.isEmpty(getAuthMethod())) {
            return Mode.OAUTH2;
        } else if (getAuthMethod().toLowerCase().contains("token") || getAuthMethod().toLowerCase().contains("bearer") || getAuthMethod().toLowerCase().contains("fix")) {
            return Mode.ACCESS_TOKEN;
        }
        return Mode.OAUTH2;

    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    @Override
    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
