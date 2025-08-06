#Build stage
FROM maven:3.8.7-openjdk-18 As build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

#Run stage

FROM amazoncorretto:17

#define few things

ARG PROFILE=dev
ARG APP_VERSION=1.0.2

WORKDIR /app
COPY --from=build /build/target/demo-*.jar /app/

EXPOSE 8088


ENV DV_URL=jdbc:postgresql://postgres-sql-bsn:5432/book_social_network
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}

CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DV_URL} demo-${JAR_VERSION}.jar