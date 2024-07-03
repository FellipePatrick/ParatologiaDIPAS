import cv2 as cv
import matplotlib.pyplot as plt
import os
import numpy as np
files = os.listdir()
    
imgs:list = []
for file in files:
    if 'jpeg' in file:
        imgs.append(cv.imread(file))
count = 1
plt.figure("Operacoes")
for im in imgs:
    im = cv.cvtColor(im, cv.COLOR_BGR2RGB)
    orig = im.copy()
    # im = cv.bilateralFilter(im, 10, 40, 0)
    im = cv.medianBlur(im, 11)
    im = cv.GaussianBlur(im, (3,3),0)
   
    pixel_values = im.reshape((-1, 3))
    pixel_values = np.float32(pixel_values)  
    criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 100, 0.2)
    K = 6
    _, labels, centers = cv.kmeans(pixel_values, K, None, criteria, 10, cv.KMEANS_RANDOM_CENTERS)
    centers = np.uint8(centers)  
    segmented_image = centers[labels.flatten()]  
    segmented_image = segmented_image.reshape(im.shape) 
   
    gray_mask = cv.cvtColor(segmented_image, cv.COLOR_BGR2GRAY)
    plt.subplot(len(imgs), 3, count)
    plt.imshow(gray_mask)
    count+=1

    
    _, maskNucleo = cv.threshold(gray_mask, 0, 255, cv.THRESH_BINARY + cv.THRESH_OTSU)
    maskNucleo = cv.bitwise_not(maskNucleo)
    maskNucleo = cv.morphologyEx(maskNucleo, cv.MORPH_DILATE, cv.getStructuringElement(cv.MORPH_ELLIPSE, (10,10)))
    image = cv.bitwise_and(orig, orig, mask=maskNucleo)
    plt.subplot(len(imgs), 3, count)
    plt.imshow(image)
    count+=1

    # gray = cv.cvtColor(orig, cv.COLOR_BGR2GRAY)
    # edges = cv.Canny(gray, 50, 150)
    kernel = cv.getStructuringElement(cv.MORPH_ELLIPSE, (5, 5))
    # Encontrar áreas seguras (fundo e objetos)
    sure_bg = cv.dilate(maskNucleo, kernel, iterations=2)
    dist_transform = cv.distanceTransform(maskNucleo, cv.DIST_L2, 5)
    ret, sure_fg = cv.threshold(dist_transform, 0.7*dist_transform.max(), 255, 0)

    # Encontrar área desconhecida (bordas)
    sure_fg = np.uint8(sure_fg)
    unknown = cv.subtract(sure_bg, sure_fg)

    # Marcar os marcadores
    ret, markers = cv.connectedComponents(maskNucleo)
    markers = markers+1
    markers[unknown==255] = 0

    # Aplicar o Watershed
    markers = cv.watershed(im, markers)
    # orig[markers == -1] = [255,0,0]
    markers[markers == -1] = 0
    markers[markers == 1] = 0

    mask_final = cv.bitwise_and(maskNucleo, markers.astype(np.uint8))
    im_floodfill = mask_final.copy()
 
    # Mask used to flood filling.
    # Notice the size needs to be 2 pixels than the image.
    h, w = mask_final.shape[:2]
    mask = np.zeros((h+2, w+2), np.uint8)
    
    # Floodfill from point (0, 0)
    cv.floodFill(im_floodfill, mask, (0,0), 255)
    
    im_floodfill_inv = cv.bitwise_not(im_floodfill)
    im_out = mask_final | im_floodfill_inv
    segment = cv.bitwise_and(orig, orig, mask=im_out)
    
    plt.subplot(len(imgs), 3, count)
    plt.imshow(segment)
    count+=1

    # histogram = cv.calcHist([np.uint8(markers)], [0], None, [256], [0, 256])
    # plt.figure(figsize=(10, 4))
    # print(histogram)
    # plt.plot(histogram, color='gray')
    # plt.title('Histograma da Imagem em Tons de Cinza')
    # plt.xlabel('Intensidade de Pixel')
    # plt.ylabel('Quantidade de Pixels')
    # plt.xlim([0, 256])
    # plt.show()

plt.show()