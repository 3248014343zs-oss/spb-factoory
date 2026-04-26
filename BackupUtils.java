package com.factory.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BackupUtils {

    @Value("${spring.datasource.url}") private String url;
    @Value("${spring.datasource.username}") private String username;
    @Value("${spring.datasource.password}") private String password;

    public String backup() {
        try {
            String db = extractDatabase(url);
            String dir = System.getProperty("user.dir") + "/backup/";
            new File(dir).mkdirs();
            String file = dir + db + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".sql";

            List<String> cmd = new ArrayList<>();
            cmd.add("mysqldump"); cmd.add("-u" + username); cmd.add("-p" + password);
            cmd.add("--add-drop-table"); cmd.add("--routines"); cmd.add("--triggers");
            cmd.add("--result-file=" + file); cmd.add(db);

            List<String> masked = new ArrayList<>(cmd);
            for (int i=0; i<masked.size(); i++) if (masked.get(i).startsWith("-p")) masked.set(i, "-p******");
            log.info("备份命令: {}", String.join(" ", masked));

            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            if (p.waitFor() != 0) throw new RuntimeException("备份失败");
            log.info("备份成功: {}", file);
            return file;
        } catch (Exception e) {
            log.error("备份异常", e);
            throw new RuntimeException("备份失败");
        }
    }

    private String extractDatabase(String url) {
        int s = url.lastIndexOf('/'), e = url.indexOf('?');
        return url.substring(s+1, e==-1 ? url.length() : e);
    }
}