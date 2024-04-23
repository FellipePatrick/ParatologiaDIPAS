clear all
close all

% Carrega a imagem
img = imread('C:\Users\felli\Pictures\ParatologiaDIPAS\img\image18.jpeg');


mask = imread('C:\Users\felli\Pictures\ParatologiaDIPAS\img\png\mask18.jpg');

% Canal vermelho da imagem
im_red = img(:,:,1);

% Limiar para segmentação
limiar = 127;

% Segmentação da imagem
im_seg = mask >= limiar;

% Multiplicação da imagem original pelos resultados da segmentação
imf = img .* uint8(im_seg);

% Exibição dos resultados
figure(1);
subplot(1,2,1);
imshow(img);
title('Imagem Original');

subplot(1,2,2);
imshow(imf);
title('Imagem Segmentada');


