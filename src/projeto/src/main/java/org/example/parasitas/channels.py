import cv2 as cv
import matplotlib.pyplot as plt
import os
import numpy as np
files = os.listdir()

def convert_to_cmyk(image):
    # Normalizar a imagem para o intervalo [0, 1]
    img_normalized = image / 255.0
    R, G, B = cv.split(img_normalized)

    # Calculando os componentes CMY
    C = 1 - R
    M = 1 - G
    Y = 1 - B

    # Calculando o componente K
    K = np.min([C, M, Y], axis=0)

    # Ajustando os componentes CMY de acordo com K
    C = (C - K) / (1 - K + 1e-10)
    M = (M - K) / (1 - K + 1e-10)
    Y = (Y - K) / (1 - K + 1e-10)

    # Converter de volta para a faixa [0, 255]
    C = (C * 255).astype(np.uint8)
    M = (M * 255).astype(np.uint8)
    Y = (Y * 255).astype(np.uint8)
    K = (K * 255).astype(np.uint8)

    return C, M, Y, K

def create_circular_kernel(diameter):
    radius = diameter // 2
    kernel = np.zeros((diameter, diameter), dtype=np.uint8)
    cv.circle(kernel, (radius, radius), radius, 1, -1)
    return kernel

imgs:list = []
for file in files:
    if 'jpeg' in file:
        imgs.append(cv.imread(file))

plt.figure("Canais CMYK")
count = 1
for img in imgs:
    result = img.copy()
    img = cv.medianBlur(img, 7)
    img = cv.GaussianBlur(img, (7,7),0)
    
    img = cv.bilateralFilter(img, 5, 40, 0)
    
    red = img[:,:, 2]
    
    ycbcr = cv.cvtColor(img, cv.COLOR_BGR2YCrCb)
    cb = ycbcr[:,:,1]
    imSeg2 = cv.subtract(red,cb)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(imSeg2, cmap='gray')
    # plt.title('Cyan')
    count +=1

    ret, binary1 = cv.threshold(imSeg2, 0, 255,  cv.THRESH_BINARY + cv.THRESH_OTSU)
    kernel = cv.getStructuringElement(cv.MORPH_ELLIPSE, (5, 5))
    binary1 = cv.bitwise_not(binary1)
    # binary = cv.morphologyEx(binary1, cv.MORPH_OPEN, kernel) 
    imageTeste = result.copy()
    imageTeste[:,:,0] = cv.bitwise_and(imageTeste[:,:,0], binary1)
    imageTeste[:,:,1] = cv.bitwise_and(imageTeste[:,:,1], binary1)
    imageTeste[:,:,2] = cv.bitwise_and(imageTeste[:,:,2], binary1)
    imageTeste = cv.cvtColor(imageTeste, cv.COLOR_BGR2RGB)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(imageTeste, cmap='gray')
    # plt.title('Cyan')
    count +=1

    
    # img[:,:,0] = cv.equalizeHist(img[:,:,0])
    # img[:,:,1] = cv.equalizeHist(img[:,:,1])
    # img[:,:,2] = cv.equalizeHist(img[:,:,2])
    C, M, Y, K = convert_to_cmyk(img)

    imSeg = cv.add(cv.add(Y, K), C)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(imSeg, cmap='gray')
    # plt.title('Cyan')
    count +=1

    
    ret, binary1 = cv.threshold(imSeg, 0, 255,  cv.THRESH_BINARY + cv.THRESH_OTSU)
    kernel = cv.getStructuringElement(cv.MORPH_ELLIPSE, (5, 5))

    binary = cv.morphologyEx(binary1, cv.MORPH_OPEN, kernel)

    image = result.copy()
    image[:,:,0] = cv.bitwise_and(image[:,:,0], binary)
    image[:,:,1] = cv.bitwise_and(image[:,:,1], binary)
    image[:,:,2] = cv.bitwise_and(image[:,:,2], binary)
    image = cv.cvtColor(image, cv.COLOR_BGR2RGB)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(image)
    # plt.title('Cyan')
    count +=1

    # noise removal
    kernel = np.ones((3,3),np.uint8)
    opening = cv.morphologyEx(binary,cv.MORPH_OPEN,kernel, iterations = 2)
    # sure background area
    sure_bg = cv.dilate(opening,kernel,iterations=3)
    # Finding sure foreground area
    dist_transform = cv.distanceTransform(opening,cv.DIST_L2,5)
    ret, sure_fg = cv.threshold(dist_transform,0.7*dist_transform.max(),255,0)
    # Finding unknown region
    sure_fg = np.uint8(sure_fg)
    unknown = cv.subtract(sure_bg,sure_fg)
    dist_transform = cv.distanceTransform(binary, cv.DIST_L2, 5)

    # ret, sure_fg = cv.threshold(dist_transform, 0.2 * dist_transform.max(), 255, 0)
    # plt.subplot(len(imgs), 5, count)
    # plt.imshow(sure_fg, cmap='gray')
    # # plt.title('Cyan')
    # count +=1

    ret, markers = cv.connectedComponents(sure_fg)
    # Add one to all labels so that sure background is not 0, but 1
    markers = markers+1
    # Now, mark the region of unknown with zero
    markers[unknown==255] = 0
    markers = cv.watershed(img,markers)
    result[markers == -1] = [255,0,0]

    result = cv.cvtColor(result, cv.COLOR_BGR2RGB)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(result, cmap='gray')
    # plt.title('Cyan')
    count +=1


plt.show()