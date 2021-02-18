FROM openjdk:17-jdk-alpine

COPY build/libs/action-update-twirp-gradle-all.jar /action-update-twirp-gradle-all.jar
ENTRYPOINT ["java", "-jar", "action-update-twirp-gradle-all.jar"]
