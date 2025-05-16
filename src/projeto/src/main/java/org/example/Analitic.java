package main.java.org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Analitic {

    // Método para seguir a fronteira do maior contorno
    public static MatOfPoint seguidorDeFronteira(Mat imagem) {
        List<MatOfPoint> contornos = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imagem, contornos, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        if (contornos.isEmpty()) {
            return null;
        }

        // Retornar o maior contorno
        return Collections.max(contornos, Comparator.comparingDouble(Imgproc::contourArea));
    }

    // Método para destacar a fronteira na imagem
    public static Mat destacarFronteira(Mat imagem, MatOfPoint contorno) {
        Mat imagemComFronteira = imagem.clone();
        List<MatOfPoint> listaContorno = Collections.singletonList(contorno);
        Imgproc.drawContours(imagemComFronteira, listaContorno, -1, new Scalar(0, 0, 255), 2); // Contorno em vermelho
        return imagemComFronteira;
    }

    // Método para gerar o código de cadeia (Chain Code) de um contorno
    public static List<Integer> gerarCodigoCadeia(MatOfPoint contorno) {
        List<Integer> codigo = new ArrayList<>();
        Point[] pontos = contorno.toArray();

        // Direções do código de Freeman (em sentido horário)
        for (int i = 0; i < pontos.length; i++) {
            Point atual = pontos[i];
            Point proximo = (i == pontos.length - 1) ? pontos[0] : pontos[i + 1];

            int dx = (int) (proximo.x - atual.x);
            int dy = (int) (proximo.y - atual.y);

            if (dx == 0 && dy < 0) {
                codigo.add(0); // Norte
            } else if (dx > 0 && dy < 0) {
                codigo.add(1); // Nordeste
            } else if (dx > 0 && dy == 0) {
                codigo.add(2); // Leste
            } else if (dx > 0 && dy > 0) {
                codigo.add(3); // Sudeste
            } else if (dx == 0 && dy > 0) {
                codigo.add(4); // Sul
            } else if (dx < 0 && dy > 0) {
                codigo.add(5); // Sudoeste
            } else if (dx < 0 && dy == 0) {
                codigo.add(6); // Oeste
            } else if (dx < 0 && dy < 0) {
                codigo.add(7); // Noroeste
            }
        }

        return codigo;
    }

    // Método para processar a imagem, encontrar o maior contorno, reamostrar e gerar o código de cadeia normalizado
    public static List<Integer> processarImagem(Mat imagem, int numPontos) {
        MatOfPoint contorno = seguidorDeFronteira(imagem);
        if (contorno == null) return new ArrayList<>();

        MatOfPoint contornoReamostrado = resample(contorno, numPontos); // Reamostragem do contorno
        List<Integer> codigoCadeia = gerarCodigoCadeia(contornoReamostrado); // Gerando código de cadeia
        return normalizarCodigoCadeia(codigoCadeia); // Normalizando o código de cadeia
    }

    // Método para normalizar o código de cadeia (invariável à rotação)
    public static List<Integer> normalizarCodigoCadeia(List<Integer> codigoCadeia) {
        List<Integer> minCodigo = new ArrayList<>(codigoCadeia);
        StringBuilder sb = new StringBuilder();
        for (int num : codigoCadeia) sb.append(num);
        int minValor = Integer.parseInt(sb.toString());

        for (int i = 0; i < codigoCadeia.size(); i++) {
            List<Integer> rotacionado = new ArrayList<>();
            rotacionado.addAll(codigoCadeia.subList(i, codigoCadeia.size()));
            rotacionado.addAll(codigoCadeia.subList(0, i));

            StringBuilder s = new StringBuilder();
            for (int num : rotacionado) s.append(num);
            int valorAtual = Integer.parseInt(s.toString());

            if (valorAtual < minValor) {
                minValor = valorAtual;
                minCodigo = rotacionado;
            }
        }

        return minCodigo; // Retorna o código de cadeia com a menor rotação
    }

    // Método para reamostrar o contorno para o número desejado de pontos
    public static MatOfPoint resample(MatOfPoint contorno, int numPontos) {
        List<Point> pontos = contorno.toList();
        double perimetro = 0;

        // Calcula o perímetro do contorno
        for (int i = 0; i < pontos.size(); i++) {
            Point atual = pontos.get(i);
            Point proximo = pontos.get((i + 1) % pontos.size());
            perimetro += Math.hypot(proximo.x - atual.x, proximo.y - atual.y);
        }

        double intervalo = perimetro / numPontos; // Distância entre pontos reamostrados
        List<Point> pontosReamostrados = new ArrayList<>();

        double distanciaAcumulada = 0;
        int i = 0;

        // Reamostragem do contorno
        for (int j = 0; j < numPontos; j++) {
            double distanciaAlvo = intervalo * j;

            while (true) {
                Point atual = pontos.get(i);
                Point proximo = pontos.get((i + 1) % pontos.size());
                double dx = proximo.x - atual.x;
                double dy = proximo.y - atual.y;
                double comprimentoSegmento = Math.hypot(dx, dy);

                if (distanciaAcumulada + comprimentoSegmento < distanciaAlvo) {
                    distanciaAcumulada += comprimentoSegmento;
                    i = (i + 1) % pontos.size();
                } else {
                    double t = (distanciaAlvo - distanciaAcumulada) / comprimentoSegmento;
                    double x = atual.x + t * dx;
                    double y = atual.y + t * dy;
                    pontosReamostrados.add(new Point(x, y));
                    break;
                }
            }
        }

        MatOfPoint resultado = new MatOfPoint();
        resultado.fromList(pontosReamostrados);
        return resultado; // Retorna o contorno reamostrado
    }
}
