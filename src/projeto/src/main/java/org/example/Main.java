package main.java.org.example;

//para dar certo o projeto, tem que ter o opencv instalado no projeto

//passo 1 - baixar o opencv https://github.com/opencv/opencv/releases/download/4.9.0/opencv-4.9.0-windows.exe

//passo 2 - ir em nos Run\Debug\Configuration ou os 3 pontinhos do lado do run, no canto superior direto, clica em edit
// depois Modify option, add VM options e adiciona seupath\opencv\build\java

// passo 3 - Project Structure e adiciona o arquivo ponto jar (.jar) que ta em seupath\opencv\build\java

// passo 4 - É só aproveitar o código!


import java.io.File;
import java.io.FilenameFilter;

public class Main {
    public static void main(String[] args) {


        //Segmentando uma pasta de imagens
        //org.example.Image.segmentImages("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img\\", "jpeg" , 107);

        //Segmentando apenas uma imagem especifica
        //Image.segmentImage("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img", 14, "não");
        // Diretório contendo as imagens
        String directoryPath = "C:\\Users\\vitin\\Documents\\Bolsa\\EAJ\\ParatologiaDIPAS\\img";
        Image.resetDiretorio(directoryPath+"\\result");

        // Filtro para selecionar apenas arquivos de imagem
        FilenameFilter imageFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg");
            }
        };

        // Listar todos os arquivos de imagem na pasta
        File directory = new File(directoryPath);
        File[] imageFiles = directory.listFiles(imageFilter);

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("Nenhuma imagem encontrada na pasta.");
            return;
        }

        // Processar cada imagem
        for (File imageFile : imageFiles) {
            String imageName = imageFile.getName();
            int indexDot = imageName.indexOf('e')+1;
            String numberImage = "";
            do{
                numberImage += imageName.charAt(indexDot);
                indexDot++;
            }while(imageName.charAt(indexDot) != '.');

            Image.segmentImage(directoryPath , Integer.parseInt(numberImage), "não");
            System.out.println(numberImage);
            System.out.println(imageFile.getAbsoluteFile());
        }
    }
}
