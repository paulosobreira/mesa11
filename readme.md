# Mesa-11

## Mesa-11 por Paulo Sobreira
-Mesa 11 Futebol de bot&atilde;o

## Controles:
- Use o mouse para palhetar os bot&otilde;es
- Use a rolagem do mouse para ajustar o zoom
- Use as setas para posicionar a tela
- O goleiro pode ser movido na grande area
- Para jogar com Goleiro palhete a bola

## Construção Maven e Docker

- mvn clean package
- mvn war:war
- docker build -f mesa11.dockerfile . -t sowbreira/mesa11
- docker push sowbreira/mesa11

## Como testar no Play with Docker

Pode ser executado no [Play with Docker](https://labs.play-with-docker.com/)

>Baixar o aqruivo do docker compose
```
curl -LfO 'https://raw.githubusercontent.com/paulosobreira/mesa11/master/docker-compose.yaml'
```

>Iniciar containers do Mysql,PhpMyAdmin e FlMane
```
docker compose up
```

>Url de acesso:

link_gerado_playwithdocker/**mesa11/html5/index.html**
