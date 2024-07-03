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
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    img = cv.medianBlur(img, 5)
    
    # img[:,:,0] = cv.equalizeHist(img[:,:,0])
    # img[:,:,1] = cv.equalizeHist(img[:,:,1])
    # img[:,:,2] = cv.equalizeHist(img[:,:,2])
    plt.subplot(len(imgs), 5, count)
    plt.imshow(img)
    count+=1
    C, M, Y, K = convert_to_cmyk(img)

    plt.subplot(len(imgs), 5, count)
    plt.imshow(C, cmap='gray')
    plt.title('Cyan')
    count +=1

    plt.subplot(len(imgs), 5, count)
    plt.imshow(M, cmap='gray')
    plt.title('Magenta')
    count +=1
    plt.subplot(len(imgs), 5, count)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(Y, cmap='gray')
    plt.title('Yellow')
    count +=1

    plt.subplot(len(imgs), 5, count)
    plt.imshow(K, cmap='gray')
    plt.title('Key (Black)')
    count +=1

imgs:list = []
for file in files:
    if 'jpeg' in file:
        imgs.append(cv.imread(file))
# plt.show()
plt.figure("Canais HSV")
count = 1
for img in imgs:

    # img[:,:,0] = cv.equalizeHist(img[:,:,0])
    # img[:,:,1] = cv.equalizeHist(img[:,:,1])
    # img[:,:,2] = cv.equalizeHist(img[:,:,2])
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    img = cv.medianBlur(img, 5)
    # img = cv.GaussianBlur(img, (3,3),0)
    plt.subplot(len(imgs), 5, count)
    plt.imshow(img)
    count+=1
    img = cv.cvtColor(img, cv.COLOR_RGB2HSV)
    # plt.title(count)
    count+=1
    plt.subplot(len(imgs), 5, count)
    plt.imshow(img[:,:,0], cmap='gray')
    # plt.title(count)
    count+=1
    plt.subplot(len(imgs), 5, count)
    plt.imshow(img[:,:,1], cmap='gray')
    # plt.title(count)
    count+=1
    plt.subplot(len(imgs), 5, count)
    plt.imshow(img[:,:,2], cmap='gray')
    # plt.title(count)
    count+=1

    
imgs:list = []
for file in files:
    if 'jpeg' in file:
        imgs.append(cv.imread(file))
count = 1
plt.figure("Operacoes")
for img in imgs:

    orig = img.copy()
    # img = cv.GaussianBlur(img, (3,3),0)
    img = cv.medianBlur(img, 5)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    # img[:,:,0] = cv.equalizeHist(img[:,:,0])
    # img[:,:,1] = cv.equalizeHist(img[:,:,1])
    # img[:,:,2] = cv.equalizeHist(img[:,:,2])
    
    plt.subplot(len(imgs), 6, count)
    plt.imshow(img)
    count+=1
    C, M, Y, K = convert_to_cmyk(img)
    img = cv.cvtColor(img, cv.COLOR_RGB2HSV)
    # plt.title(count)
    plt.subplot(len(imgs), 6, count)
    plt.imshow(K, cmap='gray')
    count+=1

    ret, binary1 = cv.threshold(img[:,:,1], 0, 255,  cv.THRESH_BINARY + cv.THRESH_OTSU)
    plt.subplot(len(imgs), 6, count)
    plt.imshow(binary1, cmap='gray')
    # plt.title(count)
    count+=1

    # ret, binary2 = cv.threshold(K, 0, 255,  cv.THRESH_BINARY + cv.THRESH_OTSU)
    binary2 = cv.adaptiveThreshold(K,255,cv.ADAPTIVE_THRESH_MEAN_C,  cv.THRESH_BINARY_INV,11,4)
    plt.subplot(len(imgs), 6, count)
    plt.imshow(cv.bitwise_not(binary2), cmap='binary')
    count+=1

    binary = cv.bitwise_or(binary1, binary2)

    ycbcr = cv.cvtColor(orig, cv.COLOR_BGR2YCrCb)
    hsv = cv.cvtColor(orig, cv.COLOR_BGR2HSV)

    result = cv.add(ycbcr[:,:,2], K)
    plt.subplot(len(imgs), 6, count)
    ret, binary3 = cv.threshold(result, 210, 255,  cv.THRESH_BINARY)
    plt.imshow(binary3, cmap='binary')
    plt.title(count)
    count+=1
    orig = cv.cvtColor(orig, cv.COLOR_BGR2RGB)
    # binary = cv.bitwise_not(binary)
    orig[:,:,0] = cv.bitwise_and(orig[:,:,0], binary)
    orig[:,:,1] = cv.bitwise_and(orig[:,:,1], binary)
    orig[:,:,2] = cv.bitwise_and(orig[:,:,2], binary)
    plt.subplot(len(imgs), 6, count)
    plt.imshow(orig)
    count+=1

plt.show()