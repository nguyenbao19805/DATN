package com.poly.thuviendatn.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/Image/Anhbia/**")
                .addResourceLocations("fileC:/Users/ACER/Desktop/DATN-20250626T101041Z-1-001/DATN/src/main/resources/static/Image/Anhbia");
    }
}
