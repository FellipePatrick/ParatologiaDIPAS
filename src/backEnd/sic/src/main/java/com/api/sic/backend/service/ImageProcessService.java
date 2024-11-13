package com.api.sic.backend.service;

import org.opencv.core.Core;
import org.springframework.stereotype.Service;


@Service
public class ImageProcessService{

    //Estudar uma melhor implementação para isso, de pegar o path da pasta de imagens
    
    private final String root ="src\\main\\webapp\\WEB-INF\\images";

    
    public void processarImagem(String imageName){

        Image.resetDiretorio(root+"\\result");

        Image.segmentImage(root, root+"\\"+imageName,imageName, "não");
    }
}