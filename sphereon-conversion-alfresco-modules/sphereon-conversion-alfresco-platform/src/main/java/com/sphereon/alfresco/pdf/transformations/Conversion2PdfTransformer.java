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

package com.sphereon.alfresco.pdf.transformations;

import com.sphereon.alfresco.pdf.ConversionDelegate;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.repo.content.transform.TransformerConfig;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;

/**
 * Created by niels on 03-Jul-17.
 */
public class Conversion2PdfTransformer extends AbstractContentTransformer2 {

    private ConversionDelegate delegate;
    public static final String NAME = TransformerConfig.PREFIX + "sphereon.conversion2pdf";


    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype,
                                   TransformationOptions options) {
        return getDelegate().isTransformable(sourceMimetype, targetMimetype, options);
    }

    @Override
    protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions opions) throws Exception {
        getDelegate().transform(reader, writer, opions);
    }

    public ConversionDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ConversionDelegate delegate) {
        this.delegate = delegate;
    }

}
