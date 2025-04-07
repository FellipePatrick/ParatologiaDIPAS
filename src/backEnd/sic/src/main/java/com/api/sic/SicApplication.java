package com.api.sic;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.api.sic.backend.core.RsaKeyProperties;
import com.api.sic.backend.domain.Usuario;
import com.api.sic.backend.domain.Usuario.Role;
import com.api.sic.backend.repository.UsuarioRepository;
import com.api.sic.backend.service.RelatorioService;
import com.api.sic.backend.service.UsuarioService;

import nu.pattern.OpenCV;

import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SicApplication implements WebMvcConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(SicApplication.class, args);
        OpenCV.loadShared();
    }

    @Autowired
    UsuarioRepository securityUserRerpository;

    @Autowired
    RelatorioService service;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    UsuarioService uService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/WEB-INF/images/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder, UsuarioService uService) {
        PasswordEncoder e = new BCryptPasswordEncoder();
        return args -> {
            if (usuarioRepository.count() == 0) {

                Usuario gestor = new Usuario();
                gestor.setNome("Gestor SIC");
                gestor.setEmail("gestor@sic.com");
                gestor.setPassword(e.encode("admin123"));
                gestor.setMatricula(Usuario.gerarMatricula());
                gestor.setRole(Role.GESTOR);
                gestor.setAdmin(false);
                usuarioRepository.save(gestor);

                Usuario admin = new Usuario();
                admin.setNome("Administrador SIC");
                admin.setEmail("admin@sic.com");
                admin.setPassword(e.encode("admin123"));
                admin.setMatricula(Usuario.gerarMatricula());
                admin.setRole(Role.ADMINISTRADOR);
                admin.setAdmin(true);
                admin.setGestor(uService.findByEmail("gestor@sic.com").get());
                usuarioRepository.save(admin);

                Usuario usuario = new Usuario();
                usuario.setNome("Usuario SIC");
                usuario.setMatricula(Usuario.gerarMatricula());
                usuario.setEmail("user@sic.com");
                usuario.setPassword(e.encode("admin123"));
                usuario.setRole(Role.USUARIO);
                usuario.setAdmin(true);
                usuario.setGestor(uService.findByEmail("gestor@sic.com").get());
                usuarioRepository.save(usuario);

                System.out.println("Usuários criados com sucesso!");
            }
        };
    }
}
