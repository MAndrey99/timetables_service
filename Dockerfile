FROM openjdk:15 as builder
COPY ./ /
RUN /gradlew build

FROM openjdk:15
COPY --from=builder /build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
