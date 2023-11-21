package com.example.service;

import com.example.model.entity.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {
    private final Path root = Paths.get("authentication-service/src/main/resources/images/");

    @Value("${image.url}")
    private String url;

    public Image uploadFile(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
//        if (!Files.exists(root)) {
//            Files.createDirectories(root);
//        }
        Files.copy(file.getInputStream(), this.root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return new Image(
                filename,
                url + filename,
                "image/" + StringUtils.getFilenameExtension(file.getOriginalFilename()),
                file.getSize()
        );
    }

    public List<Image> uploadFileMultipleFile(List<MultipartFile> files) throws IOException {
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            imageList.add(uploadFile(file));
        }
        return imageList;
    }

    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                throw new RuntimeException("Could not read the file!");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}
