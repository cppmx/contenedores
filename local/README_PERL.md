# Una aplicación REST API sencilla usando Perl

El Dockerfile para construir la aplicación de Perl se llama `Dockerfile.app1` y contiene lo siguiente:

```bash
# Utiliza una imagen de Perl
FROM perl:5.38.2

# Establece el directorio de trabajo
WORKDIR /app

# Instala Dancer
RUN cpanm Dancer2 JSON

# Copia la aplicación Perl
COPY app1/app.pl .

# Expone el puerto en el que Dancer escucha
EXPOSE 8080

# Comando para ejecutar la aplicación Dancer
CMD ["plackup", "app.pl", "--host", "0.0.0.0", "--port", "8080"]
```

- Usaré como base la imagen `perl:5.38.2`, que al día de hoy es la más reciente.
- Después se define `/app` como el directorio de trabajo.
- Se instalarán las librerías `Dancer2` y `JSON`, las cuales son necesarias para que el script de Perl se ejecute.
- El script de Perl se encuentra dentro de la carpeta `app1` y se llama `app.pl`. Copiamos ese script al contenedor.
- Exponemos el puerto `8080`.
- Y finalmente especificamos cuál será el comando que se deberá ejecutar al momento de lanzar un contenedor con la imagen resultante.

## Construcción de la imagen con la aplicación de Perl

Para construir la imagen de la aplicación Perl ejecutaremos el siguiente comando:

```bash
docker build -t unir:1 -f Dockerfile.app1 .

[+] Building 1.7s (10/10) FINISHED                                                                    docker:default
 => [internal] load build definition from Dockerfile.app1                                                       0.0s
 => => transferring dockerfile: 453B                                                                            0.0s
 => [internal] load .dockerignore                                                                               0.1s
 => => transferring context: 115B                                                                               0.0s
 => [internal] load metadata for docker.io/library/perl:5.38.2                                                  1.4s
 => [auth] library/perl:pull token for registry-1.docker.io                                                     0.0s
 => [1/4] FROM docker.io/library/perl:5.38.2@sha256:838811335044c549cdff01b673919d3613bb9571c576b808d9209649dd  0.0s
 => [internal] load build context                                                                               0.0s
 => => transferring context: 490B                                                                               0.0s
 => CACHED [2/4] WORKDIR /app                                                                                   0.0s
 => CACHED [3/4] RUN cpanm Dancer2                                                                              0.0s
 => [4/4] COPY app1/app.pl .                                                                                    0.1s
 => exporting to image                                                                                          0.0s
 => => exporting layers                                                                                         0.0s
 => => writing image sha256:f8b2657d8550deb428adfb0e3b3cc5785980eaa043810597bd68098b3dad7350                    0.0s
 => => naming to docker.io/library/unir:1 
```

Este comando va a construir la imagen usando el contenido de la carpeta `app1`. Al finalizar tendremos una imagen llamada `unir:1`:

```bash
docker images

REPOSITORY                                          TAG           IMAGE ID       CREATED          SIZE
unir                                                1             f8b2657d8550   31 seconds ago   1.04GB
```

El hash que se le pasa a este comando es el mismo que nos arrojó el comando `docker run`.

## Subir la imagen al repositorio

Para subir la imagen `unir:1` al repositorio público, primero hay que loguearse al repositorio:

```bash
$ echo $DOCKER_HUB_PASSWORD | docker login -u $DOCKER_HUB_USER --password-stdin

WARNING! Your password will be stored unencrypted in /home/ccolon/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
```

La variable de ambiente `DOCKER_HUB_PASSWORD` yo la defino con el token de acceso a mi cuenta de Docker Hub, y la variable de ambiente `DOCKER_HUB_USER` con mi nombre de usuario.

El siguiente paso es crear un tag con el nombre del repositorio a donde será entregada la imagen, junto con un identificador:

```bash
docker tag unir:1 cppmx/unir:perl
```

Y finalmente enviamos la imagen al repositorio:

```bash
docker push cppmx/unir:perl

The push refers to repository [docker.io/cppmx/unir]
6d633dd3d95a: Pushed
54febcbb114d: Pushed
8b53a121965a: Pushed
23615d3f05a4: Mounted from library/perl
1642a88de1e2: Mounted from library/perl
622fd4d922aa: Mounted from library/perl
ac7146fb6cf5: Mounted from library/perl
209de9f22f2f: Mounted from library/perl
777ac9f3cbb2: Mounted from library/perl
ae134c61b154: Mounted from library/perl
perl: digest: sha256:3aa156b5754eb57d94025beba491acfecbbd75cf0b9d62b5e6cdc9491f566cfe size: 2418
```

Finalmente se cierra la sesión del repositorio.

```bash
docker logout

Removing login credentials for https://index.docker.io/v1/
```
