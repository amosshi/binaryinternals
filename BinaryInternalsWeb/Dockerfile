# Binary Internals web server
#
# Build, from the repository root:
#   docker build -f BinaryInternalsWeb/Dockerfile -t binaryinternals-web .
#
# Run:
#   docker run --rm -p 8080:8080 --read-only --tmpfs /tmp --memory 512m --cpus 1 binaryinternals-web
#
# Settings, by environment variables (docker run -e NAME=value):
#   PORT=8080  MAX_UPLOAD_MB=20  MAX_CONCURRENT_PARSES=<processors>  PARSE_TIMEOUT_SECONDS=30  CORS_ORIGIN=*

# ---- Build the modules and a minimal Java runtime ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY . .
RUN mvn -B -q -pl BinaryInternalsWeb -am package \
        -DskipTests -Dmaven.javadoc.skip=true -Dspotbugs.skip=true -Dcheckstyle.skip=true -Dpmd.skip=true \
    && cp BinaryInternalsWeb/target/BinaryInternalsWeb-*[0-9].jar BinaryInternalsWeb/target/libs/
# The format plug-ins are listed explicitly: they are found via ServiceLoader,
# so no module requires them
RUN jlink --module-path BinaryInternalsWeb/target/libs \
        --add-modules org.binaryinternals.web,org.binaryinternals.formatbmp,org.binaryinternals.formatclass,org.binaryinternals.formatdex,org.binaryinternals.formatelf,org.binaryinternals.formatjpeg,org.binaryinternals.formatpdf,org.binaryinternals.formatpng,org.binaryinternals.formatzip \
        --add-options="-Djava.awt.headless=true -XX:MaxRAMPercentage=75" \
        --strip-debug --no-header-files --no-man-pages --compress=zip-6 \
        --output /runtime

# ---- Run ----
FROM debian:stable-slim
COPY --from=build /runtime /opt/binaryinternals
RUN useradd --uid 10001 --no-create-home --shell /usr/sbin/nologin app
USER 10001
ENV HOST=0.0.0.0 \
    PORT=8080
EXPOSE 8080
ENTRYPOINT ["/opt/binaryinternals/bin/java", "-m", "org.binaryinternals.web/org.binaryinternals.web.WebServer"]
