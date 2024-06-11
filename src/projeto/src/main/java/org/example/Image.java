package main.java.org.example;
import org.opencv.core.*;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.Size;

public class Image {
    /**
     * Classe Image, que fornece métodos para análise de imagens.
     *
     * @author Fellipe Patrick e Vitor Carvalho
     * @version 1.0
     * */

    /**
     * O método segmentImages é usado para ler várias imagens e processá-las.
     *
     * @param path      O caminho para o diretório que contém os arquivos de imagem.
     * @param extension A extensão dos arquivos de imagem.
     * @param qtdImages A quantidade de imagens a serem processadas.
     */
    public static void segmentImages(String path, String extension, int qtdImages){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for (int cont = 1; cont <= qtdImages; cont++) {
            // Carrega a imagem da vez
            Mat image = Imgcodecs.imread(path +"\\image" +cont +"."+ extension);
            // Verifica se a imagem foi carregada corretamente
            if (image.empty()) {
                System.out.println("Erro ao carregar a imagem!");
                return;
            }
            segmentImage(path, cont, "não");
        }
    }

    /**
     * O método segmentImage é usado para ler uma imagem e processá-la.
     *
     * @param path O caminho para o diretório que contém os arquivos de imagem.
     *
     */
    public static void segmentImage(String path, int cont, String zoom){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Carrega a imagem
        Mat image = Imgcodecs.imread(path +"\\image" +cont +".jpeg");
        // Verifica se a imagem foi carregada corretamente
        if (image.empty()) {
            System.out.println("Erro ao carregar a imagem!");
            return;
        }
        Mat result;

        Mat orig;
       if(zoom.equalsIgnoreCase("sim")){

           result = ajustaBrilhoContrasteZoom(image);
           //Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\orig"+ cont + ".jpeg" ,result);
           String outputPath = path+"\\result\\";

           List<Mat> outputImage = findBlackRegion(image, result, outputPath, cont);

       }else{
           //Recebe o resultado da imagem tratada da classe processImagePhone
           result =  processImagePhone(image);

           orig = result;

           // Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\orig"+ cont + ".jpeg" ,result);

           result = ajustaBrilhoContraste(result);

           //Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\alar"+ cont + ".jpeg" ,result);

           String outputPath = path+"\\result\\";

           List<Mat> outputImage = findBlackRegion(orig, result, outputPath, cont);

           //findObjects(result, path, orig, cont);
       }

    }
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static void findObjects(Mat result, String path, Mat image, int cont) {
        // Converte a imagem para escala de cinza se não estiver em escala de cinza
        Mat grayImage = new Mat();
        if (result.channels() > 1) {
            Imgproc.cvtColor(result, grayImage, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayImage = result;
        }

        // Aplica o detector de bordas Canny
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, 200, 250);

        // Lista para armazenar os contornos filtrados
        List<MatOfPoint> filteredContours = new ArrayList<>();

        // Define a área mínima desejada para considerar um contorno
        double minArea = 200; // Ajuste conforme necessário

        // Encontrar contornos
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filtra os contornos com área menor que minArea
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > minArea) {
                filteredContours.add(contour);
            }
        }

        // Desenha os contornos filtrados na imagem original
        for (MatOfPoint contour : filteredContours) {
            Imgproc.drawContours(image, Collections.singletonList(contour), -1, new Scalar(0, 255, 0), 2);
        }

        // Salvar a imagem com os contornos detectados
        Imgcodecs.imwrite(path + "\\result\\contours" +cont+".jpeg", image);

        // Processa e salva cada contorno individualmente
        for (int i = 0; i < filteredContours.size(); i++) {
            Mat mask = Mat.zeros(edges.size(), CvType.CV_8UC1);
            Imgproc.drawContours(mask, filteredContours, i, new Scalar(255), -1);

            Mat temp = new Mat(result.size(), result.type(), Scalar.all(0)); // Preenche a imagem com preto
            result.copyTo(temp, mask);

            // Salva a imagem resultante
            //Imgcodecs.imwrite(path + "\\result\\object_" + i + ".jpeg", temp);
        }
    }

    public static List<Mat> findBlackRegion(Mat imageOriginal, Mat inputImage, String outputPath, int cont) {
        // Verificar se a imagem de entrada é vazia
        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        Mat grayImage = inputImage;

        // Aplicar um limiar para binarizar a imagem (50 -> preto, 255 -> branco)
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 50, 255, Imgproc.THRESH_BINARY_INV);

        // Encontrar contornos na imagem binarizada
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Lista para armazenar as regiões cortadas
        List<Mat> croppedImages = new ArrayList<>();
        int x = 5;

        // Processar cada contorno que atenda aos critérios de área e circularidade
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area >= 250) {
                // Calcular a circularidade
                double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                double circularity = 4 * Math.PI * area / (perimeter * perimeter);
                if(circularity > 0.2){
                    // Criar uma imagem de saída destacando a região com alta concentração de preto
                    Mat outputImage = inputImage.clone();
                    Imgproc.rectangle(outputImage, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                    // Salvar a imagem de saída com a região destacada
                    Imgcodecs.imwrite(outputPath + "blackimg" + x + cont + ".jpeg", outputImage);

                    // Cortar a região encontrada da imagem original
                    Mat croppedImage = new Mat(imageOriginal, rect);
                    croppedImages.add(croppedImage);

                    // Salvar a imagem cortada (opcional)
                    //Imgcodecs.imwrite(outputPath + "blackimgcurted" + x + cont + ".jpeg", croppedImage);

                    cont++;
                }
            }
        }

        return croppedImages;
    }

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
        double sum = 0;
        int totalPixels = 0;
        for (int i = 0; i < grayImage.rows(); i++) {
            for (int j = 0; j < grayImage.cols(); j++) {
                if (mask.get(i, j)[0] == 255) {
                    sum += grayImage.get(i, j)[0];
                    totalPixels++;
                }
            }
        }
        double averageBrightness = sum / totalPixels;
        System.out.println("Brilho médio: " + averageBrightness);

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

        int aux = 40;
        if(averageBrightness < 160)
            aux = 100;
        else if(averageBrightness > 160 && averageBrightness < 200)
            aux = 60;

        Mat outputImage = new Mat();
        Imgproc.threshold(grayImage, outputImage, (threshold - aux), 255, Imgproc.THRESH_BINARY);

        return outputImage;
    }

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

        // Calcular o brilho médio da imagem dentro do maior contorno
        double sum = 0;
        int totalPixels = 0;
        for (int i = 0; i < grayImage.rows(); i++) {
            for (int j = 0; j < grayImage.cols(); j++) {
                if (grayImage.get(i, j)[0] == 255) {
                    sum += grayImage.get(i, j)[0];
                    totalPixels++;
                }
            }
        }
        double averageBrightness = sum / totalPixels;
        System.out.println("Brilho médio: " + averageBrightness);

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

        int aux = 30;

        Mat outputImage = new Mat();

        Imgproc.threshold(grayImage, outputImage, (threshold), 255, Imgproc.THRESH_BINARY);

        return outputImage;
    }


    /**
     * O método processImagePhone serve para tratar a imagem recebida do celular com reflexos e outras coisas diversas, e como resultado
     * retorna uma imagem so com a área de interesse do microscópio.
     *
     * @param image é a imagem a ser trata no momento, do tipo Mat.
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

    public static void resetDiretorio(String path){
        // Substitua "seu/diretorio/caminho" pelo caminho do seu diretório
        String directoryPath = path;
        File directory = new File(directoryPath);

        // Verifica se o caminho é um diretório
        if (!directory.isDirectory()) {
            System.out.println("O caminho fornecido não é um diretório.");
            return;
        }

        // Cria um filtro para listar apenas os arquivos .jpeg
        FilenameFilter jpegFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpeg");
            }
        };

        // Lista todos os arquivos .jpeg no diretório
        File[] jpegFiles = directory.listFiles(jpegFilter);

        // Verifica se há arquivos .jpeg no diretório
        if (jpegFiles == null || jpegFiles.length == 0) {
            System.out.println("Nenhum arquivo .jpeg encontrado no diretório.");
            return;
        }

        // Apaga todos os arquivos .jpeg
        for (File jpegFile : jpegFiles) {
            if (jpegFile.delete()) {
                //System.out.println("Arquivo " + jpegFile.getName() + " foi apagado com sucesso.");
            } else {
                System.out.println("Falha ao apagar o arquivo " + jpegFile.getName() + ".");
            }
        }

    }

}