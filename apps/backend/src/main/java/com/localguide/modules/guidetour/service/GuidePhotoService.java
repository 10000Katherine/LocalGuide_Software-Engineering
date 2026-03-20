package com.localguide.modules.guidetour.service;

import com.localguide.common.exception.ApiException;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.dto.GuidePhotoUploadResponse;
import com.localguide.modules.guidetour.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class GuidePhotoService {

    private static final long MAX_PHOTO_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final GuideService guideService;
    private final GuideRepository guideRepository;
    private final Path storageDir;

    public GuidePhotoService(GuideService guideService,
                             GuideRepository guideRepository,
                             @Value("${storage.guide-photo-dir:./uploads/guide-photos}") String storageDir) {
        this.guideService = guideService;
        this.guideRepository = guideRepository;
        this.storageDir = Path.of(storageDir).toAbsolutePath().normalize();
    }

    @Transactional
    public GuidePhotoUploadResponse uploadMyPhoto(String email, MultipartFile file) {
        validateImage(file);
        ensureStorageDir();

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported image format");
        }

        String filename = UUID.randomUUID() + "." + extension;
        Path destination = storageDir.resolve(filename).normalize();

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store image");
        }

        String photoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/guides/photo/")
                .path(filename)
                .toUriString();

        Guide guide = guideService.requireCurrentGuide(email);
        guide.setPhotoUrl(photoUrl);
        guideRepository.save(guide);

        return new GuidePhotoUploadResponse(photoUrl);
    }

    @Transactional(readOnly = true)
    public Resource loadPhotoResource(String filename) {
        Path candidate = storageDir.resolve(filename).normalize();
        if (!candidate.startsWith(storageDir)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Resource resource = new UrlResource(candidate.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Photo not found");
            }
            return resource;
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read photo");
        }
    }

    @Transactional(readOnly = true)
    public MediaType resolveContentType(String filename) {
        Path candidate = storageDir.resolve(filename).normalize();
        try {
            String contentType = Files.probeContentType(candidate);
            if (contentType == null || contentType.isBlank()) {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
            return MediaType.parseMediaType(contentType);
        } catch (IOException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Image file is required");
        }
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Image size must be 5MB or less");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase();
    }

    private void ensureStorageDir() {
        try {
            Files.createDirectories(storageDir);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initialize storage");
        }
    }
}
