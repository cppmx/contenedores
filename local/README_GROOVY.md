# Una aplicación REST API sencilla usando Groovy

El Dockerfile para construir la aplicación de Groovy se llama `Dockerfile.app3` y contiene lo siguiente:

```bash
# Etapa 1: Compilar y generar el JAR
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

COPY app3/ .

# Compila y genera el JAR
RUN ./gradlew build

# Etapa 2: Ejecutar la aplicación
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia solo los archivos necesarios de la etapa de compilación
COPY --from=builder /app/build/libs/app3-0.0.1-SNAPSHOT.jar service.jar

# Expone el puerto en el que la aplicación Spring Boot escucha
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "service.jar"]
```

Se necesita compilar el proyecto y generar el JAR que se usará para arrancar el contenedor, así que para este Dockerfile usaré dos capas.

- En la primera capa usaré `openjdk:17-jdk-slim` como imagen base, y nombraré a esta capa `builder`.
- Después se define `/app` como el directorio de trabajo.
- Copiamos el contenido de la carpeta `app3` en el directorio de trabajo.
- Después ejecutamos el comando de construcción `./gradlew build`. Este comando va a descargar las dependencias y construirá el archivo JAR.
- Para la segunda capa también se usará `openjdk:17-jdk-slim` como imagen base.
- Después se define `/app` como el directorio de trabajo.
- Se copiará de la capa `builder` el JAR resultante de la compilación.
- Exponemos el puerto `8080`.
- Y finalmente especificamos cuál será el comando que se deberá ejecutar al momento de lanzar un contenedor con la imagen resultante.

## Construcción de la imagen con la aplicación de Groovy

Para construir la imagen de la aplicación Groovy ejecutaremos el siguiente comando:

```bash
docker build -t unir:3 -f Dockerfile.app3 .

[+] Building 40.8s (10/10) FINISHED                                                                   docker:default
 => [internal] load .dockerignore                                                                               0.1s
 => => transferring context: 115B                                                                               0.0s
 => [internal] load build definition from Dockerfile.app3                                                       0.0s
 => => transferring dockerfile: 629B                                                                            0.0s
 => [internal] load metadata for docker.io/library/openjdk:17-jdk-slim                                          0.7s
 => [internal] load build context                                                                               0.0s
 => => transferring context: 6.32kB                                                                             0.0s
 => [builder 1/4] FROM docker.io/library/openjdk:17-jdk-slim@sha256:aaa3b3cb27e3e520b8f116863d0580c438ed55ecfa  0.0s
 => CACHED [builder 2/4] WORKDIR /app                                                                           0.0s
 => [builder 3/4] COPY app3/ .                                                                                  0.1s
 => [builder 4/4] RUN ./gradlew build                                                                          39.6s
 => [stage-1 3/3] COPY --from=builder /app/build/libs/app3-0.0.1-SNAPSHOT.jar service.jar                       0.1s 
 => exporting to image                                                                                          0.1s 
 => => exporting layers                                                                                         0.1s 
 => => writing image sha256:fe26c257b2608b2f9d53d19a184e6e63a1de85b9bda93a92f965437e5b70c42e                    0.0s 
 => => naming to docker.io/library/unir:3                                                                       0.0s
```

Este comando va a construir la imegen usando el contenido de la carpeta `app3`. Al finalizar tendremos una imagen llamda `unir:3`:

```bash
docker images

REPOSITORY                                          TAG           IMAGE ID       CREATED          SIZE
unir                                                3             fe26c257b260   40 seconds ago   435MB
```

## Levantar un contenedor usando la imagen de Groovy

Para lanzar un contenedor usando la imagen de Groovy usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 unir:3
657f0272a532a03df20b9b2606238f7cb047d4f2a40221522668938387808ea5
```

Este comando `docker` hace lo siguiente:

- Le pasamos el comando `run` para que cree un contenedor y lo ejecute.
- La bandera `--rm` le indica a Docker que debe borrar el contenedor una vez que se termine la ejecución del mismo.
- La bandera `-d` nos permite liberar la consola después de ejecutar el comando `docker`. Por eso vemos que al ejecutar el comando nos devuelve un hash, ese es el identificador del contenedor. Si no le pasamos esta bandera, entonces la consola se quedará bloqueada mostrándonos la salida de los logs del contenedor.
- Con la bandera `-p 8080:8080` le indicamos a docker que le asigne el puerto `8080` en el host, y que internamente el contenedor está usando también el puerto `8080`. Esto hará que las peticiones que se le hagan al host (localhost) en el puerto `8080` serán redirigidas al puerto `8080` del contenedor.
- Finalmente le pasamos la imagen que queremos usar, en este caso será la imagen que compilamos para la aplicación Groovy, la cual llamamos `unir:3`.

## Probar el contenedor con la aplicación Groovy

Para probar la aplicación Groovy vamos a usar el comando `curl`:

```bash
curl localhost:8080
```

Si todo funciona adecuadamente veremos un mensaje en formato JSON como el siguiente:

```json
{"alumno":"Carlos Colón","universidad":"UNIR","mensaje":"Hello from Groovy API!","maestria":"Desarrollo y Operaciones de Software (DevOps)","materia":"Contenedores"}
```

Para terminar el contenedor ejecutaremos el siguiente comando:

```bash
docker container kill 657f0272a532a03df20b9b2606238f7cb047d4f2a40221522668938387808ea5
```

El hash que se le pasa a este comando es el mismo que nos arrojó el comando `docker run`.

## Subir la imagen al repositorio

Para subir la imagen `unir:3` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:3 cppmx/unir:groovy
```

Y finalmente enviamos la imagen al repositorio:

```bash
docker push cppmx/unir:groovy

The push refers to repository [docker.io/cppmx/unir]
4e52a399b8b1: Pushed 
46468f15bb67: Pushed 
6be690267e47: Mounted from library/openjdk 
13a34b6fff78: Mounted from library/openjdk 
9c1b6dd6c1e6: Mounted from library/openjdk 
groovy: digest: sha256:68d71ffa529e8cfda375e7dce2eb2f4caa27a1cf0afd55189ef8cd1ec35d9be5 size: 1372
```

Finalmente se cierra la sesión del repositorio.

```bash
docker logout

Removing login credentials for https://index.docker.io/v1/
```
