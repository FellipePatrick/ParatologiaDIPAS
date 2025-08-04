package com.api.sic.backend.service;


import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecisionTree {

    public Map<String, Object> diagnostico(Mat image) {
        Map<String, Object> resultado = new HashMap<>();

        // Converte para escala de cinza
        Mat gray = new Mat();
        if (image.channels() > 1) {
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = image.clone();
        }

        // Calcula média e desvio padrão
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(gray, mean, std);

        double meanVal = mean.toArray()[0];
        double stdVal = std.toArray()[0];

        // Calcula Skewness
        double skew = calcularSkewness(gray, meanVal, stdVal);

        // Calcula contraste
        double contrast = calcularGLCMContrast(gray);


        double minVal = Core.minMaxLoc(gray).minVal;
        double round = calcularRoundness(gray);
        double bx = calcularBX(gray);

        // Classificações
        boolean zoonose = classificarComArvoreZoonose(meanVal, stdVal, skew, contrast);
        int classeParasita = classificarComArvoreParasita(minVal, contrast, stdVal, round, bx);

        resultado.put("Zoonose", zoonose);
        resultado.put("Parasita", classeParasita == 1);
        resultado.put("ClasseParasita", classeParasita); //

        return resultado;
    }

    private double calcularSkewness(Mat gray, double mean, double std) {
        byte[] pixels = new byte[(int) (gray.total() * gray.channels())];
        gray.get(0, 0, pixels);
        double sum = 0.0;
        int n = pixels.length;

        for (int i = 0; i < n; i++) {
            double val = Byte.toUnsignedInt(pixels[i]);
            sum += Math.pow(val - mean, 3);
        }

        return sum / (n * Math.pow(std, 3) + 1e-8);
    }

    private double calcularGLCMContrast(Mat gray) {
        int rows = gray.rows();
        int cols = gray.cols();
        int[][] glcm = new int[256][256];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                int pixel = (int) gray.get(i, j)[0];
                int neighbor = (int) gray.get(i, j + 1)[0];
                glcm[pixel][neighbor]++;
            }
        }

        double total = Arrays.stream(glcm).flatMapToInt(Arrays::stream).sum();
        double contrast = 0.0;

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                double p = glcm[i][j] / total;
                contrast += p * Math.pow(i - j, 2);
            }
        }

        return contrast;
    }

    private boolean classificarComArvoreZoonose(double mean, double std, double skew, double contrast) {
        if (mean <= 139.28) {
            if (std <= 36.49) {
                return skew <= 1.36;
            } else {
                return false;
            }
        } else {
            if (skew <= 0.40) {
                return contrast <= 1269.72;
            } else {
                return false;
            }
        }
    }

    private int classificarComArvoreParasita(double min, double contrast, double stdDev, double round, double bx) {
        if (min <= 51.50) {
            if (contrast <= 353.36) {
                return 0;
            } else {
                if (round <= 0.38) {
                    return 0;
                } else {
                    return 1;
                }
            }
        } else {
            if (bx <= 1.50) {
                return 0;
            } else {
                if (bx <= 332.50) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
    }


    //estudar esses métodos depois
    private double calcularRoundness(Mat gray) {
        return 0.5;
    }

    private double calcularBX(Mat gray) {
        return 100.0;
    }
}
