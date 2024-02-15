#!/bin/bash
docker compose down
mvn clean package
mvn war:war
docker build -f mesa11.dockerfile . -t sowbreira/mesa11
docker compose up
