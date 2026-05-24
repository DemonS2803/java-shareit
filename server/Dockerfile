FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} shareit-bll-server.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /shareit-bll-server.jar"]