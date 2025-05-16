package main.java.org.example;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * @param zoom É o parametro que define se a imagem está ou não usando o zoom.
     *
     */


    public static void segmentImages(String path, String extension, int qtdImages, String zoom){
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
    public static void segmentImage(String path, int cont, String zoom){
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
       if(zoom.equalsIgnoreCase("sim")){
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

           List<Mat> outputImage = findBlackRegion(orig, result, outputPath, cont, zoom);
       }

    }
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
    public static List<Mat> findBlackRegion(Mat imageOriginal, Mat inputImage, String outputPath, int cont, String zoom) {
        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        Mat grayImage = inputImage;
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 50, 255, Imgproc.THRESH_BINARY_INV);

        Imgproc.GaussianBlur(binaryImage, binaryImage, new Size(5, 5), 0);

        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(binaryImage, dilatedImage, kernel);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Mat> croppedImages = new ArrayList<>();
        int x = 5;
        double area;
        double perimeter;
        double circularity;
        double maxObject = 500;

        if (zoom.equalsIgnoreCase("sim")) {
            maxObject = imageOriginal.rows() * 5;
        } else {
            if (imageOriginal.cols() > 1280) {
                maxObject = 2500;
            }
        }

        Mat largestContourImage = new Mat(imageOriginal.size(), imageOriginal.type(), new Scalar(0, 0, 0));

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            area = rect.area();

            if (area >= maxObject) {
                perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                circularity = 4 * Math.PI * area / (perimeter * perimeter);

                if (circularity > 0.2) {
                    Mat mask = Mat.zeros(imageOriginal.size(), CvType.CV_8UC1);
                    Imgproc.drawContours(mask, contours, contours.indexOf(contour), new Scalar(255), -1);

                    Mat foreground = new Mat();
                    imageOriginal.copyTo(foreground, mask);
                    Mat croppedImage = new Mat(foreground, rect);
                    croppedImages.add(croppedImage);

                    String nomeBase = "blackimgcurted" + x + cont;
                    //Imgcodecs.imwrite(outputPath + nomeBase + ".jpeg", croppedImage);

                    // >>>>>> NOVO PROCESSAMENTO <<<<<<
                    try {
                        // 1. Converter para escala de cinza
                        Mat gray = new Mat();
                        Imgproc.cvtColor(croppedImage, gray, Imgproc.COLOR_BGR2GRAY);

                        // 2. Suavização
                        Mat blur = new Mat();
                        Imgproc.GaussianBlur(gray, blur, new Size(5, 5), 0);

                        // 3. Contorno do maior objeto na imagem recortada
                        List<MatOfPoint> subContours = new ArrayList<>();
                        Mat subHierarchy = new Mat();
                        Imgproc.findContours(blur, subContours, subHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                        if (!subContours.isEmpty()) {
                            MatOfPoint maior = Collections.max(subContours, Comparator.comparingDouble(Imgproc::contourArea));

                            // 4. Reamostrar para 16 pontos
                            MatOfPoint reamostrado = Analitic.resample(maior, 16);

                            // 5. Destacar a fronteira
                            Mat destaque = Analitic.destacarFronteira(croppedImage.clone(), reamostrado);

                            // 6. Salvar imagem com destaque
                            Imgcodecs.imwrite(outputPath + nomeBase + "_destaque.jpeg", destaque);

                            // 7. Codigo de Cadeia
                            List<Integer> codigoCadeia = Analitic.gerarCodigoCadeia(reamostrado);
                            System.out.println("Código de Cadeia para o contorno " + nomeBase + ": " + codigoCadeia);
                        }

                    } catch (Exception e) {
                        System.err.println("Erro ao processar recorte " + nomeBase + ": " + e.getMessage());
                    }

                    cont++;
                }
            }
        }

        Imgcodecs.imwrite(outputPath + "largestObject" + x + cont + ".jpeg", largestContourImage);
        return croppedImages;
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

// 1. Suavização antes da binarização
        Imgproc.medianBlur(grayImage, grayImage, 5);
// 2. Aplicar a limiarização binária com o limiar de Otsu ajustado
        Mat outputImage = new Mat();
        Imgproc.threshold(grayImage, outputImage, (threshold - (averageBrightness * 0.10)), 255, Imgproc.THRESH_BINARY);

// 3. Operações morfológicas
// 3.1. Abertura para remover pequenos ruídos brancos
        Mat kernelOpen = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(outputImage, outputImage, Imgproc.MORPH_OPEN, kernelOpen);

// 3.2. Fechamento para preencher pequenos buracos em objetos brancos
        Mat kernelClose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15));
        Imgproc.morphologyEx(outputImage, outputImage, Imgproc.MORPH_CLOSE, kernelClose);

// 4. Remover contornos pequenos (ruídos brancos restantes)
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(outputImage.clone(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area < 100) { // ajuste conforme necessário
                Imgproc.drawContours(outputImage, contours, i, new Scalar(0), -1); // apaga ruídos brancos
            }
        }

// 5. Inverter imagem para tratar os pontinhos pretos restantes
        Mat inverted = new Mat();
        Core.bitwise_not(outputImage, inverted);

// 6. Remover contornos pequenos (agora brancos, mas originalmente pretos)
        List<MatOfPoint> holeContours = new ArrayList<>();
        Mat hierarchyHoles = new Mat();
        Imgproc.findContours(inverted.clone(), holeContours, hierarchyHoles, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < holeContours.size(); i++) {
            double area = Imgproc.contourArea(holeContours.get(i));
            if (area < 700) { // ajuste esse valor também
                Imgproc.drawContours(inverted, holeContours, i, new Scalar(0), -1); // apaga os pequenos buracos pretos
            }
        }


// 7. Inverter de volta para obter a imagem final
        Core.bitwise_not(inverted, outputImage);

// Kernel pequeno para remover ruídos isolados restantes
        Mat smallKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));

// Outra rodada de abertura e fechamento mais delicada
        Imgproc.morphologyEx(outputImage, outputImage, Imgproc.MORPH_OPEN, smallKernel);
        Imgproc.morphologyEx(outputImage, outputImage, Imgproc.MORPH_CLOSE, smallKernel);

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