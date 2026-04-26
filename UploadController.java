package com.factory.controller;

import com.factory.common.Result;
import com.factory.utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadUtils fileUploadUtils;

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadUtils.uploadAvatar(file);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/material")
    public Result<String> uploadMaterialImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadUtils.uploadMaterialImage(file);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}