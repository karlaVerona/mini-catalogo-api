package com.karla.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Clase principal: arranca la aplicación Spring Boot.
// Al correrla, la API queda disponible en http://localhost:8080
@SpringBootApplication
public class MiniCatalogoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniCatalogoApiApplication.class, args);
    }
}
