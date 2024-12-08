package com.example.demo.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NonNull  MultipartFile sourceFile,
            @NonNull Integer userId) {

        final String fileUploadSubPath = "users" + File.separator + userId;

        return uploadFile(sourceFile , fileUploadSubPath);


    }

    private String uploadFile(
            @NonNull MultipartFile sourceFile,
            @NonNull String fileUploadSubPath) {

        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();

            if (!folderCreated) {
                log.warn("Failed to create folder");
                return null;
            }
        }

        final String fileExtension = getFileExtention(sourceFile.getOriginalFilename());

        // ./uploads/users/1/94292429899.jpg
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try{
            Files.write(targetPath, sourceFile.getBytes());
            log.info("Successfully uploaded file {}", targetPath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Failed to write file", e);
        }

        return null;
    }

    private String getFileExtention(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf(".");

        if(lastDotIndex == -1) {
            return "";
        }

        return fileName.substring(lastDotIndex+1).toLowerCase();
    }
}