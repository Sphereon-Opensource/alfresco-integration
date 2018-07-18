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

(function () {
    YAHOO.Bubbling.fire("registerAction",
        {
            actionName: "onActionBlockchainProofVerifyRegistration",
            fn: function com_sphereon_alfresco_blockchain_proof_js_onActionVerifyBlockchainRegistration(file) {
                this.modules.actions.genericAction(
                    {

                        success: {
                            callback: {
                                fn: function com_sphereon_alfresco_blockchain_proof_js_onActionVerifyBlockchainRegistrationSuccess(response) {
                                    Alfresco.util.PopupManager.displayPrompt(
                                        {
                                            title: this.msg("Document verification result"),
                                            text: response.serverResponse.responseText,
                                            buttons: [
                                                {
                                                    text: this.msg("button.ok"),
                                                    handler: function com_sphereon_alfresco_blockchain_proof_js_onActionCallWebScriptSuccess_success_ok() {
                                                        this.destroy();
                                                    },
                                                    isDefault: true
                                                }
                                                ]
                                        });

                                },
                                scope: this
                            }
                        },
                        failure: {
                            message: this.msg("com.sphereon.alfresco.actions.blockchain.callWebScript.msg.failure",
                                file.displayName, Alfresco.constants.USERNAME)
                        },
                        webscript: {
                            name: "/sphereon/blockchain/proof/verify-file?format=text&nodeRef={nodeRef}&fileName={fileName}",
                            stem: Alfresco.constants.PROXY_URI,
                            method: Alfresco.util.Ajax.GET,
                            params: {
                                nodeRef: file.nodeRef,
                                fileName: file.displayName
                            }
                        },
                        config: {}
                    });

            }
        });
})();