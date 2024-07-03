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

def segmentPostKmean(img, colors):
    im = img.copy()
    data = im / 255.0

    # Redimensionar a imagem para uma matriz 2D
    data = data.reshape((-1, 3))
    data = np.float32(data)

    # Definir critérios de parada e número de clusters 
    criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 100, 0.2)
    # Aplicar K-means para posterizar a imagem
    _, labels, centers = cv.kmeans(data, colors, None, criteria, 11, cv.KMEANS_RANDOM_CENTERS)

    # Converter os centros para inteiros
    centers = np.uint8(centers * 255)

    # Mapear os labels de volta para a imagem original
    posterized_image = centers[labels.flatten()]
    posterized_image = posterized_image.reshape(im.shape)

    return posterized_image.copy()

def segmentWatershed(mask,img):
    im = img.copy()
    maskParasite = mask.copy()
    kernel = cv.getStructuringElement(cv.MORPH_ELLIPSE, (5, 5))
    # Encontrar áreas seguras (fundo e objetos)
    sure_bg = cv.dilate(maskParasite, kernel, iterations=6)
    dist_transform = cv.distanceTransform(maskParasite, cv.DIST_L2, 5)
    ret, sure_fg = cv.threshold(dist_transform, 0.7*dist_transform.max(), 255, 0)

    # Encontrar área desconhecida (bordas)
    sure_fg = np.uint8(sure_fg)
    unknown = cv.subtract(sure_bg, sure_fg)

    # Marcar os marcadores
    ret, markers = cv.connectedComponents(maskParasite)
    markers = markers+1
    markers[unknown==255] = 0

    # Aplicar o Watershed
    markers = cv.watershed(im, markers)
    # orig[markers == -1] = [255,0,0]
    markers[markers == -1] = 0
    markers[markers == 1] = 0

    mask_final = cv.bitwise_and(maskParasite, markers.astype(np.uint8))

    #Aplicando flood fill
    im_floodfill = mask_final.copy()

    h, w = mask_final.shape[:2]
    mask = np.zeros((h+2, w+2), np.uint8)
    
    cv.floodFill(im_floodfill, mask, (0,0), 255)
    
    im_floodfill_inv = cv.bitwise_not(im_floodfill)
    im_out = mask_final | im_floodfill_inv

    return  im_out.copy()
    
def croped_object(img, imOrig):
    mask = img.copy()
    crop = imOrig.copy()
      # Encontrar os contornos na imagem binária
    contours, _ = cv.findContours(mask, cv.RETR_EXTERNAL, cv.CHAIN_APPROX_SIMPLE)
    
    cropped_objects = []
    for contour in contours:
        # Obter o retângulo delimitador do contorno
        x, y, w, h = cv.boundingRect(contour)
        
        # Recortar o objeto
        cropped_object = crop[y:y+h, x:x+w]
        cropped_mask = mask[y:y+h, x:x+w]
        segment_croped = cv.bitwise_and(cropped_object, cropped_object, mask=cropped_mask)
        cropped_objects.append({'image': segment_croped, 'area': (x*h)})
        
    for object in cropped_objects:
        if(object['area'] == max([object['area'] for object in cropped_objects])):
            return object['image']

plt.figure("Operacoes")
for im in imgs:
    im = cv.cvtColor(im, cv.COLOR_BGR2RGB)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(im)
    count+=1
    orig = im.copy()
    # im = cv.bilateralFilter(im, 10, 40, 0)
    im = cv.medianBlur(im, 11)
    im = cv.GaussianBlur(im, (3,3),0)

    posterized_image = segmentPostKmean(im, 3)
   
    gray_mask = cv.cvtColor(posterized_image, cv.COLOR_BGR2GRAY)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(gray_mask, cmap="gray")
    count+=1
    
    _, maskParasite = cv.threshold(gray_mask, 0, 255, cv.THRESH_BINARY + cv.THRESH_OTSU)
    maskParasite = cv.bitwise_not(maskParasite)
    # maskParasite = cv.morphologyEx(maskParasite, cv.MORPH_DILATE, cv.getStructuringElement(cv.MORPH_ELLIPSE, (10,10)))
    image = cv.bitwise_and(orig, orig, mask=maskParasite)
    plt.subplot(len(imgs),5, count)
    plt.imshow(image)
    count+=1

    # gray = cv.cvtColor(orig, cv.COLOR_BGR2GRAY)
    # edges = cv.Canny(gray, 50, 150)
    im_out = segmentWatershed(maskParasite, im)
    
    # segment = cv.bitwise_and(orig, orig, mask=im_out)

    croped_parasite = croped_object(im_out, orig)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(croped_parasite)
    count+=1

    P = round(255/4)
    # posterized_image = cv.multiply(cv.divide(segment,P),P)
    nucleos = cv.medianBlur(croped_parasite,3)
    # nucleos = cv.multiply(cv.divide(croped_parasite, P), P)
    nucleos = segmentPostKmean(nucleos, 3)
    nucleos = cv.cvtColor(nucleos, cv.COLOR_BGR2GRAY)
    nucleos = cv.multiply(nucleos, 1.5)
    cv.imwrite(f"nucleos{count}.png", nucleos)
    cv.waitKey(0)
    # pixels = np.unique(nucleos)
    # nucleos[nucleos == pixels[2]] = 0
    # _,nucleos_mask = cv.threshold(nucleos, 0, 255, cv.THRESH_BINARY + cv.THRESH_OTSU)
    # nucleos_mask = cv.morphologyEx(nucleos_mask, cv.MORPH_ERODE, cv.getStructuringElement(cv.MORPH_ELLIPSE, (5,5)))
    plt.subplot(len(imgs), 5, count)
    plt.imshow(nucleos, cmap="gray")
    count+=1
    # histogram = cv.calcHist([np.uint8(nucleos)], [0], None, [256], [0, 256])
    # plt.figure(figsize=(10, 4))
    # print(histogram)
    # plt.plot(histogram, color='gray')
    # plt.title('Histograma da Imagem em Tons de Cinza')
    # plt.xlabel('Intensidade de Pixel')
    # plt.ylabel('Quantidade de Pixels')
    # plt.xlim([0, 256])
    # plt.show()

plt.show()