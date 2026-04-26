package com.factory.utils;

import com.factory.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class FileUploadUtils {

    @Value("${upload.path:./uploads}")
    private String uploadPath;
    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;
    @Value("${upload.avatar-max-size:2097152}")
    private long avatarMaxSize;
    @Value("${upload.material-image-max-size:3145728}")
    private long materialImageMaxSize;

    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList("image/jpeg","image/png","image/gif","image/webp"));

    @PostConstruct
    public void init() { new File(uploadPath).mkdirs(); }

    public String uploadAvatar(MultipartFile file) throws IOException {
        validate(file, avatarMaxSize);
        return save(file, "avatar");
    }

    public String uploadMaterialImage(MultipartFile file) throws IOException {
        validate(file, materialImageMaxSize);
        return save(file, "material");
    }

    private void validate(MultipartFile file, long maxSize) {
        if (file.isEmpty()) throw new BusinessException("文件为空");
        if (file.getSize() > maxSize) throw new BusinessException("文件过大");
        if (!ALLOWED.contains(file.getContentType())) throw new BusinessException("不支持的文件类型");
    }

    private String save(MultipartFile file, String sub) throws IOException {
        String ext = "";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) ext = name.substring(name.lastIndexOf('.'));
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newName = UUID.randomUUID() + ext;
        File dir = new File(uploadPath + File.separator + sub + File.separator + datePath);
        dir.mkdirs();
        File target = new File(dir, newName);
        Thumbnails.of(file.getInputStream()).size(800,800).keepAspectRatio(true).outputQuality(0.85).toFile(target);
        return urlPrefix + "/" + sub + "/" + datePath + "/" + newName;
    }
}