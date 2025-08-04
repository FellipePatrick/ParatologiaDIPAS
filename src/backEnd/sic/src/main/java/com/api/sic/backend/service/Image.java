package com.api.sic.backend.service;


import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

// import nu.pattern.OpenCV;

import java.io.File;
// import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class Image {
    /**
     * Classe Image, que fornece métodos para análise de imagens.
     *
     * @author Fellipe Patrick
     * @version 1.0
     * */

    // /**
    //  * O método segmentImages é usado para ler várias imagens e processá-las.
    //  *
    //  * @param path      O caminho para o diretório que contém os arquivos de imagem.
    //  * @param extension A extensão dos arquivos de imagem.
    //  * @param qtdImages A quantidade de imagens a serem processadas.
    //  * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
    //  *
    //  */



    //Depois estudar a impplementação desse método

    // public static void segmentImages(String path, String extension, int qtdImages, String zoom){
      
    //     for (int cont = 1; cont <= qtdImages; cont++) {
    //         // Carrega a imagem da vez
    //         Mat image = Imgcodecs.imread(path +"\\image" +cont +"."+ extension);
    //         // Verifica se a imagem foi carregada corretamente
    //         if (image.empty()) {
    //             System.out.println("Erro ao carregar a imagem!");
    //             return;
    //         }
    //         segmentImage(path, cont, zoom);
    //     }
    // }


    /**
     * O método segmentImage é usado para ler uma imagem e processá-la.
     *
     * @param path O caminho para o diretório que contém os arquivos de imagem.
     * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
     *
     */
    public static List<String> segmentImage(String path, String im, String imName, boolean zoom){
  
        // int cont = 1;

        Mat image = Imgcodecs.imread(im);

        // Mat ims = image.clone();
        if (image.empty()) {
            List<String> images = new ArrayList<>();
            System.out.println("Erro ao carregar a imagem!");
            return images;
        }
        Mat result;
        Mat orig;

        if (zoom) {
            result = ajustaBrilhoContrasteZoom(image);
            String resultPath = path + File.separator + "result" + File.separator + "orig" + imName;
            Imgcodecs.imwrite(resultPath, result);
            List<String> outputImage = findBlackRegion(image, image, result,path + File.separator, imName, zoom);

            return outputImage;
        } else {
            result = processImagePhone(image);
            orig = result;
            result = ajustaBrilhoContraste(result);
            List<String> outputImage = findBlackRegion(orig,image, result, path + File.separator, imName, zoom);
           
            return outputImage;
        }
    }

    /**
     * O método findBlackRegion é usado para encontrar na imagem as regiões pretas, que possivelmente são os foregrounds procurados,
     * possiveis objetos.
     *
     * @param imageOriginal É uma versão original da imagem tratada.
     * @param inputImage É a imagem depois de ajustar o brilho e o contraste.
     * @param outputPath É o path de saída para armazenar os objetos encontrados.
     * @param cont É o numero da imagem (imagem1.jpeg, cont = 1).
     * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
     * @return Retorna uma lista de objetos encontrados na imagem.
     *
     */
    public static List<String> findBlackRegion(Mat imageOriginal, Mat imOrig, Mat inputImage, String outputPath, String imName, boolean zoom) {
        // Verificar se a imagem de entrada é vazia
        
        List<String> images = new ArrayList<>();

        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        Mat grayImage = inputImage;

        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 50, 255, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Mat> croppedImages = new ArrayList<>();
        // int x = 5;
        double area;
        double perimeter;
        double circularity;
        double maxObject = 500;
        int cont = 1;
        if(zoom){
            maxObject =  imageOriginal.rows()*5;
        }else{
            if(imageOriginal.cols() > 1280){
                maxObject = 2500;
            }
        }

        // Processar cada contorno que atenda aos critérios de área e circularidade
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            area = rect.area();
            if (area >= maxObject) {
                // Calcular a circularidade
                perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                circularity = 4 * Math.PI * area / (perimeter * perimeter);
                if(circularity > 0.2){
                    // Criar uma imagem de saída destacando a região com alta concentração de preto
                    Mat outputImage = inputImage.clone();
                    Imgproc.rectangle(outputImage, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                    // Salvar a imagem de saída com a região destacada
                    // Imgcodecs.imwrite(outputPath + "blackimg" + cont + imName, outputImage);

                    // Cortar a região encontrada da imagem original
                    Mat croppedImage = new Mat(imageOriginal, rect);
                    croppedImages.add(croppedImage);

                    // Salvar a imagem cortada (opcional)
                    Imgcodecs.imwrite(outputPath + "blackimgcurted" + cont + imName, croppedImage);

                    images.add("blackimgcurted" + cont + imName);

                    cont++;
                }
            }
        }


        Mat outputImage = imOrig.clone();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            area = rect.area();

            if (area >= maxObject) {
                // Calcular a circularidade
                perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                circularity = 4 * Math.PI * area / (perimeter * perimeter);

                if (circularity > 0.2) {
                    // Desenhar o retângulo diretamente na imagem de saída
                    Imgproc.rectangle(outputImage, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                    // Cortar a região encontrada da imagem original (opcional)
                    Mat croppedImage = new Mat(imageOriginal, rect);
                    croppedImages.add(croppedImage);

                    cont++;
                }
            }
        }

        // Salvar a imagem de saída com todas as regiões destacadas
        Imgcodecs.imwrite(outputPath + "Circulada" + imName, outputImage);

        return images;
    }


    /**
     * O método ajustaBrilhoContraste serve para ajustar o brilho de uma imagem já processada, para facilitar o encontro
     * das regiões com concetração de preto na imagem.
     *
     * @param inputImage É uma versão original da imagem já processada.
     *
     * @return Retorna uma imagem após o ajuste de Brilho e Contraste.
     *
     */
    public static Mat ajustaBrilhoContraste(Mat inputImage) {
        // Verificar se a imagem de entrada é vazia
        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        // Converter a imagem para escala de cinza se ainda não estiver
        Mat grayImage = new Mat();
        if (inputImage.channels() > 1) {
            Imgproc.cvtColor(inputImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayImage = inputImage.clone();
        }

        // Encontrar contornos
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(grayImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Encontrar o maior contorno
        double maxArea = 0;
        int maxAreaIdx = -1;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                maxArea = area;
                maxAreaIdx = i;
            }
        }

        if (maxAreaIdx == -1) {
            throw new IllegalArgumentException("Nenhum contorno encontrado na imagem");
        }

        // Criar uma máscara para o maior contorno
        Mat mask = Mat.zeros(grayImage.size(), CvType.CV_8UC1);
        Imgproc.drawContours(mask, contours, maxAreaIdx, new Scalar(255), -1);

        // Calcular o brilho médio da imagem dentro do maior contorno
        // Calcular o brilho médio da imagem
        Scalar meanScalar = Core.mean(grayImage);
        double averageBrightness = meanScalar.val[0];

        // Calcular histograma
        int[] histData = new int[256];
        for (int i = 0; i < grayImage.rows(); i++) {
            for (int j = 0; j < grayImage.cols(); j++) {
                int h = (int) grayImage.get(i, j)[0];
                histData[h]++;
            }
        }

        // Algoritmo de Otsu
        int total = grayImage.rows() * grayImage.cols();
        float sumHist = 0;
        for (int t = 0; t < 256; t++) sumHist += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t];
            if (wB == 0) continue;

            wF = total - wB;
            if (wF == 0) break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB;
            float mF = (sumHist - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        // Aplicar a limiarização binária com o limiar encontrado por Otsu

        // O calculo threshold - aux, depende do brilho médio da imagem, por isso dele assumir outros valores alem de 40.

        int aux = 40;
        if(averageBrightness < 160)
            aux = 100;
        else if(averageBrightness > 160 && averageBrightness < 200)
            aux = 60;

        Mat outputImage = new Mat();
        Imgproc.threshold(grayImage, outputImage, (threshold - aux), 255, Imgproc.THRESH_BINARY);

        return outputImage;
    }


    /**
     * O método ajustaBrilhoContrasteZoom serve para ajustar o brilho de uma imagem já processada, para facilitar o encontro
     * das regiões com concetração de preto na imagem.
     *
     * @param inputImage É uma versão original da imagem já processada.
     *
     * @return Retorna uma imagem após o ajuste de Brilho e Contraste.
     *
     */
    public static Mat ajustaBrilhoContrasteZoom(Mat inputImage) {
        // Verificar se a imagem de entrada é vazia
        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        // Converter a imagem para escala de cinza se ainda não estiver
        Mat grayImage = new Mat();
        if (inputImage.channels() > 1) {
            Imgproc.cvtColor(inputImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayImage = inputImage.clone();
        }

        // Calcular o brilho médio da imagem
        Scalar meanScalar = Core.mean(grayImage);
        double averageBrightness = meanScalar.val[0];

        // Calcular histograma
        int[] histData = new int[256];
        for (int i = 0; i < grayImage.rows(); i++) {
            for (int j = 0; j < grayImage.cols(); j++) {
                int h = (int) grayImage.get(i, j)[0];
                histData[h]++;
            }
        }

        // Algoritmo de Otsu
        int total = grayImage.rows() * grayImage.cols();
        float sumHist = 0;
        for (int t = 0; t < 256; t++) sumHist += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t];
            if (wB == 0) continue;

            wF = total - wB;
            if (wF == 0) break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB;
            float mF = (sumHist - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        // Aplicar a limiarização binária com o limiar encontrado por Otsu

        Mat outputImage = new Mat();

        Imgproc.threshold(grayImage, outputImage, (threshold-(averageBrightness*0.10)), 255, Imgproc.THRESH_BINARY);

        return outputImage;
    }


    /**
     * O método processImagePhone serve para tratar a imagem recebida do celular com reflexos e outras coisas diversas, e como resultado
     * retorna uma imagem so com a área de interesse do microscópio.
     *
     * @param image é a imagem,do tipo Mat, a ser trata no momento.
     * @return Retorna a imagem do tipo Mat tratada.
     */

    //futuramente para tentar tratar melhor, tentar pegar um canal do HSV, a magenta talvez de certo
    private static Mat processImagePhone(Mat image) {
        // Dividir a imagem nos canais de cores
        List<Mat> channels = new ArrayList<>();
        Core.split(image, channels);

        // Pegar o canal vermelho (índice 2)
        Mat redChannel = channels.get(2);

        // Calcular o brilho médio da imagem
        double sum = 0;
        int totalPixels = redChannel.rows() * redChannel.cols();
        for (int i = 0; i < redChannel.rows(); i++) {
            for (int j = 0; j < redChannel.cols(); j++) {
                sum += redChannel.get(i, j)[0];
            }
        }
        double averageBrightness = sum / totalPixels;

        // Ajustar o limiar baseado no brilho médio
        double thresholdValue = averageBrightness;

        // Aplicar a limiarização binária no canal vermelho com o novo limiar
        Mat binaryImage = new Mat();
        Imgproc.threshold(redChannel, binaryImage, thresholdValue, 255, Imgproc.THRESH_BINARY);

        // Aplicar a dilatação na imagem binarizada
        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)); // Define o tamanho do kernel
        Imgproc.dilate(binaryImage, dilatedImage, kernel);

        // Encontrar contornos
        MatOfPoint largestContour = new MatOfPoint();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;

        // Encontra o maior contorno
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        // Criar uma imagem preta com o mesmo tamanho da imagem original
        Mat mask = Mat.zeros(image.size(), CvType.CV_8UC1);

        // Desenhar o maior contorno na máscara com a cor branca
        Imgproc.drawContours(mask, Collections.singletonList(largestContour), -1, new Scalar(255), -1);

        // Pinta de branco na imagem original onde a máscara é branca
        Mat result = new Mat();
        image.copyTo(result, mask);

        // Criar uma imagem branca com o mesmo tamanho da imagem original
        Mat whiteImage = new Mat(image.size(), image.type(), new Scalar(255, 255, 255));

        // Pinta de branco a região correspondente
        Core.bitwise_not(mask, mask);  // Inverte a máscara
        whiteImage.copyTo(result, mask);

        // Desenhar apenas o contorno de branco na imagem original com espessura aumentada
        Imgproc.drawContours(result, Collections.singletonList(largestContour), -1, new Scalar(255, 255, 255), 15); // Espessura do contorno = 5

        // Encontrar o retângulo delimitador do maior contorno
        Rect boundingRect = Imgproc.boundingRect(largestContour);

        // Realizar o crop da imagem original usando o retângulo delimitador
        Mat croppedImage = new Mat(result, boundingRect);

        // Retornar a imagem cortada
        return croppedImage;
    }

}