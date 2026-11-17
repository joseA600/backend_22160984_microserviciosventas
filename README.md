# Microservicio de Tienda (Backend)

Backend hecho con Java 21 y Spring Boot 3.4.5. Administra los productos y las ventas de una tienda y guarda todo en PostgreSQL.

## Que necesitas tener instalado

- Java 21 (JDK)
- yo use Docker Desktop
- (Opcional) VS Code con la extension de Java

## Como usarlo en tu computadora

1. Abre Docker Desktop y espera a que quede en verde.
2. Abre una terminal en esta carpeta y corre la base de datos (solo la primera vez):

   ```
   docker run -d --name microservicio_postgres -e POSTGRES_DB=microservicio_db -e POSTGRES_USER=tienda -e POSTGRES_PASSWORD=tienda123 -v microservicio_pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:16-alpine
   ```

   Las tablas se crean solas al arrancar el backend.

3. Si ya creaste el contenedor antes, solo enciende el y arranca el backend:

   ```
   docker start microservicio_postgres
   .\mvnw.cmd spring-boot:run
   ```

4. Cuando veas "Tomcat started on port 8080" el backend esta listo.

## Donde revisar que funciona

- Salud del microservicio: http://localhost:8080/actuator/health
- Documentacion de la API (Swagger): http://localhost:8080/swagger-ui.html
- Productos: http://localhost:8080/api/productos

## Conexion a la base de datos


```
DB_URL=jdbc:postgresql://localhost:5432/microservicio_db
DB_USERNAME=tienda
DB_PASSWORD=tienda123
```

## Para apagar todo

1. En la terminal del backend presiona Ctrl + C.
2. Corre: `docker stop microservicio_postgres` (no borra los datos).

## Notas

- Si sale "Port 8080 was already in use" ya hay otro backend corriendo: cierralo o reinicia tu computadora.
- Si la base de datos no conecta, revisa que Docker Desktop este abierto y el contenedor encendido (`docker ps`).
