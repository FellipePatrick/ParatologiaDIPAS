package org.example;

//para dar certo o projeto, tem que ter o opencv instalado no projeto

//passo 1 - baixar o opencv https://github.com/opencv/opencv/releases/download/4.9.0/opencv-4.9.0-windows.exe

//passo 2 - ir em nos Run\Debug\Configuration ou os 3 pontinhos do lado do run, no canto superior direto, clica em edit
// depois Modify option, add VM options e adiciona seupath\opencv\build\java

// passo 3 - Project Structure e adiciona o arquivo ponto jar (.jar) que ta em seupath\opencv\build\java

// passo 4 - É só aproveitar o código!




public class Main {
    public static void main(String[] args) {
        //Segmentando uma pasta de imagens
        Image.segmentImages("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img\\", "jpeg" , 36);


        //Segmentando apenas uma imagem especifica
        //Image.segmentImage("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img", 2);
    }
}
