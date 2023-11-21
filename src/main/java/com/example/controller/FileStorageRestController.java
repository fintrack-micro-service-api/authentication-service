package com.example.controller;


import com.example.exception.NotFoundException;
import com.example.model.entity.Image;
import com.example.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("api/v1/file")
public class FileStorageRestController {
    private final FileUploadService imageService;

    public FileStorageRestController(FileUploadService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/upload-image", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload Image")
    public ResponseEntity<Image> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok().body(imageService.uploadFile(file));
    }

    @PostMapping(value = "/upload-multiple-files", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload multiple Image")
    public ResponseEntity<List<Image>> uploadFileMultipleFile(
            @RequestParam("file") List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.ok().body(imageService.uploadFileMultipleFile(files));
    }

    @GetMapping("/get-image")
    @Operation(summary = "Get Image")
    public ResponseEntity<Resource> getImage(
            @RequestParam("file") String fileName
    ) {
        try {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageService.load(fileName));
        } catch (Exception e) {
            throw new NotFoundException("file name :" + fileName + "is not found");
        }
    }


    @GetMapping("/download-image")
    @Operation(summary = "Download Image")
    public ResponseEntity<Resource> downloadImage(
            @RequestParam("file") String fileName
    ) {
        Path path = Paths.get("src/main/resources/images/" + fileName);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            System.out.println("Error message {} " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
