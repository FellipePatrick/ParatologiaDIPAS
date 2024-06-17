import os
import platform
import subprocess

def bloquear_pagina(url):
    # Determinar o arquivo de hosts com base no sistema operacional
    if platform.system() == "Windows":
        hosts_path = "C:\\Windows\\System32\\drivers\\etc\\hosts"
    elif platform.system() == "Linux" or platform.system() == "Darwin":  # Darwin é macOS
        hosts_path = "/etc/hosts"
    else:
        raise Exception("Sistema operacional não suportado")

    # Adicionar a linha ao arquivo de hosts
    with open(hosts_path, 'a') as hosts_file:
        hosts_file.write(f"127.0.0.1    {url}\n")

    # Limpar o cache DNS (requer privilégios de administrador)
    if platform.system() == "Windows":
        subprocess.run(["ipconfig", "/flushdns"], check=True)
    elif platform.system() == "Linux":
        subprocess.run(["sudo", "systemctl", "restart", "NetworkManager"], check=True)
    elif platform.system() == "Darwin":  # macOS
        subprocess.run(["sudo", "dscacheutil", "-flushcache"], check=True)
        subprocess.run(["sudo", "killall", "-HUP", "mDNSResponder"], check=True)

# URL da página a ser bloqueada
pagina_para_bloquear = "https://www.instagram.com/"

# Bloquear a página
bloquear_pagina(pagina_para_bloquear)
print(f"Página {pagina_para_bloquear} bloqueada com sucesso.")
