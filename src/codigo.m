% Carrega a imagem
img = imread('C:\Users\felli\Repositorio GitHub\ParatologiaDIPAS\img\image1.jpeg');

% Converte a imagem para o espaço de cores HSV
img_hsv = rgb2hsv(img);

% Separa os canais HSV
h_channel = img_hsv(:,:,1); % Canal de matiz
s_channel = img_hsv(:,:,2); % Canal de saturação
v_channel = img_hsv(:,:,3); % Canal de valor

% Exibe os canais separados
figure;
subplot(1,3,1);
imshow(h_channel);
title('Canal de Matiz');
subplot(1,3,2);
imshow(s_channel);
title('Canal de Saturação');
subplot(1,3,3);
imshow(v_channel);
title('Canal de Valor');

