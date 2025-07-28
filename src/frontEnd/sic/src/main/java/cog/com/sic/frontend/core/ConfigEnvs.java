package cog.com.sic.frontend.core;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigEnvs {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String servidor = dotenv.get("SERVIDOR");
}