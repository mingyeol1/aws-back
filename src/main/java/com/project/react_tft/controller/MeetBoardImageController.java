package com.project.react_tft.controller;

import com.project.react_tft.dto.image.ImageResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class MeetBoardImageController {

    @Value("C:\\upload")
    private String uploadPath;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<List<ImageResultDTO>> upload(@RequestPart("files") List<MultipartFile> files) {

        List<ImageResultDTO> list = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            files.forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath + File.separator + uuid + "_" + originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);

                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.add(ImageResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image).build()
                );
            });
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @DeleteMapping("/remove/{fileName}")
    public ResponseEntity<Boolean> removeFile(@PathVariable String fileName){
        File file = new File(uploadPath + File.separator + fileName);
        boolean removed = false;

        try {
            removed = file.delete();

            if (fileName.startsWith("s_")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(removed);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
