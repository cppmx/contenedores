# Una aplicación REST API sencilla usando PHP

El Dockerfile para construir la aplicación de PHP se llama `Dockerfile.app2` y contiene lo siguiente:

```bash
# Usa una imagen de PHP con Apache
FROM php:7.4-apache

# Copia el código de la aplicación al contenedor
COPY app2/index.php /var/www/html/

# Configura Apache para escuchar en el puerto 8080
RUN sed -i 's/80/8080/g' /etc/apache2/sites-available/000-default.conf /etc/apache2/ports.conf

# Exponer el puerto 8080
EXPOSE 8080

# Comando para iniciar el servidor web de Apache
CMD ["apache2-foreground"]
```

- Usaré como base una imagen de Apache que tenga el módulo PHP, en este caso será `php:7.4-apache`. El script de PHP necesita un servidor Web para poder ejecutarse, Apache tiene un módulo de PHP que permite la ejecución de scripts PHP.
- El script de PHP se encuentra dentro de la carpeta `app2` y se llama `index.php`. Copiamos ese script al contenedor en la ruta `/var/www/html/` para que el demonio de Apache lo pueda localizar.
- Por default, Apache escucha peticiones en el puerto `80`, así que usando el comando `sed` modificamos la configuración de Apache para que escuche peticiones en el puerto `8080`.
- Exponemos el puerto `8080`.
- Y finalmente especificamos cuál será el comando que se deberá ejecutar al momento de lanzar un contenedor con la imagen resultante.

## Construcción de la imagen con la aplicación de PHP

Para construir la imagen de la aplicación PHP ejecutaremos el siguiente comando:

```bash
docker build -t unir:2 -f Dockerfile.app2 .


[+] Building 2.8s (9/9) FINISHED                                                                      docker:default
=> [internal] load build definition from Dockerfile.app2                                                       0.0s
=> => transferring dockerfile: 507B                                                                            0.0s
=> [internal] load .dockerignore                                                                               0.1s
=> => transferring context: 115B                                                                               0.0s
=> [internal] load metadata for docker.io/library/php:7.4-apache                                               2.2s
=> [auth] library/php:pull token for registry-1.docker.io                                                      0.0s
=> [internal] load build context                                                                               0.0s
=> => transferring context: 327B                                                                               0.0s
=> CACHED [1/3] FROM docker.io/library/php:7.4-apache@sha256:c9d7e608f73832673479770d66aacc8100011ec751d1905f  0.0s
=> [2/3] COPY app2/index.php /var/www/html/                                                                    0.1s
=> [3/3] RUN sed -i 's/80/8080/g' /etc/apache2/sites-available/000-default.conf /etc/apache2/ports.conf        0.3s
=> exporting to image                                                                                          0.0s
=> => exporting layers                                                                                         0.0s
=> => writing image sha256:e661aa9286a84caf191520044b14da49ff559fd08f79182198baaa905a2fe2ee                    0.0s
=> => naming to docker.io/library/unir:2
```

Este comando va a construir la imagen usando el contenido de la carpeta `app2`. Al finalizar tendremos una imagen llamada `unir:2`:

```bash
docker images

REPOSITORY                                          TAG           IMAGE ID       CREATED          SIZE
unir                                                2             e661aa9286a8   3 minutes ago    453MB
```

## Levantar un contenedor usando la imagen de PHP

Para lanzar un conetendor usando la imagen de PHP usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 unir:2
f81d6435d2b70d6bf88677e397a2015a920ac1a911ba4b22b9a97592e111c724
```

Este comando `docker` hace lo siguiente:

- Le pasamos el comando `run` para que cree un contenedor y lo ejecute.
- La bandera `--rm` le indica a Docker que debe borrar el contenedor una vez que se termine la ejecución del mismo.
- La bandera `-d` nos permite liberar la consola después de ejecutar el comando `docker`. Por eso vemos que al ejecutar el comando nos devuelve un hash, ese es el identificador del contenedor. Si no le pasamos esta bandera, entonces la consola se quedará bloqueada mostrándonos la salida de los logs del contenedor.
- Con la bandera `-p 8080:8080` le indicamos a docker que le asigne el puerto `8080` en el host, y que internamente el contenedor está usando también el puerto `8080`. Esto hará que las peticiones que se le hagan al host (localhost) en el puerto `8080` serán redirigidas al puerto `8080` del contenedor.
- Finalmente le pasamos la imagen que queremos usar, en este caso será la imagen que compilamos para la aplicación PHP, la cual llamamos `unir:2`.

## Probar el contenedor con la aplicación PHP

Para probar la aplicación PHP vamos a usar el comando `curl`:

```bash
curl localhost:8080
```

Si todo funciona adecuadamente veremos un mensaje en formato JSON como el siguiente:

```json
{"alumno":"Carlos Colón","universidad":"UNIR","mensaje":"Hello from PHP API!","maestria":"Desarrollo y Operaciones de Software (DevOps)","materia":"Contenedores"}
```

Para terminar el contenedor ejecutaremos el siguiente comando:

```bash
docker container kill f81d6435d2b70d6bf88677e397a2015a920ac1a911ba4b22b9a97592e111c724
```

El hash que se le pasa a este comando es el mismo que nos arrojó el comando `docker run`.

## Subir la imagen al repositorio

Para subir la imagen `unir:2` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:2 cppmx/unir:php
```

Y finalmente enviamos la imagen al repositorio:

```bash
docker push cppmx/unir:php

The push refers to repository [docker.io/cppmx/unir]
1d55bd490c9a: Pushed
d922e51c457c: Pushed
3d33242bf117: Mounted from library/php
529016396883: Mounted from library/php
5464bcc3f1c2: Mounted from library/php
28192e867e79: Mounted from library/php
d173e78df32e: Mounted from library/php
0be1ec4fbfdc: Mounted from library/php
30fa0c430434: Mounted from library/php
a538c5a6e4e0: Mounted from library/php
e5d40f64dcb4: Mounted from library/php
44148371c697: Mounted from library/php
797a7c0590e0: Mounted from library/php
f60117696410: Mounted from library/php
ec4a38999118: Mounted from library/php
php: digest: sha256:637aaa8811f5ba2b0ca05257926fbe91cff7672b0920a52584b57c411b76386b size: 3450
```

Finalmente se cierra la sesión del repositorio.

```bash
docker logout

Removing login credentials for https://index.docker.io/v1/
```
