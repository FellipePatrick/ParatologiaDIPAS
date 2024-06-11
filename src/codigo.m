% Carrega a imagem
img = imread('C:\Users\felli\Repositorio GitHub\ParatologiaDIPAS\img\image16.jpeg');

% Carrega a imagem
img2 = imread('C:\Users\felli\Repositorio GitHub\ParatologiaDIPAS\img\image104.jpeg');

% Converte a imagem para o espaço de cores HSV
img1_hsv = rgb2hsv(img);

v1_channel = img_hsv(:,:,3); % Canal de valor

% Converte a imagem para o espaço de cores HSV
img2_hsv = rgb2hsv(img2);

v2_channel = img2_hsv(:,:,3); % Canal de valor

% Carrega o pacote de imagem
pkg load image


% Exibe os canais separados
figure;
subplot(1, 2, 1);
imshow(v1_channel);
title('Canal de Valor Original');

subplot(1, 2, 2);
imshow(v2_channel);
title('Canal de Valor Original');

