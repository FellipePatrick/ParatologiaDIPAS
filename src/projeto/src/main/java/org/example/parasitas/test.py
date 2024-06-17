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
    
    img[:,:,0] = cv.equalizeHist(img[:,:,0])
    img[:,:,1] = cv.equalizeHist(img[:,:,1])
    img[:,:,2] = cv.equalizeHist(img[:,:,2])
    plt.subplot(len(imgs), 6, count)
    plt.imshow(img)
    count+=1
    C, M, Y, K = convert_to_cmyk(img)

    plt.subplot(len(imgs), 6, count)
    plt.imshow(C, cmap='gray')
    plt.title('Cyan')
    count +=1

    plt.subplot(len(imgs), 6, count)
    plt.imshow(M, cmap='gray')
    plt.title('Magenta')
    count +=1
    plt.subplot(len(imgs), 6, count)
    plt.imshow(Y, cmap='gray')
    plt.title('Yellow')
    count +=1

    plt.subplot(len(imgs), 6, count)
    plt.imshow(K, cmap='gray')
    plt.title('Key (Black)')
    count +=1
    hist = cv.calcHist([K], [0], None, [256], [0, 256])

    plt.subplot(len(imgs), 6, count)
    plt.plot(hist, color='black')
    count +=1
count = 1
for img in imgs:
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    
    img[:,:,0] = cv.equalizeHist(img[:,:,0])
    img[:,:,1] = cv.equalizeHist(img[:,:,1])
    img[:,:,2] = cv.equalizeHist(img[:,:,2])
    plt.subplot(len(imgs), 3, count)
    plt.imshow(img)
    count+=1
    C, M, Y, K = convert_to_cmyk(img)

    plt.subplot(len(imgs), 3, count)
    plt.imshow(K)
    count+=1
    mask = Y < 255 
    # binary_mask = np.uint8(mask) * 255
    plt.subplot(len(imgs),3, count)
    plt.imshow(mask, cmap="binary")
    count+=1


plt.show()