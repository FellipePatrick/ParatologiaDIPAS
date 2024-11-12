package com.api.sic.backend.service;

import org.opencv.core.Core;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageProcessService{

    private final Path root = Paths.get("src/main/webapp/WEB-INF/images");

    
    public void processarImagem(String imageName){
        String x = "C:\\Users\\felli\\Desktop\\ParatologiaDIPAS\\src\\backEnd\\sic\\src\\main\\webapp\\WEB-INF\\images";
        System.err.println("Path: " + x);
        System.out.println("OpenCV version: " + Core.VERSION);

        Image.segmentImage(x, "não", imageName);
    }
}