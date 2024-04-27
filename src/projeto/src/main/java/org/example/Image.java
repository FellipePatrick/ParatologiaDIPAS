package org.example;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Image {
     /**
     * Classe abstrata Image, que fornece métodos para análise de imagens.
     *
     * @author Fellipe Patrick e Vitor Carvalho
     * @version 1.0
     * */

    /**
     * O método segmentImages é usado para ler um varias imagens e processa-las.
     *
     * @param path O caminho para o arquivo de imagem.
     * @return Retorna o array tridimensional de inteiros representando uma imagem colorida.
     */


    /**
     * O método segmentImages é usado para ler várias imagens e processá-las.
     *
     * @param path      O caminho para o diretório que contém os arquivos de imagem.
     * @param extension A extensão dos arquivos de imagem.
     * @param qtdImages A quantidade de imagens a serem processadas.
     */
    public static void segmentImages(String path, String extesion, int qtdImages){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for (int cont = 1; cont <= qtdImages; cont++) {
            // Carrega a imagem da vez
            Mat image = Imgcodecs.imread(path +"\\image" +cont +"."+ extesion);
            // Verifica se a imagem foi carregada corretamente
            if (image.empty()) {
                System.out.println("Erro ao carregar a imagem!");
                return;
            }
            // Dividir a imagem nos canais de cores
            List<Mat> channels;
            channels = new ArrayList<>();
            Core.split(image, channels);
            // Pegar o canal vermelho
            Mat redChannel = channels.get(2); // No OpenCV, os canais são BGR, portanto o canal vermelho é o terceiro canal (índice 2)

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

            //Desenha o contorno na imagem origial
            Imgproc.drawContours(image, Collections.singletonList(largestContour), -1, new Scalar(0, 255, 0), 2);

            // Salvar a imagem com os círculos detectados
            Imgcodecs.imwrite(path + "\\result\\se2g" + cont + ".jpeg", image);

//            // Redimensiona a máscara para o tamanho da imagem original
//            Mat resizedMask = new Mat();
//            Imgproc.resize(dilatedImage, resizedMask, image.size());
//
//            // Pinta de preto na imagem original onde a máscara é preta
//            Mat result = new Mat();
//            Core.bitwise_and(image, image, result, resizedMask);

        }
    }
}
