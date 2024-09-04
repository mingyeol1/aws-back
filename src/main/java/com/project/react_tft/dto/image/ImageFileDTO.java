package com.project.react_tft.dto.image;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageFileDTO {

    private List<MultipartFile> files;
}
