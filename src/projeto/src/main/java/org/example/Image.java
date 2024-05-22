package org.example;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
            segmentImage(path, cont);
        }
    }

    /**
     * O método segmentImage é usado para ler uma imagem e processá-la.
     *
     * @param path O caminho para o diretório que contém os arquivos de imagem.
     *
     */
    public static void segmentImage(String path, int cont){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Carrega a imagem
        Mat image = Imgcodecs.imread(path +"\\image" +cont +".jpeg");
        // Verifica se a imagem foi carregada corretamente
        if (image.empty()) {
            System.out.println("Erro ao carregar a imagem!");
            return;
        }
        //Recebe o resultado da imagem tratada da classe processImagePhone
        Mat result =  processImagePhone(image);

        Mat orig = result;

        // Salva a imagem
        Imgcodecs.imwrite(path + "\\result\\orig"+ cont + ".jpeg" ,result);

       result = ajustaBrilhoContraste(result);

       //Salva a imagem
        Imgcodecs.imwrite(path + "\\result\\alar"+ cont + ".jpeg" ,result);

        String outputPath = path+"\\result\\";
        Mat outputImage = findBlackRegion(orig, result, outputPath);


    }
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat findBlackRegion(Mat imageOriginal, Mat inputImage, String outputPath) {
        // Verificar se a imagem de entrada é vazia
        if (inputImage.empty()) {
            throw new IllegalArgumentException("A imagem de entrada está vazia");
        }

        Mat grayImage = inputImage;

        // Aplicar um limiar para binarizar a imagem (0 -> preto, 255 -> branco)
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 50, 255, Imgproc.THRESH_BINARY_INV);

        // Encontrar contornos na imagem binarizada
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Inicializar variáveis para encontrar o maior contorno (região preta)
        double maxArea = 0;
        Rect largestRect = new Rect();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                largestRect = rect;
            }
        }

        // Criar uma imagem de saída destacando a região com alta concentração de preto
        Mat outputImage = inputImage.clone();
        Imgproc.rectangle(outputImage, largestRect.tl(), largestRect.br(), new Scalar(0, 255, 0), 2);

        // Salvar a imagem de saída com a região destacada
        Imgcodecs.imwrite(outputPath + "black_region_highlighted" + ".jpeg", outputImage);

        // Cortar a região encontrada da imagem original
        Mat croppedImage = new Mat(imageOriginal, largestRect);

        // Salvar a imagem cortada
        Imgcodecs.imwrite(outputPath + "black_region_cropped" + ".jpeg", croppedImage);

        return croppedImage;
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

        // Criar a imagem de saída
        Mat outputImage = Mat.zeros(grayImage.size(), CvType.CV_8UC1);

        int atual = 170;
        double aux = 2.55;
        for (int x = 1; x <= 100; x++) {
            for (int i = 0; i < grayImage.rows(); i++) {
                for (int j = 0; j < grayImage.cols(); j++) {
                    if (grayImage.get(i, j)[0] == atual) {
                        outputImage.put(i, j, aux);
                    }
                }
            }
            aux += 5.1;
            atual += 1;
        }

        return outputImage;
    }


    public static Mat aplicaFiltroPassaAlta(Mat inputImage) {
        // Definir o kernel de passa alta
        Mat kernel = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1);
                put(0, 1, -1);
                put(0, 2, -1);
                put(1, 0, -1);
                put(1, 1,  8);
                put(1, 2, -1);
                put(2, 0, -1);
                put(2, 1, -1);
                put(2, 2, -1);
            }
        };

        // Criar a imagem de saída
        Mat outputImage = new Mat();

        // Aplicar a convolução com o kernel de passa alta
        Imgproc.filter2D(inputImage, outputImage, -1, kernel);

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

        // Pegar o canal vermelho
        // No OpenCV, os canais são BGR, portanto o canal vermelho é o terceiro canal (índice 2)
        Mat redChannel = channels.get(2);

        // Aplicar a limiarização binária no canal vermelho
        Mat binaryImage = new Mat();
        Imgproc.threshold(redChannel, binaryImage, 127, 255, Imgproc.THRESH_BINARY);

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
