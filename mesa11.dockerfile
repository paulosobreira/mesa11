FROM tomcat:9.0.82-jdk11
MAINTAINER Paulo Sobreira
WORKDIR /usr/local/tomcat/webapps
RUN  rm -rf *
ADD target/mesa11.war /usr/local/tomcat/webapps/mesa11.war
EXPOSE 8080