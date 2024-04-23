package org.example;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for (int cont = 1; cont < 35; cont++) {
            // Carrega a imagem
            Mat image = Imgcodecs.imread("C:\\Users\\felli\\Pictures\\ParatologiaDIPAS\\img\\image" + cont + ".jpeg");
            // Verifica se a imagem foi carregada corretamente
            if (image.empty()) {
                System.out.println("Erro ao carregar a imagem!");
                return;
            }
            // Converte a imagem para escala de cinza
            Mat gray = new Mat();
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
            // Aplica um suavização para reduzir o ruído
            Imgproc.GaussianBlur(gray, gray, new org.opencv.core.Size(9, 9), 2, 2);
            Mat binaryImage = new Mat();
            Imgproc.threshold(gray, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            // Detecta círculos usando a Transformada de Hough
            Mat circles = new Mat();
            Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) gray.rows() / 8);
            // Encontra o maior círculo
            double[] largestCircle = null;
            double maxRadius = 0;
            for (int i = 0; i < circles.cols(); i++) {
                double[] circle = circles.get(0, i);
                if (circle != null && circle.length > 2) {
                    double radius = circle[2];
                    if (radius > maxRadius) {
                        maxRadius = radius;
                        largestCircle = circle;
                    }
                }
            }

            // Cria uma máscara onde o maior círculo destacado é branco e o resto é preto
            Mat mask = new Mat(binaryImage.rows(), binaryImage.cols(), binaryImage.type(), new org.opencv.core.Scalar(0));
            if (largestCircle != null) {
                org.opencv.core.Point center = new org.opencv.core.Point(Math.round(largestCircle[0]), Math.round(largestCircle[1]));
                int radius = (int) Math.round(largestCircle[2]);
                // Desenha o maior círculo na máscara
                Imgproc.circle(mask, center, radius, new org.opencv.core.Scalar(255), -1); // -1 preenche o círculo
                // Redimensiona a máscara para o tamanho da imagem original
                Mat resizedMask = new Mat();
                Imgproc.resize(mask, resizedMask, image.size());
                // Pinta de preto na imagem original onde a máscara é preta
                Mat result = new Mat();
                Core.bitwise_and(image, image, result, resizedMask);
                Imgcodecs.imwrite("C:\\Users\\felli\\Pictures\\ParatologiaDIPAS\\img\\png\\masked_image" + cont + ".jpg", result);
            }else{
                System.out.println("Imagem " + cont + " fora dos padrões!");
            }
        }
    }
}
