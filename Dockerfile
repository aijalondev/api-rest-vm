# Usa a imagem do Maven para compilar o projeto
FROM maven:3.8.6 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Usa a imagem do OpenJDK para rodar o .jar
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]