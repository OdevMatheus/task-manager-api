# build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Camada de cache para dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código e gera o pacote .jar
COPY src ./src
RUN mvn clean package -DskipTests

# runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia apenas o jar
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring
EXPOSE 8080

# Execução da aplicação configurada para o perfil de produção
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]