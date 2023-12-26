# Wordpress

Para crear una imagen de Docker que contenga Wordpress voy a usar un instalador que configure Wordpress con SQLite. Esto me permitirá ejecutar Wordpress sin la necesidad de tener que instalar un servidor MySQL.

Voy a usar el script de instalación que se encuentra en el siguiente repositorio: <https://github.com/luizbills/create-wordpress-sqlite>

El archivo `Dockerfile.wordpress` contendrá lo siguiente:

```bash
# Utilizamos una imagen base con soporte para PHP y Apache
FROM php:7.4-apache

# Activamos el superusuario
USER root

    # Primero actualizamos el sistema base
RUN apt-get update -y && apt-get upgrade -y && \
    # Luego instalamos las herramientas necesarias para la instalación y configuración de Wordpress
    apt-get install unzip wget -y && \
    # Por seguridad y para reducir el tamaño de la imagen, limpiamos el cache de instalación
    apt-get clean && \
    # Ahopra instalamos extensiones de PHP necesarias para WordPress
    docker-php-ext-install mysqli pdo_mysql

# Definimos el directorio de trabajo
WORKDIR /var/www/html

# Descargamos e instalamos WordPress
RUN wget -q -O - \
    https://raw.githubusercontent.com/luizbills/create-wordpress-sqlite/main/installer \
    | bash && \
    # Configuramos Apache para escuchar en el puerto 8080
    sed -i 's/80/8080/g' /etc/apache2/sites-available/000-default.conf /etc/apache2/ports.conf

# Exponemos el puerto 8080
EXPOSE 8080

# Establecemos el usuario por defecto que usarán los contenedores
USER www-data

# Comando para iniciar Apache
CMD ["apache2-foreground"]
```

- La base para este Dockerfile será `php:7.4-apache`.
- Lo primero será activar el usuario root para poder ejecutar tareas administrativas durante la construcción de la imagen.
- Después, vamos a actualizar el sistema base, e instalaremos una serie de aplicaciones y dependencias de Wordpress.
- Vamos a establecer `/var/www/html` como el directorio de trabajo.
- Usando el comando `wget` descargamos el script de instalación y lo ejecutamos en una consola de `bash`. También cambiaremos el puerto base de Apache, y haremos que se ejecute en el puerto `8080`.
- Exponemos el puerto `8080`, y establecemos el usuario `www-data` como usuario predeterminado.
- Finalmente ejecutamos a Apache como demonio.

## Compilación de la imagen con Wordpress

Para compilar la imagen con Wordpress usaremos el siguiente comando:

```bash
$ docker build -t unir:wordpress -f Dockerfile.wordpress .

[+] Building 35.2s (8/8) FINISHED                                                                        docker:default
 => [internal] load .dockerignore                                                                                  0.1s
 => => transferring context: 2B                                                                                    0.0s
 => [internal] load build definition from Dockerfile.wordpress                                                     0.0s
 => => transferring dockerfile: 1.39kB                                                                             0.0s
 => [internal] load metadata for docker.io/library/php:7.4-apache                                                  0.0s
 => CACHED [1/4] FROM docker.io/library/php:7.4-apache                                                             0.0s
 => [2/4] RUN apt-get update -y && apt-get upgrade -y &&     apt-get install unzip wget -y &&     apt-get clean   27.4s
 => [3/4] WORKDIR /var/www/html                                                                                    0.1s
 => [4/4] RUN wget -q -O -     https://raw.githubusercontent.com/luizbills/create-wordpress-sqlite/main/installer  7.1s 
 => exporting to image                                                                                             0.5s 
 => => exporting layers                                                                                            0.5s 
 => => writing image sha256:49e088b1dda67ccd6d5a29a99a8c34a5d547358cddd3f92bd1da4e6e23b39953                       0.0s 
 => => naming to docker.io/library/unir:wordpress                                                                  0.0s
```

Podemos ver la imagen en el repositorio local con el siguiente comando:

```bash
$ docker images

REPOSITORY              TAG           IMAGE ID       CREATED          SIZE
unir                    wordpress     49e088b1dda6   13 minutes ago   613MB
```

## Lanzar un contenedor

Para lanzar un contenedor con la imagen de Wordpress usaremos el siguiente comando:

```bash
$ docker run --rm -d -p 8080:8080 unir:wordpress

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

- El comando `run` le dice a docker que cree y ejecute un contenedor nuevo.
- Con `--rm` le indicamos a docker que borre el contenedor cuando terminemos de usarlo.
- Con `-d` le indicamos a docker que libere la consola. Si no le pasamos esta bandera, entonces la consola donde ejecutemos este comando se quedará bloqueada desplegando líneas del log del proceso que se está ejecutando.
- Con `-p 8080:8080` le indicamos a docker que haga un mapeo de puertos. El primer valor `8080` es el puerto del host, y el segundo `8080` es el puerto del contenedor.
- Finalmente, le decimos cuál imagen usar para crear el contenedor. En este caso es el nombre de la imagen que acabamos de compilar: `unir:wordpress`.

El hash que muestra es el identificador del contenedor que se está ejecutando. Podemos ver que el contenedor está en ejecución con el siguiente comando:

```bash
$ docker ps

CONTAINER ID   IMAGE            COMMAND                  CREATED         STATUS         PORTS                                               NAMES
3ab24806d991   unir:wordpress   "docker-php-entrypoi…"   7 minutes ago   Up 7 minutes   80/tcp, 0.0.0.0:8080->8080/tcp, :::8080->8080/tcp   beautiful_jennings
```

En este caso, wordpress es un sitio web, así que podremos verlo en un navegador accediendo a la URL: [http://localhost:8080/](http://localhost:8080/).

Pra terminar el contenedor ejecutaremos el siguiente comando:

```bash
$ docker kill 3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

## Subir la imagen al repositorio público

Para subir la imagen `unir:wordpress` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:wordpress cppmx/unir:wordpress
```

Y finalmente enviamos la imagen al repositorio:

```bash
$ docker push cppmx/unir:wordpress

The push refers to repository [docker.io/cppmx/unir]
3b3a61e972a7: Pushed 
cb877451f652: Pushed 
3d33242bf117: Layer already exists 
529016396883: Layer already exists 
5464bcc3f1c2: Layer already exists 
28192e867e79: Layer already exists 
d173e78df32e: Layer already exists 
0be1ec4fbfdc: Layer already exists 
30fa0c430434: Layer already exists 
a538c5a6e4e0: Layer already exists 
e5d40f64dcb4: Layer already exists 
44148371c697: Layer already exists 
797a7c0590e0: Layer already exists 
f60117696410: Layer already exists 
ec4a38999118: Layer already exists 
wordpress: digest: sha256:1886632696298e248922d2c5e339a9ee929aa5b16ecc6a8679ded08b16c29cf5 size: 3665
```

Finalmente se cierra la sesión del repositorio.

```bash
$ docker logout

Removing login credentials for https://index.docker.io/v1/
```
