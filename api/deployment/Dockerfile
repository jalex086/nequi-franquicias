FROM eclipse-temurin:25-jdk-alpine

VOLUME /tmp

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copiar JAR con el nombre correcto
COPY *.jar app.jar

# Configuración JVM optimizada para contenedores
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

# Cambiar ownership del JAR
RUN chown appuser:appuser app.jar

# Cambiar a usuario no-root
USER appuser

# Health check para verificar que la app esté funcionando
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
