package com.api.sic.backend.service;


import java.util.Map;

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

    
            Imagem imagemOriginal = new Imagem();
            imagemOriginal.setNome("Original");
            imagemOriginal.setCodigoIm(codigoIm);
            imagemOriginal.setPath(pathImage);
            imagemOriginal.setRelatorio(relatorioService.findById(idRelatorio));
            imagemService.create(imagemOriginal);

            Imagem imagemCirculada = new Imagem();
            imagemCirculada.setNome("Pré Processada");
            imagemCirculada.setCodigoIm(codigoIm);
            imagemCirculada.setPath("Circulada" + pathImage);
            imagemCirculada.setRelatorio(relatorioService.findById(idRelatorio));
            imagemService.create(imagemCirculada);

            Map<String, String> images = Image.segmentImage(root, root+"\\"+pathImage,pathImage, zoom);

            for (Map.Entry<String, String> entry : images.entrySet()) {
                Imagem imagemParasita = new Imagem();
                imagemParasita.setPath(entry.getKey());
                imagemParasita.setCodigoIm(codigoIm);
                imagemParasita.setNome(entry.getValue()); 
                imagemParasita.setRelatorio(relatorioService.findById(idRelatorio));
                imagemService.create(imagemParasita);
            }

        }
    } 

    public void processarImagem(String pathImage, String codigoIm, boolean zoom){ {

        Imagem imagemOriginal = new Imagem();
        imagemOriginal.setNome("Original");
        imagemOriginal.setCodigoIm(codigoIm);
        imagemOriginal.setPath(pathImage);
        imagemService.create(imagemOriginal);

        Imagem imagemCirculada = new Imagem();
        imagemCirculada.setNome("Pré Processada");
        imagemCirculada.setCodigoIm(codigoIm);
        imagemCirculada.setPath("Circulada" + pathImage);
        imagemService.create(imagemCirculada);

        Map<String, String> images = Image.segmentImage(root, root+"\\"+pathImage,pathImage, zoom);

         for (Map.Entry<String, String> entry : images.entrySet()) {
                Imagem imagemParasita = new Imagem();
                imagemParasita.setPath(entry.getKey()); 
                imagemParasita.setCodigoIm(codigoIm);
                imagemParasita.setNome(entry.getValue());
                imagemService.create(imagemParasita);
            }

    }
} 
}