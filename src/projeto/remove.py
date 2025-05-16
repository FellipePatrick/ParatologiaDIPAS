from rembg import remove
from PIL import Image
import os
import sys

def remover_fundo(path_imagem):
    nome_arquivo = os.path.splitext(os.path.basename(path_imagem))[0]
    imagem_saida = f"{nome_arquivo}_sem_fundo.png"

    with Image.open(path_imagem) as img:
        imagem_sem_fundo = remove(img)
        imagem_sem_fundo.save("C:\\Users\\felli\Pictures\\Imagens DIPAS\\imgZoom\\result" + imagem_saida)
        print(f"Fundo removido e salvo como {imagem_saida}")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Use: python ex.py <caminho_imagem>")
        sys.exit(1)
    caminho_imagem = sys.argv[1]
    remover_fundo(caminho_imagem)
