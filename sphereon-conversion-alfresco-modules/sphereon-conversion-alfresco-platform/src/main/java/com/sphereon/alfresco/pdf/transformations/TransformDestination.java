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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by niels on 10-7-2017.
 */
public enum TransformDestination {
    PDF(TransformableMimeType.PDF, TransformableMimeType.PDF, TransformableMimeType.JPG, TransformableMimeType.PNG, TransformableMimeType.TIFF, TransformableMimeType.GIF, TransformableMimeType.TEXT_PLAIN, TransformableMimeType.CSV, TransformableMimeType.RTF, TransformableMimeType.EXCEL, TransformableMimeType.EXCEL_OPENXML, TransformableMimeType.MSG, TransformableMimeType.WORD, TransformableMimeType.WORD_OPENXML);


    private final List<TransformableMimeType> sources = new ArrayList<>();
    private final TransformableMimeType target;


    TransformDestination(TransformableMimeType target, TransformableMimeType sourceOne, TransformableMimeType... extraSources) {
        this.target = target;
        sources.add(sourceOne);
        if (extraSources == null || extraSources.length == 0) {
            return;
        }

        for (TransformableMimeType mimeType : extraSources) {
            if (!sources.contains(mimeType)) {
                sources.add(mimeType);
            }
        }
    }

    public TransformableMimeType getTarget() {
        return target;
    }

    public List<TransformableMimeType> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public boolean supports(TransformableMimeType source, TransformableMimeType target) {
        return supports(source.getMimeType(), target.getMimeType());
    }

    public boolean supports(String source, String target) {
        if (target == null || source == null || !getTarget().getMimeType().toLowerCase().contains(target.toLowerCase())) {
            return false;
        }

        for (TransformableMimeType mimeType : getSources()) {
            if (mimeType.getMimeType().toLowerCase().contains(source.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static TransformDestination get(String source, String target) {
        for (TransformDestination destination : values()) {
            if (destination.supports(source, target)) {
                return destination;
            }
        }
        return null;
    }


}
