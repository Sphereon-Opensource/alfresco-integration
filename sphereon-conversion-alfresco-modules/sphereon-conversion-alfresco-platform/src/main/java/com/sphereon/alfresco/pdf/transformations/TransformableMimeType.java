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

import org.alfresco.repo.content.MimetypeMap;

/**
 * Created by niels on 10-7-2017.
 */
public enum TransformableMimeType {
    PDF(MimetypeMap.MIMETYPE_PDF), TIFF(MimetypeMap.MIMETYPE_IMAGE_TIFF), JPG(MimetypeMap.MIMETYPE_IMAGE_JPEG), GIF(MimetypeMap.MIMETYPE_IMAGE_GIF), PNG(MimetypeMap.MIMETYPE_IMAGE_PNG), TEXT_PLAIN(MimetypeMap.MIMETYPE_TEXT_PLAIN), RTF("application/rtf"), CSV(MimetypeMap.MIMETYPE_TEXT_CSV), MSG(MimetypeMap.MIMETYPE_OUTLOOK_MSG), EXCEL(MimetypeMap.MIMETYPE_EXCEL), EXCEL_OPENXML(MimetypeMap.MIMETYPE_OPENXML_SPREADSHEET), WORD(MimetypeMap.MIMETYPE_WORD), WORD_OPENXML(MimetypeMap.MIMETYPE_OPENXML_WORDPROCESSING);

    private String mimeType;
    TransformableMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

}
