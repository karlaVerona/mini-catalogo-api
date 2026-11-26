package com.karla.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de la API sin levantar el servidor: MockMvc simula
// las peticiones HTTP contra el controlador de verdad.
@SpringBootTest
@AutoConfigureMockMvc
class PeliculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // GET /peliculas debe listar las 3 películas de ejemplo.
    @Test
    void listarDevuelveLasPeliculasDeEjemplo() throws Exception {
        mockMvc.perform(get("/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // GET /peliculas/999 debe responder 404 porque no existe.
    @Test
    void buscarIdInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/peliculas/999"))
                .andExpect(status().isNotFound());
    }

    // POST sin título debe responder 400 por la validación.
    @Test
    void crearSinTituloDevuelve400() throws Exception {
        String json = "{\"titulo\":\"\",\"director\":\"Alguien\",\"anio\":2020,\"genero\":\"Drama\"}";
        mockMvc.perform(post("/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
