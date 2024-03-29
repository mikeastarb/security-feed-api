FROM maven:3.8.4-openjdk-17-slim
COPY pom.xml /project/pom.xml
COPY src /project/src
COPY LICENSE.md /project/LICENSE.md
RUN mvn verify -f /project/pom.xml
ENTRYPOINT mvn spring-boot:run -f /project/pom.xml -DskipTests