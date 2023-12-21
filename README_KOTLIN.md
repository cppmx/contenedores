# Una aplicación REST API sencilla usando Kotlin

El Dockerfile para construir la aplicación de Kotlin se llama `Dockerfile.app4` y contiene lo siguiente:

```bash
# Etapa 1: Compilar y generar el JAR
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY app4/ .

# Compila y genera el JAR
RUN ./gradlew build

# Etapa 2: Ejecutar la aplicación
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia solo los archivos necesarios de la etapa de compilación
COPY --from=builder /app/build/libs/app4-0.0.1-SNAPSHOT.jar service.jar

# Expone el puerto en el que la aplicación Spring Boot escucha
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "service.jar"]
```

Se necesita compilar el proyecto y generar el JAR que se usará para arrancar el contenedor, así que para este Dockerfile usaré dos capas.

- En la primera capa usaré `openjdk:17-jdk-slim` como imagen base, y nombraré a esta capa `builder`.
- Después se define `/app` como el directorio de trabajo.
- Copiamos el contenido de la carpeta `app4` en el directorio de trabajo.
- Después ejecutamos el comando de construcción `./gradlew build`. Este comando va a descargar las dependencias y construirá el archivo JAR.
- Para la segunda capa también se usará `openjdk:17-jdk-slim` como imagen base.
- Después se define `/app` como el directorio de trabajo.
- Se copiará de la capa `builder` el JAR resultante de la compilación.
- Exponemos el puerto `8080`.
- Y finalmente especificamos cuál será el comando que se deberá ejecutar al momento de lanzar un contenedor con la imagen resultante.

## Construcción de la imagen con la aplicación de Kotlin

Para construir la imagen de la aplicación Kotlin ejecutaremos el siguiente comando:

```bash
docker build -t unir:4 -f Dockerfile.app4 .

[+] Building 65.4s (11/11) FINISHED                                                                   docker:default
 => [internal] load build definition from Dockerfile.app4                                                       0.1s
 => => transferring dockerfile: 629B                                                                            0.0s
 => [internal] load .dockerignore                                                                               0.0s
 => => transferring context: 115B                                                                               0.0s
 => [internal] load metadata for docker.io/library/openjdk:17-jdk-slim                                          1.3s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                  0.0s
 => [internal] load build context                                                                               0.1s
 => => transferring context: 25.90MB                                                                            0.1s
 => [builder 1/4] FROM docker.io/library/openjdk:17-jdk-slim@sha256:aaa3b3cb27e3e520b8f116863d0580c438ed55ecfa  0.0s
 => CACHED [builder 2/4] WORKDIR /app                                                                           0.0s
 => [builder 3/4] COPY app4/ .                                                                                  0.1s
 => [builder 4/4] RUN ./gradlew build                                                                          63.4s
 => [stage-1 3/3] COPY --from=builder /app/build/libs/app4-0.0.1-SNAPSHOT.jar service.jar                       0.1s 
 => exporting to image                                                                                          0.1s 
 => => exporting layers                                                                                         0.1s 
 => => writing image sha256:e2ed58006ca35ccf9485765fe38e56cadc573e98a3bbb01b9d809f62a027825b                    0.0s 
 => => naming to docker.io/library/unir:4                                                                       0.0s
```

Este comando va a construir la imegen usando el contenido de la carpeta `app4`. Al finalizar tendremos una imagen llamda `unir:4`:

```bash
docker images

REPOSITORY                                          TAG           IMAGE ID       CREATED             SIZE
unir                                                4             e2ed58006ca3   51 seconds ago      432MB
```

## Levantar un contenedor usando la imagen de Kotlin

Para lanzar un contenedor usando la imagen de Kotlin usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 unir:4
764007f39867fd18e657bfcefe9d794d3cd1c7399c188146cb46f8bba1ea462b
```

Este comando `docker` hace lo siguiente:

- Le pasamos el comando `run` para que cree un contenedor y lo ejecute.
- La bandera `--rm` le indica a Docker que debe borrar el contenedor una vez que se termine la ejecución del mismo.
- La bandera `-d` nos permite liberar la consola después de ejecutar el comando `docker`. Por eso vemos que al ejecutar el comando nos devuelve un hash, ese es el identificador del contenedor. Si no le pasamos esta bandera, entonces la consola se quedará bloqueada mostrándonos la salida de los logs del contenedor.
- Con la bandera `-p 8080:8080` le indicamos a docker que le asigne el puerto `8080` en el host, y que internamente el contenedor está usando también el puerto `8080`. Esto hará que las peticiones que se le hagan al host (localhost) en el puerto `8080` serán redirigidas al puerto `8080` del contenedor.
- Finalmente le pasamos la imagen que queremos usar, en este caso será la imagen que compilamos para la aplicación Kotlin, la cual llamamos `unir:4`.

## Probar el contenedor con la aplicación Kotlin

Para probar la aplicación Kotlin vamos a usar el comando `curl`:

```bash
curl localhost:8080
```

Si todo funciona adecuadamente veremos un mensaje en formato JSON como el siguiente:

```json
{"alumno":"Carlos Colón","universidad":"UNIR","mensaje":"Hello from Kotlin API!","maestria":"Desarrollo y Operaciones de Software (DevOps)","materia":"Contenedores"}
```

Para terminar el contenedor ejecutaremos el siguiente comando:

```bash
docker container kill 764007f39867fd18e657bfcefe9d794d3cd1c7399c188146cb46f8bba1ea462b
```

El hash que se le pasa a este comando es el mismo que nos arrojó el comando `docker run`.

## Subir la imagen al repositorio

Para subir la imagen `unir:4` al repositorio público, primero hay que loguearse al repositorio:

```bash
echo $DOCKER_HUB_CPPMX | docker login -u cppmx --password-stdin

WARNING! Your password will be stored unencrypted in /home/ccolon/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
```

La variable de ambiente `DOCKER_HUB_CPPMX` yo la defino con el token de acceso a mi cuenta de Docker Hub.

El siguiente paso es crear un tag con el nombre del repositorio a donde será entregada la imagen, junto con un identificador:

```bash
docker tag unir:4 cppmx/unir:kotlin
```

Y finalmente enviamos la imagen al repositorio:

```bash
docker push cppmx/unir:kotlin

The push refers to repository [docker.io/cppmx/unir]
4e52a399b8b1: Pushed 
46468f15bb67: Pushed 
6be690267e47: Mounted from library/openjdk 
13a34b6fff78: Mounted from library/openjdk 
9c1b6dd6c1e6: Mounted from library/openjdk 
kotlin: digest: sha256:805f12768a49750db93425aba31974cf126294097d064c5f0de24c0e8cdaad3d size: 1372
```

Finalmente se cierra la sesión del repositorio.

```bash
docker logout

Removing login credentials for https://index.docker.io/v1/
```
