package com.karla.catalogo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Controlador REST del catálogo. Todas las rutas empiezan con /peliculas.
// Los datos viven en memoria (un mapa), así la API corre sin MySQL.
// Si el servidor se apaga, los datos se pierden: es a propósito,
// para practicar el CRUD sin montar una base de datos.
@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    // Mapa en memoria: id -> película. ConcurrentHashMap es seguro
    // si dos peticiones llegan al mismo tiempo.
    private final Map<Long, Pelicula> catalogo = new ConcurrentHashMap<>();

    // Contador para asignar ids nuevos (1, 2, 3...).
    private final AtomicLong siguienteId = new AtomicLong(1);

    // Datos de ejemplo al arrancar, para probar sin crear nada primero.
    public PeliculaController() {
        guardar(new Pelicula(null, "Coco", "Lee Unkrich", 2017, "Animacion"));
        guardar(new Pelicula(null, "El laberinto del fauno", "Guillermo del Toro", 2006, "Fantasia"));
        guardar(new Pelicula(null, "Roma", "Alfonso Cuaron", 2018, "Drama"));
    }

    // GET /peliculas -> lista todas las películas.
    @GetMapping
    public List<Pelicula> listar() {
        return new ArrayList<>(catalogo.values());
    }

    // GET /peliculas/buscar?titulo=coco -> busca por título (sin distinguir mayúsculas).
    // Va antes que /{id} para que "buscar" no se confunda con un id.
    @GetMapping("/buscar")
    public List<Pelicula> buscarPorTitulo(@RequestParam String titulo) {
        String texto = titulo.toLowerCase();
        return catalogo.values().stream()
                .filter(p -> p.titulo().toLowerCase().contains(texto))
                .toList();
    }

    // GET /peliculas/1 -> una película por id. Si no existe, 404.
    @GetMapping("/{id}")
    public Pelicula buscarPorId(@PathVariable Long id) {
        return buscarOError(id);
    }

    // POST /peliculas -> crea una película. El servidor asigna el id.
    // @Valid revisa las reglas de Pelicula; si fallan, Spring responde 400.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 en vez de 200: "se creó algo nuevo"
    public Pelicula crear(@Valid @RequestBody Pelicula nueva) {
        return guardar(nueva);
    }

    // PUT /peliculas/1 -> actualiza una película que ya existe. Si no existe, 404.
    @PutMapping("/{id}")
    public Pelicula actualizar(@PathVariable Long id, @Valid @RequestBody Pelicula datos) {
        buscarOError(id); // solo para verificar que existe
        Pelicula actualizada = new Pelicula(id, datos.titulo(), datos.director(), datos.anio(), datos.genero());
        catalogo.put(id, actualizada);
        return actualizada;
    }

    // DELETE /peliculas/1 -> elimina. Si no existe, 404. Si se borra, 204 sin cuerpo.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        buscarOError(id);
        catalogo.remove(id);
    }

    // Guarda una película asignándole un id nuevo.
    private Pelicula guardar(Pelicula pelicula) {
        long id = siguienteId.getAndIncrement();
        Pelicula conId = new Pelicula(id, pelicula.titulo(), pelicula.director(), pelicula.anio(), pelicula.genero());
        catalogo.put(id, conId);
        return conId;
    }

    // Busca por id o lanza 404 con un mensaje claro.
    private Pelicula buscarOError(Long id) {
        Pelicula pelicula = catalogo.get(id);
        if (pelicula == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una pelicula con id " + id);
        }
        return pelicula;
    }
}
