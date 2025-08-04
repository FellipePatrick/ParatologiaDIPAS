package main.java.org.example;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

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
     * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
     *
     */


    public static void segmentImages(String path, String extension, int qtdImages, boolean zoom, boolean machineLearnig){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for (int cont = 1; cont <= qtdImages; cont++) {
            // Carrega a imagem da vez
            Mat image = Imgcodecs.imread(path +"\\image" +cont +"."+ extension);
            // Verifica se a imagem foi carregada corretamente
            if (image.empty()) {
                System.out.println("Erro ao carregar a imagem!");
                return;
            }
            segmentImage(path, cont, zoom);
        }
    }


    /**
     * O método segmentImage é usado para ler uma imagem e processá-la.
     *
     * @param path O caminho para o diretório que contém os arquivos de imagem.
     * @param cont É a numeração da imagem da vez.
     * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
     *
     */
    public static void segmentImage(String path, int cont, boolean zoom){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Carrega a imagem
        Mat image = Imgcodecs.imread(path +"\\image" + cont + ".jpeg");
        // Obter e imprimir as dimensões da imagem

        // Verifica se a imagem foi carregada corretamente
        if (image.empty()) {
            System.out.println("Erro ao carregar a imagem!");
            return;
        }
        Mat result;
        Mat orig;
       if(zoom){
           result = ajustaBrilhoContrasteZoom(image);
           //Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\orig"+ cont + ".jpeg" ,result);
           String outputPath = path+"\\result\\";

           List<Mat> outputImage = findBlackRegion(image, result, outputPath, cont, zoom);

       }
       else{
           //Recebe o resultado da imagem tratada da classe processImagePhone
           result =  processImagePhone(image);

           orig = result;

           // Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\orig"+ cont + ".jpeg" ,result);

           result = ajustaBrilhoContraste(result);

           //Salva a imagem
           Imgcodecs.imwrite(path + "\\result\\alar"+ cont + ".jpeg" ,result);

           String outputPath = path+"\\result\\";

           List<Mat> outputImage = findBlackRegion(image, result, outputPath, cont, zoom);

       }

    }
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }




    public static List<Mat> findBlackRegion(Mat imageOriginal, Mat inputImage, String outputPath, int cont, boolean zoom) {
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
        double maxObject = 500;

        if(zoom){
            maxObject = imageOriginal.rows()*5;
        }else{
            if(imageOriginal.cols() > 1280){
                maxObject = 2500;
            }
        }

        TreeDecision td = new TreeDecision();

        // Processar cada contorno que atenda aos critérios de área e circularidade
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area >= maxObject) {
                // Calcular a circularidade
                double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                double circularity = 4 * Math.PI * area / (perimeter * perimeter);

                if(circularity > 0.2){
                    // Criar máscara apenas para este contorno (com mesmo tamanho da imagem original)
                    Mat mask = Mat.zeros(imageOriginal.size(), CvType.CV_8UC1);
                    Imgproc.drawContours(mask, Arrays.asList(contour), -1, new Scalar(255), Imgproc.FILLED);

                    // Aplicar a máscara na imagem original
                    Mat maskedImage = new Mat(imageOriginal.size(), imageOriginal.type(), new Scalar(0));
                    imageOriginal.copyTo(maskedImage, mask);

                    // Cortar apenas a região do retângulo delimitador
                    Mat croppedImage = new Mat(maskedImage, rect);
                    croppedImages.add(croppedImage);

                    // Salvar a imagem cortada se for parasita

                    Map<String, Object> resultado = td.diagnostico(croppedImage);

                    String forma = verificarForma(croppedImage);

                    if ("redondo".equals(forma) || "oval".equals(forma)) {
                         td.diagnostico(croppedImage);

                         if((boolean) resultado.get("Zoonose") || (boolean) resultado.get("Parasita")){

                             if((boolean) resultado.get("Parasita"))
                                Imgcodecs.imwrite(outputPath + "parasita" + x + cont + ".jpeg", croppedImage);

                            if((boolean) resultado.get("Zoonose"))
                                Imgcodecs.imwrite(outputPath + "zoonose" + x + cont + ".jpeg", croppedImage);
                         }else
                            Imgcodecs.imwrite(outputPath + "oval" + x + cont + ".jpeg", croppedImage);
                    }

                    Imgcodecs.imwrite(outputPath + "blackimgcurted" + x + cont + ".jpeg", croppedImage);

                    cont++;
                }
            }
        }

        return croppedImages;
    }

    private static String verificarForma(Mat imagem) {
        Mat gray = new Mat();
        Imgproc.cvtColor(imagem, gray, Imgproc.COLOR_BGR2GRAY);

        Mat bin = new Mat();
        Imgproc.threshold(gray, bin, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        List<MatOfPoint> contornos = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bin, contornos, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contornos.isEmpty()) {
            return "indefinido";
        }

        for (MatOfPoint contorno : contornos) {
            double area = Imgproc.contourArea(contorno);
            double perimetro = Imgproc.arcLength(new MatOfPoint2f(contorno.toArray()), true);

            if (perimetro == 0) continue;

            double circularidade = 4 * Math.PI * area / (perimetro * perimetro);

            if (circularidade > 0.4) {
                return "redondo";
            } else if (circularidade > 0.65) {
                return "oval";
            }
        }

        return "irregular";
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
        System.out.println("Brilho médio: "+averageBrightness);

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


    /**
     * O método resetDiretorio é usado para limpar as imagens de um diretorio.
     *
     * @param path É o path do diretorio para ser limpo.
     *
     */
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