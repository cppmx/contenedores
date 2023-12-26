# Contenedores

He realizado este proyecto para cumplir con la tarea del segundo semestre de la materia de Contenedores de la maestría en Desarrollo y Operaciones de Software de UNIR.

La tarea consiste en la creación de cuatro imágenes de Docker para cuatro proyectos que usen diferentes lenguajes de programación.
Las imágenes que crearé serán las siguientes:

1. [Jenkins](JENKINS.md) - Se trata de un servidor de automatización de código abierto que permite a los desarrolladores de todo el mundo crear, probar e implementar su software de manera confiable. Esta aplicación ha sido desarrollada principalmente en Ruby, Python y Groovy.
2. [NetData](NETDATA.md) - es una plataforma distribuida de monitoreo del estado en tiempo real para sistemas, hardware, contenedores y aplicaciones, que recopila métricas. El desarrollo de esta aplicación ha sido usando C y Python.
3. [Animación WebGL](WEBGL.md) - Se trata de una animación interactiva desarrollada en GLSL y WebGL.
4. [Wordpress](WORDPRESS.md) - Es un software de código abierto que se puede utilizar para crear fácilmente un sitio web, blog o aplicación. Fue desarrollada usando el lenguaje de programación PHP.

## Imágenes compartidas en un repositorio público

Las imágenes creadas de cada uno de los respectivos archivos Dockerfile fueron subidas a un repositorio público en Docker Hub. A continuación se explica cómo acceder a ellas.

### La imagen de Jenkins

Para descargar la imagen de Jenkins usaremos el siguiente comando:

```bash
docker pull cppmx/unir:jenkins
```

Para lanzar un contenedor con esta imagen usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 --name jenkins cppmx/unir:jenkins
```

Podremos ver la aplicación de Jenkins en un navegador accediendo a la siguiente URL: <http://localhost:8080/>

Para detener y eliminar el contenedor de Jenkins usaremos el comando:

```bash
docker kill jenkins
```

### La imagen de NetData

Para descargar la imagen de NetData usaremos el siguiente comando:

```bash
docker pull cppmx/unir:netdata
```

Para lanzar un contenedor con esta imagen usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:19999 --name netdata cppmx/unir:netdata
```

Podremos ver la aplicación de NetData en un navegador accediendo a la siguiente URL: <http://localhost:8080/>

Para detener y eliminar el contenedor de Jenkins usaremos el comando:

```bash
docker kill netdata
```

### La imagen de WebGL

Para descargar la imagen con la animación de WebGL usaremos el siguiente comando:

```bash
docker pull cppmx/unir:webgl
```

Para lanzar un contenedor con esta imagen usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:80 --name webgl cppmx/unir:webgl
```

Podremos ver la animación WebGL en un navegador accediendo a la siguiente URL: <http://localhost:8080/>. Haz click en alguna zona de la pantalla y arrastra el mouse para jugar con la animación.

Para detener y eliminar el contenedor de Jenkins usaremos el comando:

```bash
docker kill webgl
```

### La imagen de Wordpress

Para descargar la imagen de Wordpress usaremos el siguiente comando:

```bash
docker pull cppmx/unir:wordpress
```

Para lanzar un contenedor con esta imagen usaremos el siguiente comando:

```bash
docker run --rm -d -p 8080:8080 --name wordpress cppmx/unir:wordpress
```

Podremos ver la aplicación de Wordpress en un navegador accediendo a la siguiente URL: <http://localhost:8080/>. Deberán de poder ver un primer post como se muestra en la siguiente imagen:

Para detener y eliminar el contenedor de Jenkins usaremos el comando:

```bash
docker kill wordpress
```
