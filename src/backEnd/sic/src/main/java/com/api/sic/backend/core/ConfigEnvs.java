package com.api.sic.backend.core;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigEnvs {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String servidor_to_mobile = dotenv.get("ServerToMobile");

    public static final String pagina_web_senha = dotenv.get("PaginaWebSenha");
}