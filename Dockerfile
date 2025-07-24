# Java 21
FROM eclipse-temurin:21-jdk AS builder

# Çalışma dizini oluştur
WORKDIR /app

# Maven kopyala
COPY mvnw .
COPY .mvn .mvn

# pom.xml bağımlılıklarını yükler
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Tüm kaynak kodları kopyala ve paketle
COPY src src
RUN ./mvnw clean package -DskipTests

# Çalıştırma imajı
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Uygulamayı çalıştır
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
