package com.karla.catalogo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Una película del catálogo. Es un record: Java genera solo
// el constructor, los getters y equals/hashCode.
// Las anotaciones validan los datos que llegan en el JSON:
// si fallan, Spring responde 400 Bad Request sin que
// tengamos que escribir ese código a mano.
public record Pelicula(

        // El id lo asigna el servidor al crear la película,
        // por eso en el POST se puede mandar null.
        Long id,

        // El título es obligatorio y no puede ir en blanco.
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @NotBlank(message = "El director es obligatorio")
        String director,

        // El cine nació en 1888 (primeras películas) y no
        // aceptamos años futuros imposibles.
        @Min(value = 1888, message = "El anio debe ser 1888 o mayor")
        @Max(value = 2100, message = "El anio debe ser 2100 o menor")
        int anio,

        String genero
) {
}
