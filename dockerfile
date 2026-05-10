FROM eclipse-temurin:17-jdk-jammy

# create temporary drive
VOLUME /tmp

# expose ports 8080 for spring boot
EXPOSE 8090

# create app dir
RUN mkdir -p /jpantry/

# create logs dir
RUN mkdir -p /jpantry/logs/

# cp demo-0.0.1-SNAPSHOT.jar /app/application.jar
ADD target/jpantry-0.0.1-SNAPSHOT.jar /jpantry/jpantry.jar

# run the .jar file with the active profile dev
ENTRYPOINT ["java","-D java.security.egd=file:/dev/./urandom","-D spring.profiles.active=docker", "-jar", "/jpantry/jpantry.jar"]