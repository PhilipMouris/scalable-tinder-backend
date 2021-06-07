

#
## Add files.
#FROM ubuntu:18.04
#
#
#RUN  apt-get update \
#  && apt-get install -y wget \
#  && rm -rf /var/lib/apt/lists/* \
#  && apt-get update \
#  && apt-get install -y gnupg \
#  && rm -rf /var/lib/apt/lists/*
#
#RUN apt-get update && \
#    apt-get install -y --fix-missing erlang-base \
#                            erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
#                            erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
#                            erlang-runtime-tools erlang-snmp erlang-ssl \
#                            erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl
#
## Install RabbitMQ.
#RUN \
#  apt-get update && \
#  DEBIAN_FRONTEND=noninteractive apt-get install -y --fix-missing rabbitmq-server && \
#  rm -rf /var/lib/apt/lists/* && \
#  rabbitmq-plugins enable rabbitmq_management  \
#
#
## Define environment variables.
#ENV RABBITMQ_LOG_BASE=/data/log
#ENV RABBITMQ_MNESIA_BASE=/data/mnesia
#
## Define mount points.
#VOLUME ["/data/log", "/data/mnesia"]

FROM openjdk:16-alpine3.13
# ----
# Install Maven
RUN apk add --no-cache curl tar bash
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
RUN mkdir -p /usr/share/maven && \
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 && \
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# ----
# Install project dependencies and keep sources
# make source folder
RUN mkdir -p /usr/src/app/Backend
WORKDIR /usr/src/app/Backend
# install maven dependency packages (keep in image)
COPY ./pom.xml /usr/src/app/Backend
RUN mvn clean
RUN mvn -T 1C install && rm -rf target
# copy other source files (keep in image)
COPY src/main/resources /usr/src/app/Backend/src/main/resources
COPY src/main/java/Config /usr/src/app/Backend/src/main/java/Config

COPY src/main/java/MessageQueue /usr/src/app/Backend/src/main/java/MessageQueue
COPY DockerFile_Builders/RabbitMQ/Commands.sh /usr/src/app/Backend/
COPY DockerFile_Builders/RabbitMQ/wait_for_it.sh /usr/src/app/Backend/
RUN mvn compile
RUN mvn package
ENTRYPOINT ["bash","./Commands.sh"]
#EXPOSE 5672
#EXPOSE 15672
# RUN java -cp target/Backend-1.0-SNAPSHOT.jar NettyWebServer.NettyServer
# CMD ["mvn","java"]