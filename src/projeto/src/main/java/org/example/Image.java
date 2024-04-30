package org.example;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            // Salvar a imagem com os círculos detectados
            Imgcodecs.imwrite(path + "\\result\\result" + cont + ".jpeg", processImagePhone(image));

        }
    }

    /**
     * O método segmentImage é usado para ler uma imagem e processá-la.
     *
     * @param path O caminho para o diretório que contém os arquivos de imagem.
     *
     * @param imageName É o nome da imagem.
     */
    public static void segmentImage(String path, String imageName){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Carrega a imagem
        Mat image = Imgcodecs.imread(path + imageName);
        // Verifica se a imagem foi carregada corretamente
        if (image.empty()) {
            System.out.println("Erro ao carregar a imagem!");
            return;
        }
        //Recebe o resultado da imagem tratada da classe processImagePhone
        Mat result =  processImagePhone(image);

        ArrayList<Mat> lista = findObjects(result, path);

        // Salva a imagem
        Imgcodecs.imwrite(path + "\\result\\result.jpeg" ,result);
    }

    private static ArrayList<Mat> findObjects(Mat result, String path) {
        ArrayList<Mat> lista = new ArrayList<>();
        // Converte a imagem para escala de cinza
        Mat grayImage = new Mat();
        Imgproc.cvtColor(result, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Exemplo de valores de threshold (ajuste conforme necessário)
        int threshold1 = 50;
        int threshold2 = 200;

        // Aplica o detector de bordas Canny
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, threshold1, threshold2);

        // Lista para armazenar os contornos filtrados
        List<MatOfPoint> filteredContours = new ArrayList<>();

        // Define a área mínima desejada para considerar um contorno
        double minArea = 50; // Ajuste conforme necessário

        // Encontrar contornos
        MatOfPoint largestContour = new MatOfPoint();
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

        for (int i = 0; i < filteredContours.size(); i++) {
            // Cria uma máscara para o contorno atual
            Mat mask = Mat.zeros(edges.size(), CvType.CV_8UC1);
            Imgproc.drawContours(mask, filteredContours, i, new Scalar(255), -1);

            // Cria uma imagem temporária com o mesmo tamanho da imagem original
            Mat temp = new Mat(result.size(), result.type(), Scalar.all(0)); // Preenche a imagem com preto

            // Aplica a máscara na imagem original
            result.copyTo(temp, mask);

            // Salva a imagem resultante
            Imgcodecs.imwrite(path + "\\result\\object_" + i + ".jpeg", temp);
        }

        return lista;
    }

    /**
     * O método processImagePhone serve para tratar a imagem recebida do celular com reflexos e outras coisas diversas, e como resultado
     * retorna uma imagem so com a área de interesse do microscópio.
     *
     * @param image é a imagem a ser trata no momento, do tipo Mat.
     * @return Retorna a imagem do tipo Mat tratada.
     */

    //futuramente para tentar tratar melhor, tentar pegar um canal do HSV, a magenta talvez de certo
    private static Mat processImagePhone(Mat image){
        // Dividir a imagem nos canais de cores
        List<Mat> channels;
        channels = new ArrayList<>();
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

        //Encontra o maior contorno
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        // Criar uma imagem preta com o mesmo tamanho da imagem original
        Mat mask = Mat.zeros(image.size(), CvType.CV_8UC1);

        // Desenhar o maior contorno (círculo) na máscara com a cor branca
        Imgproc.drawContours(mask, Collections.singletonList(largestContour), -1, new Scalar(255), -1);

        // Pinta de preto na imagem original onde a máscara é preta
        Mat result = new Mat();
        Core.bitwise_and(image, image, result, mask);

        // Retorna o resultado
        return result;
    }

}
