FROM openjdk:16 as builder
COPY ./ /
RUN /gradlew build

FROM openjdk:16
COPY --from=builder /build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
