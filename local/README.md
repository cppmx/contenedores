# Contenedores

Los cuatro proyectos que he creado localmente son los siguientes:

1. [Una aplicación REST API sencilla usando Perl](README_PERL.md)
2. [Una aplicación REST API sencilla usando PHP](RAEDME_PHP.md)
3. [Una aplicación REST API sencilla usando Groovy](README_GROOVY.md)
4. [Una aplicación REST API sencilla usando Kotlin](README_KOTLIN.md)

Las dos primeras aplicaciones son básicamente un script, y las dos últimas usan Gradle para construir el JAR que se ejecutará en el contenedor.

Estas aplicaciones hacen algo muy sencillo, expone una API en el path `/` la cual puede ser consultada mediante el verbo `GET`. La respuesta es similar en todos los casos, un string en formato JSON que dice lo siguiente:

```json
{
   "alumno":"Carlos Colón",
   "materia":"Contenedores",
   "maestria":"Desarrollo y Operaciones de Software (DevOps)",
   "mensaje":"Hello from <language> API!",
   "universidad":"UNIR"
}
```

El proyecto está organizado de forma estándar para que se puedan ejecutar los mismos comandos para construir cada una de las imágenes:

```bash
docker build -t unir:<tag> -f Dockerfile.<ext> .
```

Soĺo hay que reemplazar el tag por el identificador que se le desea dar a cada imagen, y usar la extensión adecuada para cada Dockerfile.

De igual forma la ejecución de los contenedores será con el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 unir:<tag>
```

Siguiendo la misma línea, la prueba de cada aplicación se hará con el mismo comando:

```bash
curl localhost:8080
```

En los documentos de cada aplicación se da una explicación más amplia de estos comandos.

## Usar Docker compose

También se agrega un archivo `docker-compose.yml` el cual levanta todos los contenedores al mismo tiempo. Los contenedores creados por compose usarán los siguientes puertos:

- 8081 para la aplicación Perl
- 8082 para la aplicación PHP
- 8083 para la aplicación Groovy
- 8084 para la aplicación Kotlin

Para levantar los contenedores usaremos el siguiente comando:

```bash
docker compose up -d

[+] Building 419.7s (37/37) FINISHED                                                                  docker:default
 => [app1 internal] load .dockerignore                                                                          0.0s
 => => transferring context: 115B                                                                               0.0s
 => [app1 internal] load build definition from Dockerfile.app1                                                  0.1s
 => => transferring dockerfile: 458B                                                                            0.0s
 => [app4 internal] load build definition from Dockerfile.app4                                                  0.2s
 => => transferring dockerfile: 629B                                                                            0.0s
 => [app4 internal] load .dockerignore                                                                          0.3s
 => => transferring context: 115B                                                                               0.0s
 => [app2 internal] load .dockerignore                                                                          0.3s
 => => transferring context: 115B                                                                               0.0s
 => [app2 internal] load build definition from Dockerfile.app2                                                  0.4s
 => => transferring dockerfile: 507B                                                                            0.0s
 => [app3 internal] load build definition from Dockerfile.app3                                                  0.4s
 => => transferring dockerfile: 629B                                                                            0.0s
 => [app3 internal] load .dockerignore                                                                          0.4s
 => => transferring context: 115B                                                                               0.0s
 => [app1 internal] load metadata for docker.io/library/perl:5.38.2                                             2.7s
 => [app4 internal] load metadata for docker.io/library/openjdk:17-jdk-slim                                     1.4s
 => [app2 internal] load metadata for docker.io/library/php:7.4-apache                                          1.6s
 => [app1 auth] library/perl:pull token for registry-1.docker.io                                                0.0s
 => [app4 auth] library/openjdk:pull token for registry-1.docker.io                                             0.0s
 => [app2 auth] library/php:pull token for registry-1.docker.io                                                 0.0s
 => [app3 builder 1/4] FROM docker.io/library/openjdk:17-jdk-slim@sha256:aaa3b3cb27e3e520b8f116863d0580c438ed5  0.0s
 => [app3 internal] load build context                                                                          0.4s
 => => transferring context: 295.49kB                                                                           0.0s
 => [app4 internal] load build context                                                                          0.9s
 => => transferring context: 25.90MB                                                                            0.2s
 => [app2 internal] load build context                                                                          0.5s
 => => transferring context: 327B                                                                               0.0s
 => [app2 1/3] FROM docker.io/library/php:7.4-apache@sha256:c9d7e608f73832673479770d66aacc8100011ec751d1905ff6  0.0s
 => CACHED [app4 builder 2/4] WORKDIR /app                                                                      0.0s
 => [app3 builder 3/4] COPY app3/ .                                                                             0.5s
 => CACHED [app4 builder 3/4] COPY app4/ .                                                                      0.0s
 => CACHED [app4 builder 4/4] RUN ./gradlew build                                                               0.0s
 => CACHED [app4 stage-1 3/3] COPY --from=builder /app/build/libs/app4-0.0.1-SNAPSHOT.jar service.jar           0.0s
 => [app3 builder 4/4] RUN ./gradlew build                                                                     38.6s
 => [app4] exporting to image                                                                                   0.0s
 => => exporting layers                                                                                         0.0s
 => => writing image sha256:3de6e8dbdfc1e9634dd938815676cb0a0fe43f273622eb5b776df0b34b294089                    0.0s
 => => naming to docker.io/library/contenedores-app4                                                            0.0s
 => CACHED [app2 2/3] COPY app2/index.php /var/www/html/                                                        0.0s
 => CACHED [app2 3/3] RUN sed -i 's/80/8080/g' /etc/apache2/sites-available/000-default.conf /etc/apache2/port  0.0s
 => [app2] exporting to image                                                                                   0.0s
 => => exporting layers                                                                                         0.0s
 => => writing image sha256:8247bdc85d01c795ece71c215637c649d0927c62e56fcb1e2d21fd14650516eb                    0.0s
 => => naming to docker.io/library/contenedores-app2                                                            0.0s
 => [app1 1/4] FROM docker.io/library/perl:5.38.2@sha256:911d58e98b72cdf25d5e5785c3d4a715f69556463747f13ee058f  1.7s
 => => resolve docker.io/library/perl:5.38.2@sha256:911d58e98b72cdf25d5e5785c3d4a715f69556463747f13ee058f96d70  0.0s
 => => sha256:911d58e98b72cdf25d5e5785c3d4a715f69556463747f13ee058f96d70b7a529 6.88kB / 6.88kB                  0.0s
 => => sha256:76001266756d8008463849740c5f0c0ea3170cd23b9798d93df5fcc9bc08c5aa 5.15kB / 5.15kB                  0.0s
 => => sha256:68fca170be8f34dda4500bc9c1467357299b4f6e52069151563fd77278639458 135B / 135B                      0.2s
 => => sha256:ff3843b3884f6f924b999488413e963d877a74c30f52d6f05022594b4d7c79de 15.64MB / 15.64MB                1.1s
 => => sha256:bccd0b3f2598e5c030bfa0165881b8a9fc1e8dcf2ac4b2c993f0e3ce251fbc40 133B / 133B                      0.2s
 => => sha256:277b51421f83d6b26442ef65bfaa3261c1a0efd7478da56704397babe861e467 2.00kB / 2.00kB                  0.0s
 => => extracting sha256:68fca170be8f34dda4500bc9c1467357299b4f6e52069151563fd77278639458                       0.0s
 => => extracting sha256:ff3843b3884f6f924b999488413e963d877a74c30f52d6f05022594b4d7c79de                       0.4s
 => => extracting sha256:bccd0b3f2598e5c030bfa0165881b8a9fc1e8dcf2ac4b2c993f0e3ce251fbc40                       0.0s
 => [app1 internal] load build context                                                                          0.0s
 => => transferring context: 490B                                                                               0.0s
 => [app1 2/4] WORKDIR /app                                                                                     0.1s
 => [app1 3/4] RUN cpanm Dancer2 JSON                                                                         414.4s
 => [app3 stage-1 3/3] COPY --from=builder /app/build/libs/app3-0.0.1-SNAPSHOT.jar service.jar                  0.1s
 => [app3] exporting to image                                                                                   0.1s
 => => exporting layers                                                                                         0.1s
 => => writing image sha256:201f7e54ba409c53cee65db1c2ea48d392e51ad0a34b136bb609e86eba876c09                    0.0s
 => => naming to docker.io/library/contenedores-app3                                                            0.0s
 => [app1 4/4] COPY app1/app.pl .                                                                               0.1s
 => [app1] exporting to image                                                                                   0.5s
 => => exporting layers                                                                                         0.5s
 => => writing image sha256:b49f14a6f20b75bdd9152e795fb1804c37007d3d3434ac024814adb6e4dd95de                    0.0s
 => => naming to docker.io/library/contenedores-app1                                                            0.0s
[+] Running 5/5
 ✔ Network contenedores_default   Created                                                                       0.2s 
 ✔ Container contenedores-app2-1  Started                                                                       0.1s 
 ✔ Container contenedores-app3-1  Started                                                                       0.1s 
 ✔ Container contenedores-app1-1  Started                                                                       0.1s 
 ✔ Container contenedores-app4-1  Started                                                                       0.1s
```

**NOTA**: Yo estoy usando Red Hat Enterprise 8.0 para esta prueba, y en mi caso `compose` forma parte de los comandos de `docker`. Sin embargo, en algunos sistemas `docker compose` es una aplicación por separado y el comando suele ser `docker-compose`.

Podemos ver los servicios levantados por Docker compose con el siguiente comando:

```bash
docker compose ps

NAME                  IMAGE               COMMAND                                       SERVICE   CREATED          STATUS          PORTS
contenedores-app1-1   contenedores-app1   "plackup app.pl --host 0.0.0.0 --port 8080"   app1      10 minutes ago   Up 10 minutes   0.0.0.0:8081->8080/tcp, :::8081->8080/tcp
contenedores-app2-1   contenedores-app2   "docker-php-entrypoint apache2-foreground"    app2      10 minutes ago   Up 10 minutes   80/tcp, 0.0.0.0:8082->8080/tcp, :::8082->8080/tcp
contenedores-app3-1   contenedores-app3   "java -jar service.jar"                       app3      10 minutes ago   Up 10 minutes   0.0.0.0:8083->8080/tcp, :::8083->8080/tcp
contenedores-app4-1   contenedores-app4   "java -jar service.jar"                       app4      10 minutes ago   Up 10 minutes   0.0.0.0:8084->8080/tcp, :::8084->8080/tcp
```

Para probar los servicios levantados podemos ejecutar el siguiente comando:

```bash
for port in 8081 8082 8083 8084; do response_code=$(curl -s -o /dev/null -w "%{http_code}" localhost:$port) && [ "$response_code" -eq 200 ] || { echo "Error en el puerto $port (HTTP $response_code)"; exit 1; }; done
```

Para detener los contenedores sólo hay que ejecutar el siguiente comando:

```bash
docker compose down

[+] Running 5/5
 ✔ Container contenedores-app4-1  Removed                                                       0.3s 
 ✔ Container contenedores-app1-1  Removed                                                      10.2s 
 ✔ Container contenedores-app2-1  Removed                                                       1.2s 
 ✔ Container contenedores-app3-1  Removed                                                       0.3s 
 ✔ Network contenedores_default   Removed                                                       0.1s
```

## Uso de las imágenes públicas

Las imágenes creadas para este proyecto han sido subidas a un repositorio público, se puede hacer uso de ellas sin tener que compilar los Dockerfiles.

### Usar la imagen de Perl del repositorio público

Para levantar un contenedor usando la imagen de Perl usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 cppmx/unir:perl
```

Para probar el contenedor ejecutaremos el comando:

```bash
curl localhost:8080
```

### Usar la imagen de PHP del repositorio público

Para levantar un contenedor usando la imagen de PHP usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 cppmx/unir:php
```

Para probar el contenedor ejecutaremos el comando:

```bash
curl localhost:8080
```

### Usar la imagen de Groovy del repositorio público

Para levantar un contenedor usando la imagen de Groovy usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 cppmx/unir:groovy
```

Para probar el contenedor ejecutaremos el comando:

```bash
curl localhost:8080
```

### Usar la imagen de Kotlin del repositorio público

Para levantar un contenedor usando la imagen de Kotlin usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 cppmx/unir:kotlin
```

Para probar el contenedor ejecutaremos el comando:

```bash
curl localhost:8080
```
