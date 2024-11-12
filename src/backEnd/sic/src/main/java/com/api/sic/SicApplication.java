package com.api.sic;

import org.modelmapper.ModelMapper;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.api.sic.backend.service.Image;

import nu.pattern.OpenCV;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SicApplication implements WebMvcConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(SicApplication.class, args);
        OpenCV.loadShared();
	
		// Image.resetDiretorio("C:\\Users\\felli\\Pictures\\Imagens DIPAS\\img\\result");

        // //Segmentando uma pasta de imagens
        // //org.example.Image.segmentImages("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img", "jpeg" , 4, "sim");

        // //Segmentando apenas uma imagem especifica
        // Image.segmentImage("C:\\Users\\felli\\Pictures\\Imagens DIPAS\\img", 2 , "não");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/WEB-INF/images/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
