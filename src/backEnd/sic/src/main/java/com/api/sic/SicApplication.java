package com.api.sic;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SicApplication.class, args);
	}

	@Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    };
}
