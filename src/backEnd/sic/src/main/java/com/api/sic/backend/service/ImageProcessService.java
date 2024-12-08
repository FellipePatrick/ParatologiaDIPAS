package com.api.sic.backend.service;


import org.springframework.stereotype.Service;

import com.api.sic.backend.domain.Imagem;


@Service
public class ImageProcessService{

    //Estudar uma melhor implementação para isso, de pegar o path da pasta de imagens
    
    private final String root ="src\\main\\webapp\\WEB-INF\\images";

    private ImagemService imagemService;
    private RelatorioService relatorioService;

    ImageProcessService(ImagemService imagemService, RelatorioService relatorioService){
        this.imagemService = imagemService;
        this.relatorioService = relatorioService;
    }
    
    public void processarImagem(String pathImage, long idRelatorio, String codigoIm, boolean zoom){ {


        if (zoom) {
           System.out.println("Zoom ativado");
            
        }else{
            System.out.println("Zoom desativado");
        }
        Imagem imagemOriginal = new Imagem();
        imagemOriginal.setNome("Original");
        imagemOriginal.setCodigoIm(codigoIm);
        imagemOriginal.setPath(pathImage);
        imagemOriginal.setRelatorio(relatorioService.findById(idRelatorio));
        imagemService.create(imagemOriginal);

        Imagem imagemCirculada = new Imagem();
        imagemCirculada.setNome("Circulada");
        imagemCirculada.setCodigoIm(codigoIm);
        imagemCirculada.setPath("Circulada" + pathImage);
        imagemCirculada.setRelatorio(relatorioService.findById(idRelatorio));
        imagemService.create(imagemCirculada);

        Image.segmentImage(root, root+"\\"+pathImage,pathImage, zoom);
    }
} 
}