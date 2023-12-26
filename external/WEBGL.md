# Applicación WebGL

Para esta imagen voy a usar el proyecto llamado WebGL Fluid Simulation el cual puede ser consultado en el siguiente repositorio: <https://github.com/PavelDoGreat/WebGL-Fluid-Simulation/>

Este proyecto usa GLSL como lenguaje de programación y se apoya de WebGL para hacer el renderizado. A continuación una breve explicación de que es cada una de estas tecnologías.

1. GLSL (OpenGL Shading Language):

    - **Definición**: GLSL es un lenguaje de programación de alto nivel diseñado para escribir shaders, que son pequeños programas utilizados para controlar la forma en que los gráficos son procesados en hardware de GPU (Unidad de Procesamiento Gráfico).
    - **Uso**: Se utiliza principalmente en conjunto con la API OpenGL, que es una interfaz de programación de aplicaciones para gráficos 3D. GLSL permite a los desarrolladores crear efectos visuales avanzados, realizar cálculos complejos en la GPU y manipular la apariencia de los objetos en la pantalla.

2. WebGL (Web Graphics Library):

    - **Definición**: WebGL es una tecnología basada en OpenGL que permite la representación de gráficos 3D en un navegador web sin necesidad de complementos adicionales. WebGL es una especificación web mantenida por el Grupo de Trabajo de WebGL de la Fundación Khronos.
    - **Uso**: Permite a los desarrolladores utilizar GLSL para escribir shaders y crear gráficos 3D interactivos directamente en páginas web. WebGL está integrado en la mayoría de los navegadores web modernos, lo que significa que no se requieren complementos adicionales para visualizar contenido basado en WebGL.

Para poder ejecutar una aplicación basada en estas tecnologías sólo hace falta un servidor web como Apache o Nginx, y poner los archivos en la carpeta correcta para que el servidor web pueda encontrarlos. El archivo `Dockerfile.webgl` será muy sencillo, su contenido será el siguiente:

```bash
# Utilizamos una imagen ligera de Nginx como base
FROM nginx:alpine

# Instalamos Git
RUN apk update && apk --no-cache add git

    # Primero vamos a borrar cualquier el contendio predeterminado de la carpeta html
RUN rm -fr /usr/share/nginx/html && \
    # Luego clonamos el repositorio
    git clone https://github.com/PavelDoGreat/WebGL-Fluid-Simulation.git /usr/share/nginx/html

# Exponemos el puerto en el que Nginx escucha (puerto por defecto: 80)
EXPOSE 80
```

- La base de este Dockerfile será `nginx:alpine`.
- Se actualiza el sistema base y se instala la herramienta `git`.
- En una sola corrida eliminamos el contenido de la carpeta `/usr/share/nginx/html`, y en su lugar clonamos el repositorio del proyecto WebGL. Esto hará que la carpeta `/usr/share/nginx/html` contenga los archivos del proyecto.
- Finalmente exponemos el puerto `80`. Este es el puerto predeterminado de las imágenes de Nginx.

Debido a que se está usando una imagen Nginx como base, no es necesario ejecutar ningún comando. Las imágenes Nginx lanzan automáticamente el servicio en cuanto se crea un contenedor.

## Compilación de la imagen WebGL

Para compilar la imagen WebGL usaremos el siguiente comando:

```bash
$ docker build -t unir:webgl -f Dockerfile.webgl .

[+] Building 1.5s (8/8) FINISHED                                                                         docker:default
 => [internal] load build definition from Dockerfile.webgl                                                       0.1s
 => => transferring dockerfile: 808B                                                                               0.0s
 => [internal] load .dockerignore                                                                                  0.1s
 => => transferring context: 2B                                                                                    0.0s
 => [internal] load metadata for docker.io/library/alpine:latest                                                   1.4s
 => [1/4] FROM docker.io/library/alpine:latest@sha256:51b67269f354137895d43f3b3d810bfacd3945438e94dc5ac55fdac3403  0.0s
 => CACHED [2/4] RUN apk update && apk add --no-cache curl jq bash                                                 0.0s
 => CACHED [3/4] RUN curl -Ss "https://raw.githubusercontent.com/webgl/webgl/master/packaging/installer/insta  0.0s
 => CACHED [4/4] RUN curl -Ss "https://raw.githubusercontent.com/webgl/webgl/master/packaging/installer/kicks  0.0s
 => exporting to image                                                                                             0.0s
 => => exporting layers                                                                                            0.0s
 => => writing image sha256:4b2f8f8f91ea24444dc4e252f741d5774aa766c1479a39d0e952226b37698620                       0.0s
 => => naming to docker.io/library/unir:webgl                                                                    0.0s
```

Podemos ver la imagen en el repositorio local con el siguiente comando:

```bash
$ docker images

REPOSITORY                     TAG           IMAGE ID       CREATED          SIZE
unir                           webgl       4b2f8f8f91ea   32 minutes ago   572MB
```

## Lanzar un contenedor

Para lanzar un contenedor con la imagen de NetData usaremos el siguiente comando:

```bash
$ docker run --rm -d -p 8080:80 unir:webgl

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

- El comando `run` le dice a docker que cree y ejecute un contenedor nuevo.
- Con `--rm` le indicamos a docker que borre el contenedor cuando terminemos de usarlo.
- Con `-d` le indicamos a docker que libere la consola. Si no le pasamos esta bandera, entonces la consola donde ejecutemos este comando se quedará bloqueada desplegando líneas del log del proceso que se está ejecutando.
- Con `-p 8080:80` le indicamos a docker que haga un mapeo de puertos. El valor `8080` es el puerto del host, y `80` es el puerto del contenedor.
- Finalmente, le decimos cuál imagen usar para crear el contenedor. En este caso es el nombre de la imagen que acabamos de compilar: `unir:webgl`.

El hash que muestra es el identificador del contenedor que se está ejecutando. Podemos ver que el contenedor está en ejecución con el siguiente comando:

```bash
$ docker ps

CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                         NAMES
3ab24806d991   unir:webgl   "/opt/webgl/bin/ne…"   6 seconds ago   Up 5 seconds   0.0.0.0:8080->19999/tcp, :::8080->19999/tcp   eloquent_galois
```

En este caso, la animación se puede ver en un sitio web, así que accedemos desde un navegador a la URL: [http://localhost:8080/](http://localhost:8080/).

Pra terminar el contenedor ejecutaremos el siguiente comando:

```bash
$ docker kill 3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

## Subir la imagen al repositorio público

Para subir la imagen `unir:webgl` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:webgl cppmx/unir:webgl
```

Y finalmente enviamos la imagen al repositorio:

```bash
$ docker push cppmx/unir:webgl

The push refers to repository [docker.io/cppmx/unir]
3ab4ef3b587c: Pushed 
2cd2b2d7be85: Pushed 
2d33248ce9da: Mounted from library/nginx 
9770295d804c: Mounted from library/nginx 
62e59aa00d24: Mounted from library/nginx 
769b844042ad: Mounted from library/nginx 
4c6b2fc6378f: Mounted from library/nginx 
c612d245f985: Mounted from library/nginx 
3d49ee199a5c: Mounted from library/nginx 
9fe9a137fd00: Mounted from library/nginx 
webgl: digest: sha256:666d194ecb457a4ab76b1c613d3c032f3ddf28ad1125385d9fb12a6d9b8219e8 size: 2411
```

Finalmente se cierra la sesión del repositorio.

```bash
$ docker logout

Removing login credentials for https://index.docker.io/v1/
```
