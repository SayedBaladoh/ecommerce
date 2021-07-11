# Docker multi-stage build
 
# 1. Building the App with Maven
FROM maven:3-jdk-11
 
ADD . /app
WORKDIR /app
 
# Just echo so we can see, if everything is there :)
#RUN ls -l
 
# Run Maven build
RUN mvn clean install -Dmaven.test.skip=true
 
# 2. Just using the build artifact and then removing the build-container
FROM openjdk:11-jdk
 
MAINTAINER Sayed Baladoh
 
# VOLUME /tmp
 
# Add Spring Boot app.jar to Container
COPY --from=0 "/app/target/ecommerce-*-SNAPSHOT.jar" /app/ecommerce.jar
 
# Fire up our Spring Boot app by default
CMD ["java","-jar","/app/ecommerce.jar"]