import os

def renomear_fotos(pasta_origem):
    # Lista todos os arquivos na pasta de origem
    arquivos = os.listdir(pasta_origem)
    
    # Filtra apenas os arquivos com extensão de imagem
    fotos = [arquivo for arquivo in arquivos if arquivo.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp', '.heic'))]
    
    # Renomeia para um nome padrão i1
    for i, foto in enumerate(fotos, start=1):
        nome, extensao = os.path.splitext(foto)
        novo_nome = f"i{i}{extensao}"
        os.rename(os.path.join(pasta_origem, foto), os.path.join(pasta_origem, novo_nome))
    
    # Lista novamente todos os arquivos na pasta de origem após o primeiro renomeio
    arquivos = os.listdir(pasta_origem)
    fotos = [arquivo for arquivo in arquivos if arquivo.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp', '.heic'))]
    
    # Renomeia para um nome padrão image1
    for i, foto in enumerate(fotos, start=1):
        nome, extensao = os.path.splitext(foto)
        novo_nome = f"image{i}{extensao}"
        os.rename(os.path.join(pasta_origem, foto), os.path.join(pasta_origem, novo_nome))

# Solicita que o usuário insira o caminho da pasta de origem
pasta_origem = input("Por favor, insira o caminho da pasta de origem: ")

renomear_fotos(pasta_origem)
