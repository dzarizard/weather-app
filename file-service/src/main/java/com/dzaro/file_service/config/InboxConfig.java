package com.dzaro.file_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class InboxConfig {

  @Bean
  Path inboxDir(@Value("${archive.inbox.dir}") String dir) {
    return Paths.get(dir);
  }
}