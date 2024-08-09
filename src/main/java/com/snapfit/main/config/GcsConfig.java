package com.snapfit.main.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class GcsConfig {

    @Value("${location}")
    private String location;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        System.out.println(location);
//        ClassPathResource resource = new ClassPathResource(location);
        Files.createDirectories(Path.of("/Users/yongha/Downloads/my"));
        InputStream inputStream = Files.newInputStream(Path.of(location));
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();
    }
}
