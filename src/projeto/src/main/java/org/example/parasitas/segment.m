clear all;
close all;
pkg load image;
im = imread("ancy1.jpeg");
low_in = double(min(im(:))) / 255;  % Converter valores de 0-255 para 0-1
high_in = double(max(im(:))) / 255;

##adjusted_img = imadjust(im, [low_in, high_in], [0, 1]);
##
##mask = zeros(size(adjusted_img,1), size(adjusted_img,2))
##
##for i=1: size(adjusted_img,1)
##  for j=1 : size(adjusted_img,2)
##      if adjusted_img(i,j,1) < 150
##        mask(i,j) = 255;
##      else
##        mask(i,j) = 0;
##      endif
##    endfor
##  endfor

##figure("name", "mask")
##imshow(mask)

figure("name", "im")
imshow(im)

figure("name", "R")
imhist(im(:,:,1))

figure("name", "G")
imhist(im(:,:,2))

figure("name", "B ")
imhist(im(:,:,3))
