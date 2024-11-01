package com.example.social_network.config;

import com.example.social_network.utils.TagExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public TagExtractor tagExtractor(){
        return new TagExtractor();
    }
}
