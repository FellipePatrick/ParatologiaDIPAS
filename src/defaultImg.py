import os
from PIL import Image
import pillow_heif

def heicToJpeg(input_path):
    try:
        # Carrega o arquivo HEIC
        heif_file = pillow_heif.read_heif(input_path)
        
        # Converte para uma imagem PIL
        image = Image.frombytes(
            heif_file.mode,
            heif_file.size,
            heif_file.data,
            "raw",
            heif_file.mode,
            heif_file.stride,
        )

        # Define o novo caminho para o arquivo JPEG
        novo_caminho = os.path.splitext(input_path)[0] + '.jpeg'
        
        # Salva a imagem no formato JPEG
        image.save(novo_caminho, format="JPEG")
        return novo_caminho
    except Exception as e:
        print(f"Erro ao converter {input_path}: {e}")
        return None

def renomear_fotos(pasta_origem):
    arquivos = os.listdir(pasta_origem)
    
    fotos = []
    for arquivo in arquivos:
        if arquivo.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp', '.heic')):
            caminho_arquivo = os.path.join(pasta_origem, arquivo)
            if arquivo.lower().endswith('.heic'):
                novo_caminho = heicToJpeg(caminho_arquivo)
                if novo_caminho:
                    os.remove(caminho_arquivo)
                    fotos.append(os.path.basename(novo_caminho))
            else:
                fotos.append(arquivo)
    
    for i, foto in enumerate(fotos, start=1):
        nome, extensao = os.path.splitext(foto)
        novo_nome = f"i{i}{extensao}"
        os.rename(os.path.join(pasta_origem, foto), os.path.join(pasta_origem, novo_nome))
    
    arquivos = os.listdir(pasta_origem)
    fotos = [arquivo for arquivo in arquivos if arquivo.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp'))]
    
    for i, foto in enumerate(fotos, start=1):
        nome, extensao = os.path.splitext(foto)
        novo_nome = f"image{i}{extensao}"
        os.rename(os.path.join(pasta_origem, foto), os.path.join(pasta_origem, novo_nome))

# Solicita ao usuário para inserir o caminho da pasta de origem
pasta_origem = input("Por favor, insira o caminho da pasta de origem: ")

# Chama a função para renomear as fotos
renomear_fotos(pasta_origem)
