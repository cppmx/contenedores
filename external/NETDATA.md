# NetData

Se trata de una herramienta de monitoreo de sistemas y aplicaciones de código abierto, diseñada para proporcionar una visión detallada y en tiempo real del rendimiento de servidores, sistemas operativos y aplicaciones. Se destaca por su capacidad para ofrecer métricas de manera extremadamente rápida, con un enfoque en la simplicidad y la facilidad de uso.

Algunas características clave de Netdata incluyen:

- **Monitoreo en tiempo real**: Netdata proporciona métricas en tiempo real, lo que significa que puedes ver el rendimiento actual de tu sistema con actualizaciones continuas y detalladas.
- **Visualización intuitiva**: Utiliza una interfaz web interactiva y altamente visual que permite observar el rendimiento a nivel granular con gráficos detallados y paneles informativos.
- **Soporte para numerosos sistemas operativos y aplicaciones**: Netdata es compatible con una amplia variedad de sistemas operativos, incluyendo Linux, FreeBSD y macOS, así como con varias aplicaciones y servicios, lo que lo convierte en una herramienta versátil.
- **Alertas y notificaciones**: Puedes configurar alertas basadas en umbrales para ser notificado cuando el rendimiento del sistema se desvíe de lo esperado.
- **Bajo consumo de recursos**: Netdata está diseñado para consumir recursos mínimos y tener un impacto insignificante en el rendimiento del sistema que está monitoreando.

Netdata se utiliza comúnmente en entornos de servidores y sistemas distribuidos para realizar un seguimiento del rendimiento de servidores web, bases de datos, servicios en la nube y otros componentes de infraestructuras tecnológicas. Proporciona información valiosa para administradores de sistemas, desarrolladores y cualquier persona interesada en optimizar y comprender el comportamiento de sus sistemas en tiempo real.

El código fuente de esta aplicación lo podemos encontrar en el siguiente repositorio: <https://github.com/netdata/netdata/>

Ya existen imágenes en el repositorio público de Docker Hub con la aplicación de NetData, pero para fines prácticos en esta actividad voy a crear un Dockerfile donde se ejecutarán los scripts de instalación de la aplicación, el contenido del archivo `Dockerfile.netdata` será el siguiente:

```bash
# Utilizamos la imagen base de Alpine Linux
FROM alpine:3.19.0

# Instalamos dependencias necesarias para Netdata
RUN apk update && apk add --no-cache curl jq bash

# Descargamos e instalamos Netdata
RUN curl -Ss "https://raw.githubusercontent.com/netdata/netdata/master/packaging/installer/install-required-packages.sh" >/tmp/install-required-packages.sh && bash /tmp/install-required-packages.sh -y
RUN curl -Ss "https://raw.githubusercontent.com/netdata/netdata/master/packaging/installer/kickstart.sh" >/tmp/installer.sh && bash /tmp/installer.sh --non-interactive

# Exponemos el puerto en el que Netdata escucha
EXPOSE 19999

# Comando para iniciar Netdata
CMD ["/opt/netdata/bin/netdata", "-D"]
```

- La base para este Dockerfile será `alpine:3.19.0`.
- El siguiente paso será la actualización del sistema alpine, y la instalación de las herramientas `curl`, `bash` y `jq`. La aplicación `curl` nos servirá para descargar los scripts de instalación de NetData, estos scripts fueron creados para ser ejecutados en `bash`, y utilizan `jq` para hacer el parseo de varias sentencias, es por eso que es necesario tener estas aplicaciones instaladas.
- Primero descargamos y ejecutamos el script `install-required-packages.sh`.
- Luego hacemos lo mismo con el script `kickstart.sh`.
- Después exponemos el puerto predeterminado de NetData el cual es `19999`.
- Y finalmente arrancamos NetData como demonio.

## Compilación de la imagen con NetData

Para compilar la imagen con NetData usaremos el siguiente comando:

```bash
$ docker build -t unir:netdata -f Dockerfile.netdata .

[+] Building 1.5s (8/8) FINISHED                                                                         docker:default
 => [internal] load build definition from Dockerfile.netdata                                                       0.1s
 => => transferring dockerfile: 808B                                                                               0.0s
 => [internal] load .dockerignore                                                                                  0.1s
 => => transferring context: 2B                                                                                    0.0s
 => [internal] load metadata for docker.io/library/alpine:latest                                                   1.4s
 => [1/4] FROM docker.io/library/alpine:latest@sha256:51b67269f354137895d43f3b3d810bfacd3945438e94dc5ac55fdac3403  0.0s
 => CACHED [2/4] RUN apk update && apk add --no-cache curl jq bash                                                 0.0s
 => CACHED [3/4] RUN curl -Ss "https://raw.githubusercontent.com/netdata/netdata/master/packaging/installer/insta  0.0s
 => CACHED [4/4] RUN curl -Ss "https://raw.githubusercontent.com/netdata/netdata/master/packaging/installer/kicks  0.0s
 => exporting to image                                                                                             0.0s
 => => exporting layers                                                                                            0.0s
 => => writing image sha256:4b2f8f8f91ea24444dc4e252f741d5774aa766c1479a39d0e952226b37698620                       0.0s
 => => naming to docker.io/library/unir:netdata                                                                    0.0s
```

Podemos ver la imagen en el repositorio local con el siguiente comando:

```bash
$ docker images

REPOSITORY                     TAG           IMAGE ID       CREATED          SIZE
unir                           netdata       4b2f8f8f91ea   32 minutes ago   572MB
```

## Lanzar un contenedor

Para lanzar un contenedor con la imagen de NetData usaremos el siguiente comando:

```bash
$ docker run --rm -d -p 8080:19999 unir:netdata

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

- El comando `run` le dice a docker que cree y ejecute un contenedor nuevo.
- Con `--rm` le indicamos a docker que borre el contenedor cuando terminemos de usarlo.
- Con `-d` le indicamos a docker que libere la consola. Si no le pasamos esta bandera, entonces la consola donde ejecutemos este comando se quedará bloqueada desplegando líneas del log del proceso que se está ejecutando.
- Con `-p 8080:8080` le indicamos a docker que haga un mapeo de puertos. El valor `8080` es el puerto del host, y `19999` es el puerto del contenedor.
- Finalmente, le decimos cuál imagen usar para crear el contenedor. En este caso es el nombre de la imagen que acabamos de compilar: `unir:netdata`.

El hash que muestra es el identificador del contenedor que se está ejecutando. Podemos ver que el contenedor está en ejecución con el siguiente comando:

```bash
$ docker ps

CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                         NAMES
3ab24806d991   unir:netdata   "/opt/netdata/bin/ne…"   6 seconds ago   Up 5 seconds   0.0.0.0:8080->19999/tcp, :::8080->19999/tcp   eloquent_galois
```

En este caso, NetData es un sitio web, así que podremos verlo en un navegador accediendo a la URL: [http://localhost:8080/](http://localhost:8080/).

Pra terminar el contenedor ejecutaremos el siguiente comando:

```bash
$ docker kill 3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

## Subir la imagen al repositorio público

Para subir la imagen `unir:netdata` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:netdata cppmx/unir:netdata
```

Y finalmente enviamos la imagen al repositorio:

```bash
$ docker push cppmx/unir:netdata

The push refers to repository [docker.io/cppmx/unir]
63f646645d9a: Pushed 
33520c0190db: Pushed 
47d8228bf159: Pushed 
5af4f8f59b76: Mounted from library/alpine 
netdata: digest: sha256:63e1efcf7105eaa1f13ce962cedbb4365a96f90f01cd4d123ed48e8ceaf9b762 size: 1164
```

Finalmente se cierra la sesión del repositorio.

```bash
$ docker logout

Removing login credentials for https://index.docker.io/v1/
```
