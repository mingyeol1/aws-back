package com.project.react_tft.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.react_tft.dto.image.ImageResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MeetBoardImageController {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<List<ImageResultDTO>> upload(@RequestPart("files") List<MultipartFile> files) {
        List<ImageResultDTO> list = new ArrayList<>();

        files.forEach(file -> {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String s3Filename = uuid + "_" + originalFilename;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            try {
                amazonS3Client.putObject(bucket, s3Filename, file.getInputStream(), metadata);
                String s3Url = amazonS3Client.getUrl(bucket, s3Filename).toString();

                list.add(ImageResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalFilename)
                        .img(true)
                        .s3Url(s3Url)
                        .build());
            } catch (IOException e) {
                log.error("Error uploading file", e);
            }
        });

        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/remove/{fileName}")
    public ResponseEntity<Boolean> removeFile(@PathVariable String fileName) {
        try {
            amazonS3Client.deleteObject(bucket, fileName);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<String> viewFile(@PathVariable String fileName) {
        String s3Url = amazonS3Client.getUrl(bucket, fileName).toString();
        return ResponseEntity.ok(s3Url);
    }
}