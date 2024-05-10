FROM eclipse-temurin:17
WORKDIR /home
COPY ./target/lockbox-service-0.0.1-SNAPSHOT.jar lockbox.jar
ENTRYPOINT ["java", "-jar", "lockbox.jar"]