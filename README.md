# crud-mongo-grpc

En este repositorio se encuentra un ejemplo de un servicio grpc en el que se desarrolla un crud con una base de datos mongodb.

---
## Configuración para Maven

### Dependencias
Se requiere agregar las siguientes dependencias al proyecto:

- dependencia para reconocer clases de un **message** creadas por el compilador
```
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.21.5</version>
</dependency>
````
- dependencia para usar utilidades para los objetos **message**:
```
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java-util</artifactId>
    <version>3.21.5</version>
</dependency>
```
- dependencia para reconocer clases de un **service** grpc creadas por el compilador:
```
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.49.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.49.0</version>
</dependency>
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.2</version>
</dependency>
```
- dependencia para levantar al servicio:
```       
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.49.0</version>
</dependency>
```
- dependencia para usar objetos de mongodb:
```
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.2.2</version>
</dependency>
```
- dependencia para poder realizar test unitarios:
```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.1</version>
    <scope>test</scope>
</dependency>
```

### Plugins

Se requieren los siguientes plugins para el buen funcionamiento del proyecto:

- plugin para compilacion con maven:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>11</source>
        <target>11</target>
    </configuration>
</plugin>
```
- plugin para compilar el archivo proto, tanto para los objetos **message** como para los **services**:
```   
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <!-- configuracion para generar clase de Grpc del proto -->
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.21.5:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.49.0:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
### Extenciones

Y finalmente agregar la siguiente extencion para reconocer variables de ambiente:
```
<extension>
    <groupId>kr.motd.maven</groupId>
    <artifactId>os-maven-plugin</artifactId>
    <version>1.7.1</version>
</extension>
```

---
## Indicaciones de uso

Se requiere compilar el proyecto con ``mvn compile`` para generar las clases del archivo proto y que no existan errores.

Despues inicar el proceso del server, este quedara esperando las llamadas del cliente.

Despues iniciar el proceso del cliente y ejecutar una llamada al servicio.

___
