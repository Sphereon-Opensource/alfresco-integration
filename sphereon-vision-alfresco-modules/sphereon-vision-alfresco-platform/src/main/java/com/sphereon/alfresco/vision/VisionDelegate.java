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

import com.sphereon.alfresco.base.auth.AccessTokenRequester;
import com.sphereon.alfresco.vision.transformations.TransformDestination;
import com.sphereon.sdk.pdf.api.Conversion2PDFApi;
import com.sphereon.sdk.pdf.model.ConversionJobResponse;
import com.sphereon.sdk.pdf.model.ConversionSettings;
import com.sphereon.sdk.pdf.model.Lifecycle;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.transform.ContentTransformerHelper;
import org.alfresco.repo.content.transform.TransformerConfig;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisionDelegate extends ContentTransformerHelper {
    private enum FileDirection { SOURCE, TARGET}

    private static Log log = LogFactory.getLog(VisionDelegate.class);
    private Conversion2PDFApi api = new Conversion2PDFApi();



    private FileFolderService fileFolderService;

    private VisionConfiguration config;
    private AccessTokenRequester accessTokenRequester = new AccessTokenRequester();
    public static final String NAME = TransformerConfig.PREFIX + "conversion-delegate";

    public VisionConfiguration getConfig() {
        return config;
    }

    public void setConfig(VisionConfiguration configuration) {
        this.config = configuration;
    }


    public boolean isTransformable(String sourceMimetype, String targetMimetype,
                                   TransformationOptions options) {
        return getConfig().isEnabled() && TransformDestination.get(sourceMimetype, targetMimetype) != null;
    }

    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) {
        if (!getConfig().isEnabled()) {
            log.warn("Conversion configuration not enabled. Transformation will not be executed");
            return;
        }
        FileInfo sourceFileInfo = fileFolderService.getFileInfo(options.getSourceNodeRef());
        if (sourceFileInfo.isFolder() || sourceFileInfo.isLink()) {
            log.warn("Conversion not possible for folder or link. source node " + options.getSourceNodeRef().getId());
            return;
        }
        ConversionSettings conversionSettings = getConfig().updateSettings(new ConversionSettings());
//        options.get

        String sourceBaseName = FilenameUtils.getBaseName(sourceFileInfo.getName());
        String sourceExt = FilenameUtils.getExtension(sourceFileInfo.getName());
        final String targetExt = writer.getMimetype() == null ? "pdf" : getMimetypeService().getExtension(writer.getMimetype());
        log.info(String.format("Converting %s of node %s from %s to %s using engine...", sourceFileInfo.getName(), options.getSourceNodeRef().getId(), getMimetypeService().getExtension(reader.getMimetype()), targetExt, conversionSettings.getEngine()));

        File file = null;
        File resultFile = null;
        ConversionJobResponse conversionJobResponse = null;

        try {


            api.getApiClient().setAccessToken(getAccessToken());

            conversionJobResponse = api.createJob(conversionSettings);

            file = createTempFile(sourceBaseName, sourceExt, FileDirection.SOURCE);
            reader.getContent(file);
            conversionJobResponse = api.addInputFile(conversionJobResponse.getJobId(), file, file.getName());
            conversionJobResponse = api.submitJob(conversionJobResponse.getJobId(), conversionJobResponse.getJob());

            int count = 0;
            while ((conversionJobResponse.getStatus() == ConversionJobResponse.StatusEnum.INPUTS_UPLOADED || conversionJobResponse.getStatus() == ConversionJobResponse.StatusEnum.PROCESSING) && count++ < 120) {
                Thread.sleep(500);
                conversionJobResponse = api.getJob(conversionJobResponse.getJobId());
            }
            if (conversionJobResponse.getStatus() != ConversionJobResponse.StatusEnum.DONE) {
                throw new AlfrescoRuntimeException(String.format("Could not convert %s file to PDF: %s", file.getName(), conversionJobResponse.getStatusMessage()));
            }

            // TODO: Use streams
            byte[] convertedContent = api.getStream(conversionJobResponse.getJobId());
            resultFile = createTempFile(sourceBaseName, targetExt, FileDirection.TARGET);
            FileUtils.writeByteArrayToFile(resultFile, convertedContent);
            writer.putContent(resultFile);
            log.info(String.format("Converted %s of node %s from %s to %s", sourceFileInfo.getName(), options.getSourceNodeRef().getId(), getMimetypeService().getExtension(reader.getMimetype()), targetExt));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        } finally {
            deleteTempFile(file);
            deleteTempFile(resultFile);
            if (conversionJobResponse == null || conversionJobResponse.getStatus() != ConversionJobResponse.StatusEnum.DONE) {
                return;
            }
            if (conversionJobResponse.getJob().getSettings().getJobLifecycle().getType() != Lifecycle.TypeEnum.RETRIEVAL) {
                try {
                    // Only delete succesfull jobs when delete on retrieval is not specified
                    api.deleteJob(conversionJobResponse.getJobId());
                } catch (Exception e) {
                    log.warn("Error during job cleanup: " + e.getMessage(), e);
                }
            }
        }
    }

    private void deleteTempFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            log.error("Could not delete temp file " + file.getAbsolutePath(), e);
            file.deleteOnExit();
        }
    }

    private File createTempFile(String filename, String ext, FileDirection direction) {
        try {
            Path path = Paths.get(getConfig().getTempDir());
            path = direction == FileDirection.SOURCE ? path.resolve("source") : path.resolve("converted");
            if (!path.toFile().exists()) {
                log.info("Creating temp path for conversion: " + path.toFile().getAbsolutePath());
                path.toFile().mkdirs();
            }

            return Files.createFile(path.resolve(filename + "." + ext)).toFile();
        } catch (IOException ioe) {
            throw new AlfrescoRuntimeException(ioe.getMessage(), ioe);
        }

    }

    private String getAccessToken() {
        return accessTokenRequester.get(config);
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }
}