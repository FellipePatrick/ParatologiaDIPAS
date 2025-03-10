package com.api.sic;

import org.modelmapper.ModelMapper;
import org.opencv.core.Core;
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
import com.api.sic.backend.service.Image;

import nu.pattern.OpenCV;

import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SicApplication implements WebMvcConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(SicApplication.class, args);
        OpenCV.loadShared();
	
		// Image.resetDiretorio("C:\\Users\\felli\\Pictures\\Imagens DIPAS\\img\\result");

        // //Segmentando uma pasta de imagens
        // //org.example.Image.segmentImages("C:\\Users\\felli\\Repositorio GitHub\\ParatologiaDIPAS\\img", "jpeg" , 4, "sim");

        // //Segmentando apenas uma imagem especifica
        // Image.segmentImage("C:\\Users\\felli\\Pictures\\Imagens DIPAS\\img", 2 , "não");
    }

    @Autowired
    UsuarioRepository securityUserRerpository;

    @Autowired
    BCryptPasswordEncoder encoder;

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
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder) {
        PasswordEncoder e = new BCryptPasswordEncoder();
        return args -> {
            if (usuarioRepository.count() == 0) { // Evita duplicação de usuários ao reiniciar a aplicação
                Usuario admin = new Usuario();
                admin.setNome("Admin User");
                admin.setEmail("admin@sic.com");
                admin.setPassword(e.encode("admin123"));
                admin.setRole(Role.ADMINISTRADOR);
                admin.setAdmin(true);
                usuarioRepository.save(admin);

                Usuario user = new Usuario();
                user.setNome("Normal User");
                user.setEmail("user@sic.com");
                user.setPassword(e.encode("user123"));
                user.setRole(Role.USUARIO);
                user.setAdmin(false);
                usuarioRepository.save(user);

                System.out.println("Usuários criados com sucesso!");
            }
        };
    }
}
