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

package com.sphereon.alfresco.blockchain.proof.actions;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.webscripts.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerifyFileWebScript extends DeclarativeWebScript {
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        if (req.getParameter("nodeRef") == null) {
            throw new AlfrescoRuntimeException("Cannot verify file on blockchain. Node argument is missing from request");
        }
        String nodeRef = req.getParameter("nodeRef");

        model.put("currentDateTime", new Date());
        return model;
    }


}
