package com.aihire.backend.util;

import com.aihire.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveResume(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to upload empty file");
        }

        // Check size (Maximum 5 MB = 5 * 1024 * 1024 bytes)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size exceeds the limit of 5MB");
        }

        // Check extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("Invalid file name");
        }

        String extension = getFileExtension(originalFilename);
        if (!extension.equalsIgnoreCase("pdf") && 
            !extension.equalsIgnoreCase("doc") && 
            !extension.equalsIgnoreCase("docx")) {
            throw new BadRequestException("Only PDF, DOC, and DOCX files are allowed");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique file name
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename.replaceAll("\\s+", "_");
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the web access URL path
            return "/uploads/resumes/" + uniqueFilename;
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file. Error: " + ex.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return filename.substring(lastIndex + 1);
    }
}
