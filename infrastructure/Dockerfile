# Multi-stage Dockerfile para Java 25 + Spring Boot
# Optimizado para CI/CD y producción

# Stage 1: Build
FROM openjdk:25-jdk-slim AS builder

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Configurar directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

# Descargar dependencias (cache layer)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src/ src/

# Compilar aplicación
RUN ./gradlew bootJar --no-daemon \
    && java -Djarmode=layertools -jar build/libs/*.jar extract

# Stage 2: Runtime
FROM openjdk:25-jre-slim AS runtime

# Crear usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Instalar herramientas de monitoreo
RUN apt-get update && apt-get install -y \
    curl \
    dumb-init \
    && rm -rf /var/lib/apt/lists/*

# Configurar directorio de trabajo
WORKDIR /app

# Copiar layers de la aplicación (optimización de cache)
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Cambiar ownership al usuario no-root
RUN chown -R appuser:appuser /app
USER appuser

# Configurar JVM para contenedores
ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    --enable-preview \
    --add-modules jdk.incubator.concurrent \
    -Djava.security.egd=file:/dev/./urandom"

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Usar dumb-init para manejo correcto de señales
ENTRYPOINT ["dumb-init", "--"]

# Comando de inicio
CMD ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
