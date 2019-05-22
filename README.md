# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 26 - Campus TagusPark

Tiago Rosado 77958 tiagocrosado@tecnico.ulisboa.pt

Rui Pacheco 77989 rui.pacheco@tecnico.ulisboa.pt

João Peralta 77997 joao.peralta@tecnico.ulisboa.pt 

Repositório:
[tecnico-distsys/T_26-project](https://github.com/tecnico-distsys/T_26-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo Linux

[1] Iniciar servidores de apoio

JUDDI:
```
#### Iniciar o jUDDI
Fazer o download do [jUDDI 3.2.2](http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/download/juddi-3.3.2_tomcat-7.0.64_9090.zip)
Extrair o ficheiro zip 
Executar
chmod +x juddi-3.3.2_tomcat-7.0.64_9090/bin/*.sh 
cd juddi-3.3.2_tomcat-7.0.64_9090/bin/
./startup.sh

#### Parar o jUDDI
cd juddi-3.3.2_tomcat-7.0.64_9090/bin/
./shutdown.sh
...  
```


[2] Criar pasta temporária

```
mkdir SD-26-T
cd SD-26-T
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone --branch SD_R2 https://github.com/tecnico-distsys/T_26-project.git 
```


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd X509
mvn clean install
```

```
cd ws-handlers
mvn clean install
```
-------------------------------------------------------------------------------

### Serviço CA (entidade certificadora)

[5] Construir e executar **servidor**

```
cd ca-ws
mvn clean compile 
mvn exec:java
```

```
cd ca-ws-cli
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[6] Construir e executar **servidor**

```
cd transporter-ws
mvn clean compile
mvn exec:java
```

[7] Construir **cliente** 

```
cd transporter-ws-cli
mvn clean compile
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[8] Construir e executar **servidor**

```
cd broker-ws
mvn clean compile
mvn exec:java
```


[9] Construir **cliente** 

```
cd broker-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
