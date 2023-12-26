# Jenkins

Para la imagen de Jenkins voy a usar una imagen de las que ya existen en el Docker Hub y la voy a personalizar.
Realmente no hace falta hacer mucho para hacer funcionar Jenkins en Docker pues el proyecto entero ya ha sido contenerizado.
Sin embargo, hay algunas configuraciones iniciales que se pueden hacer en la imagen para que al arrancar un contenedor este funcione sin tener que pasar por el wizard inicial.

El archivo `Dockerfile.jenkins` contendrá lo siguiente:

```bash
# Utilizamos la imagen oficial de Jenkins
FROM jenkins/jenkins:2.437-jdk17

ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# Omitimos el Wizard inicial
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# Cambiamos al usuario root para realizar instalaciones adicionales
USER root

# Creamos un archivo con los plugins que deseamos instalar
RUN cat <<EOF >> /usr/share/jenkins/plugins.txt
LISTA de Plugins que se desean instalar
EOF

    # Instalamos los plugins
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/plugins.txt && \
    # Actualizamos el sistema
    apt-get update && \
    # Agregamos el repositorio de Docker
    apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - &&\
    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/debian \
    $(lsb_release -cs) \
    stable"

    # Volvemos a ejecutar el comadno de actualización para que considere el nuevo repositorio
RUN apt-get update  -qq \
    # Ahora instalamos docker
    && apt-get install docker-ce -y && \
    # Agregamos al usuario jenkins al grupo de Docker para que pueda ejecutar el comando docker
    usermod -aG docker jenkins && \
    # Limipamos la cache
    apt-get clean && \
    # Instalamos el comando docker-compose
    curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose

# Cambiamos al usuario jenkins
USER jenkins
```

- La base para este Dockerfile será `jenkins/jenkins:2.437-jdk17`.
- Después se definen dos variables de ambiente: `JENKINS_USER` y `JENKINS_PASS`. Estas variables nos servirán para definir las credenciales de acceso iniciales de Jenkins.
- Con otra variable de ambiente llamada `JAVA_OPTS` establecemos un valor para que Jenkins no nos muestre el Wizard inicial.
- Habilitamos al usuario root. A partir de este momento, todos los comandos que se ejecuten se harán como super usuario.
- Se creará un archivo llamado `/usr/share/jenkins/plugins.txt` que contendrá una lista de todos los plugins que deseamos instalar de inicio.
- Después, se instalarán los plugins de la lista anterior. También se actualizará el sistema y se instalará el repositorio de Docker dentro de la distribución base.
- En una segunda capa volveremos a actualizar el sistema, esto para asegurarnos de que el repositorio de Docker que se agregó en el paso anterior no tenga errores, también se instalará Docker, se limpiará el cache, y como último paso se instalará el comando `docker-compose`.
- Finalmente, se establecerá el usuario `jenkins` como usuario predeterminado. Esto hará que los contenedores que se lancen con esta imagen usen este usuario por default.

## Compilación de la imagen con Jenkins

Para compilar la imagen con Jenkins usaremos el siguiente comando:

```bash
$ docker build -t unir:jenkins -f Dockerfile.jenkins .

[+] Building 66.6s (8/8) FINISHED                                                                        docker:default
 => [internal] load build definition from Dockerfile.jenkins                                                       0.1s
 => => transferring dockerfile: 2.00kB                                                                             0.0s
 => [internal] load .dockerignore                                                                                  0.0s
 => => transferring context: 2B                                                                                    0.0s
 => [internal] load metadata for docker.io/jenkins/jenkins:2.437-jdk17                                             2.3s
 => [1/4] FROM docker.io/jenkins/jenkins:2.437-jdk17@sha256:61d7ddb0ed9ceac46defa35d620842dc53f35c97332189fff69d  10.3s
 => [2/4] RUN cat <<EOF >> /usr/share/jenkins/plugins.txt                                                          0.7s
 => [3/4] RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/plugins.txt &&     apt-get update &&     apt-g  36.6s
 => [4/4] RUN apt-get update  -qq     && apt-get install docker-ce -y &&     usermod -aG docker jenkins &&     a  15.1s 
 => exporting to image                                                                                             1.5s 
 => => exporting layers                                                                                            1.5s 
 => => writing image sha256:40d493711042e20dd091adb2a8e4b4ec95f0899867cf260ee82d3bc2ac504720                       0.0s 
 => => naming to docker.io/library/unir:jenkins                                                                    0.0s
```

Podemos ver la imagen en el repositorio local con el siguiente comando:

```bash
$ docker images

REPOSITORY              TAG           IMAGE ID       CREATED             SIZE
unir                    jenkins       40d493711042   17 minutes ago      1.27GB
```

## Lanzar un contenedor

Para lanzar un contenedor con la imagen de Jenkins usaremos el siguiente comando:

```bash
$ docker run --rm -d -p 8080:8080 unir:jenkins

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

- El comando `run` le dice a docker que cree y ejecute un contenedor nuevo.
- Con `--rm` le indicamos a docker que borre el contenedor cuando terminemos de usarlo.
- Con `-d` le indicamos a docker que libere la consola. Si no le pasamos esta bandera, entonces la consola donde ejecutemos este comando se quedará bloqueada desplegando líneas del log del proceso que se está ejecutando.
- Con `-p 8080:8080` le indicamos a docker que haga un mapeo de puertos. El primer valor `8080` es el puerto del host, y el segundo `8080` es el puerto del contenedor.
- Finalmente, le decimos cuál imagen usar para crear el contenedor. En este caso es el nombre de la imagen que acabamos de compilar: `unir:jenkins`.

El hash que muestra es el identificador del contenedor que se está ejecutando.
El comando anterior es suficiente para poder ejecutar Jenkins, pero no se podrá usar Docker dentro del contenedor aunque lo hayamos instalado durante la construcción de la imagen. Si queremos habilitar el uso de docker dentro del contenedor entonces usaremos el siguiente comando:

```bash
$ docker run --rm -d -v /var/run/docker.sock:/var/run/docker.sock
-v $(which docker):/usr/bin/docker -p 8080:8080 unir:jenkins

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

Se agregaron los siguientes dos parámetros:

- `-v /var/run/docker.sock:/var/run/docker.sock`: Aquí estamos haciendo uso de la bandera para montar volúmenes, pero en lugar de montar un volumen haremos un mapeo del socket del demonio de docker del host. Para poder usar docker necesitamos que el demonio (o servicio) esté en ejecución, pero no podemos levantar el demonio dentro del contenedor, así que usaremos el demonio local dentro del contenedor.
- `-v $(which docker):/usr/bin/docker`: Al igual que en el parámetro anterior, en lugar de fomentar un volumen haremos un mapeo del ejecutable de docker local y lo pondremos a disposición del contenedor.

Con estos dos parámetros ahora podremos usar Docker dentro del contenedor. Y en el caso de Jenkins, este podrá usar docker en los pipelines que se creen ahí.

Jenkins funciona desde un sitio web, así que podremos verlo en un navegador accediendo a la URL: <http://localhost:8080/>.

Pra terminar el contenedor ejecutaremos el siguiente comando:

```bash
$ docker kill 3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3

3ab24806d99192801b99acf792e867f79d492c805eb291fe5344f3eb2c06cef3
```

## Subir la imagen al repositorio público

Para subir la imagen `unir:jenkins` al repositorio público, primero hay que loguearse al repositorio:

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
docker tag unir:jenkins cppmx/unir:jenkins
```

Y finalmente enviamos la imagen al repositorio:

```bash
$ docker push cppmx/unir:jenkins

The push refers to repository [docker.io/cppmx/unir]
199ffa22b2b6: Pushed 
676dc265cba7: Pushed 
c5cfd9828e9e: Pushed 
9d2ac2c0ff8a: Mounted from jenkins/jenkins 
332e9e229ff6: Mounted from jenkins/jenkins 
09837bbd5cf0: Mounted from jenkins/jenkins 
41845bfe3217: Mounted from jenkins/jenkins 
e8ebf2f61f7e: Mounted from jenkins/jenkins 
d67698bd652f: Mounted from jenkins/jenkins 
4e4120310583: Mounted from jenkins/jenkins 
cbafdbf1168c: Mounted from jenkins/jenkins 
c60e22b48c1d: Mounted from jenkins/jenkins 
d3e2992bd59c: Mounted from jenkins/jenkins 
c01409bca979: Mounted from jenkins/jenkins 
7cea17427f83: Mounted from jenkins/jenkins 
jenkins: digest: sha256:33fb6b768f30e012a96cc149f5db3ffb6455a1d5456acc42e8d6fff7945adfb6 size: 3466
```

Finalmente se cierra la sesión del repositorio.

```bash
$ docker logout

Removing login credentials for https://index.docker.io/v1/
```
