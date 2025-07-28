package main.java.org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Remove {
    public static void Remove(String imagemPath){
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "remove.py", imagemPath);

            Process process = pb.start();

            // Lê a saída padrão (stdout)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }

            // Lê a saída de erro (stderr)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((linha = errorReader.readLine()) != null) {
                System.err.println(linha);
            }

            int exitCode = process.waitFor();
            System.out.println("Processo finalizado com código: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}