# mini-catalogo-api

API REST pequeña en Java con Spring Boot: un catálogo de películas con CRUD completo.

Es un proyecto de práctica, pensado para subir a GitHub y poder explicarlo en una entrevista.

## Qué hace

- Lista todas las películas: `GET /peliculas`
- Busca una por id: `GET /peliculas/{id}`
- Busca por título: `GET /peliculas/buscar?titulo=coco`
- Crea una película: `POST /peliculas`
- Actualiza una película: `PUT /peliculas/{id}`
- Elimina una película: `DELETE /peliculas/{id}`

Los datos viven **en memoria**, así que la API corre sin instalar ni configurar MySQL. Al apagar el servidor, los datos se pierden y al arrancar vuelven las 3 películas de ejemplo. En `src/main/resources/application.properties` está documentado cómo se pasaría a MySQL (dependencias JPA + conector y las propiedades, con usuario y contraseña por variables de entorno, nunca en el código).

Si pides un id que no existe, responde **404**. Si mandas datos inválidos (por ejemplo, título vacío), responde **400**.

## Tecnologías

- Java 21
- Spring Boot 3.2.5
- Maven
- Spring Web y Spring Validation (`jakarta.validation`)
- JUnit 5 + MockMvc para las pruebas

## Cómo correrlo

Requisitos: Java 21 y Maven.

```bash
# 1. Compilar y correr las pruebas
mvn test

# 2. Arrancar la API (queda en http://localhost:8080)
mvn spring-boot:run
```

## Probarlo con curl

Con la API corriendo en otra terminal:

```bash
# Listar todas (trae las 3 de ejemplo)
curl http://localhost:8080/peliculas

# Buscar por id
curl http://localhost:8080/peliculas/1

# Buscar por título
curl "http://localhost:8080/peliculas/buscar?titulo=coco"

# Crear una película (responde 201 con la película creada y su id)
curl -X POST http://localhost:8080/peliculas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Dune","director":"Denis Villeneuve","anio":2021,"genero":"Ciencia ficcion"}'

# Actualizar la película 1
curl -X PUT http://localhost:8080/peliculas/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Coco","director":"Lee Unkrich","anio":2017,"genero":"Animacion"}'

# Eliminar la película 2 (responde 204, sin cuerpo)
curl -X DELETE http://localhost:8080/peliculas/2 -i

# Pedir un id que no existe (responde 404)
curl -i http://localhost:8080/peliculas/999
```

## Estructura

```
mini-catalogo-api/
├── pom.xml                                  # Dependencias y versión de Java (Spring Boot 3.2.5)
└── src/main/java/com/karla/catalogo/
    ├── MiniCatalogoApiApplication.java      # Clase principal, arranca Spring Boot
    ├── Pelicula.java                        # Modelo + validación (@NotBlank, @Min, @Max)
    └── PeliculaController.java              # Endpoints REST y datos en memoria
└── src/main/resources/
    └── application.properties             # Puerto 8080 y opción MySQL comentada
└── src/test/java/com/karla/catalogo/
    └── PeliculaControllerTest.java          # 3 pruebas con MockMvc
```

## Cómo explicarlo en entrevista

**1. ¿Qué hace este proyecto?**
> Es una API REST en Spring Boot para un catálogo de películas, con los 5 verbos del CRUD: listar, buscar por id y por título, crear, actualizar y eliminar. Los datos están en memoria, a propósito, para practicar el CRUD sin montar una base de datos, y en el `application.properties` dejé documentado cómo se pasaría a MySQL.

**2. ¿Por qué en memoria y no en base de datos?**
> Para que el proyecto corra con un solo comando, sin instalar MySQL. El controlador guarda en un `ConcurrentHashMap`, que es seguro si llegan dos peticiones a la vez, y los ids los da un `AtomicLong`. El siguiente paso sería cambiar ese mapa por un repositorio JPA con MySQL, y ya dejé anotado qué dependencias y propiedades faltan.

**3. ¿Cómo manejas los errores?**
> Si el id no existe lanzo `ResponseStatusException` con 404 y un mensaje. Para los datos inválidos uso `@Valid` con las reglas en el modelo (`@NotBlank`, `@Min`, `@Max`), y Spring responde 400 solo. Al crear respondo 201 y al borrar 204. Y hay 3 pruebas con MockMvc que verifican el listado, el 404 y el 400.
