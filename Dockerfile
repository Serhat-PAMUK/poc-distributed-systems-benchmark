# AŞAMA 1: Build (Derleme)
FROM hub.epias.com.tr/hub/library/maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY epias-EPIAS-DC-CA.crt /usr/local/share/ca-certificates/
COPY repo.maven.apache.org.crt /usr/local/share/ca-certificates/
RUN update-ca-certificates

# Bağımlılıkları ve build'i yap
COPY pom.xml .
RUN mvn dependency:go-offline


COPY src ./src
RUN mvn clean package -DskipTests


# AŞAMA 2: Runtime (Çalıştırma)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Build stage'den JAR dosyasını kopyala
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]