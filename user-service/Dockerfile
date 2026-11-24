# Etapa 7b: transforma o user-service numa imagem Docker.
# São dois estágios: o primeiro COMPILA (precisa do JDK e do Maven),
# o segundo só RODA (precisa só do JRE). A imagem final fica menor e sem ferramentas de build.

# ---------- Estágio 1: compilar ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia primeiro só o que define as dependências. Enquanto o pom.xml não mudar,
# o Docker reaproveita esta camada e não baixa tudo de novo a cada build.
COPY .mvn .mvn
COPY mvnw pom.xml ./
# Remove quebras de linha do Windows (CRLF), que fazem o mvnw falhar no Linux
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -q -B dependency:go-offline

# Agora o código. Mudar o código só refaz daqui para baixo.
COPY src src
RUN ./mvnw -q -B package -DskipTests

# ---------- Estágio 2: rodar ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Roda com um usuário comum, e não como root: se alguém invadir a aplicação,
# não ganha poder de administrador dentro do container
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
