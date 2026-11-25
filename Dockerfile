# Estágio 1: Compilação com Ant e JDK 21 (compatível com javac.source=18)
FROM eclipse-temurin:21-jdk-jammy AS builder
RUN apt-get update && apt-get install -y ant && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY atendimento/ /app/
RUN ant clean jar

# Estágio 2: Execução leve com JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/dist/atendimento.jar /app/atendimento.jar
COPY --from=builder /app/dist/lib/ /app/lib/

ENTRYPOINT ["java", "-jar", "/app/atendimento.jar"]
