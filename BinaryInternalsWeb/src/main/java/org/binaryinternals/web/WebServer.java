/*
 * WebServer.java    October 20, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.Icons;

/// Web server and HTTP API, each request runs on a virtual thread.
///
/// | Endpoint | Purpose |
/// |---|---|
/// | `GET /` | The web page |
/// | `GET /health` | Health check |
/// | `GET /api/v1/formats` | Supported formats |
/// | `POST /api/v1/parse?name=Foo.class` | Parse the request body, get the JSON tree |
/// | `GET /icons/{Name}.png` | Tree node icon |
///
/// Configured by environment variables, see {@link Config#fromEnv()}.
///
/// @author Amos Shi
public final class WebServer {

    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TYPE_JSON = "application/json; charset=utf-8";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String API_PREFIX = "/api/";
    private static final int GZIP_MIN_BYTES = 1024;

    private final Config config;
    private final ParseService parser;
    private final HttpServer server;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore parsePermits;
    private final Map<String, byte[]> iconCache = new ConcurrentHashMap<>();

    /// Server settings.
    ///
    /// @param host Address to listen on
    /// @param port Port to listen on
    /// @param maxUploadBytes Maximum size of an uploaded file
    /// @param maxConcurrentParses Maximum number of files parsed at the same time
    /// @param parseTimeoutSeconds Maximum time to wait for a parse
    /// @param corsOrigin Value of `Access-Control-Allow-Origin` for the API,
    /// empty to disable
    /// @param tempDir Folder for the temporary copy of the uploaded files
    public record Config(String host, int port, int maxUploadBytes, int maxConcurrentParses,
            int parseTimeoutSeconds, String corsOrigin, Path tempDir) {

        /// Read the settings from the environment variables, with defaults:
        ///
        /// | Variable | Default |
        /// |---|---|
        /// | `HOST` | `127.0.0.1` |
        /// | `PORT` | `8080` |
        /// | `MAX_UPLOAD_MB` | `20` |
        /// | `MAX_CONCURRENT_PARSES` | number of processors |
        /// | `PARSE_TIMEOUT_SECONDS` | `30` |
        /// | `CORS_ORIGIN` | `*` |
        /// | `TMPDIR` | the `java.io.tmpdir` system property |
        ///
        /// @return The settings
        public static Config fromEnv() {
            final Map<String, String> env = System.getenv();
            return new Config(
                    env.getOrDefault("HOST", "127.0.0.1"),
                    Integer.parseInt(env.getOrDefault("PORT", "8080")),
                    Math.multiplyExact(Integer.parseInt(env.getOrDefault("MAX_UPLOAD_MB", "20")), 1024 * 1024),
                    Integer.parseInt(env.getOrDefault("MAX_CONCURRENT_PARSES",
                            String.valueOf(Runtime.getRuntime().availableProcessors()))),
                    Integer.parseInt(env.getOrDefault("PARSE_TIMEOUT_SECONDS", "30")),
                    env.getOrDefault("CORS_ORIGIN", "*"),
                    Path.of(env.getOrDefault("TMPDIR", System.getProperty("java.io.tmpdir"))));
        }
    }

    /// An HTTP error, sent as the JSON error body.
    private static final class HttpError extends Exception {

        private static final long serialVersionUID = 1L;

        private final int status;

        HttpError(final int status, final String message) {
            super(message);
            this.status = status;
        }
    }

    /// Create the server, call {@link #start()} to start it.
    ///
    /// @param config Server settings
    /// @throws IOException Failed to open the port
    public WebServer(final Config config) throws IOException {
        this.config = config;
        this.parser = new ParseService(config.tempDir());
        this.parsePermits = new Semaphore(config.maxConcurrentParses());
        this.server = HttpServer.create(new InetSocketAddress(config.host(), config.port()), 0);
        this.server.setExecutor(this.executor);

        this.server.createContext("/", ex -> this.handle(ex, this::handleIndex));
        this.server.createContext("/health", ex -> this.handle(ex, e -> this.send(e, 200, TYPE_JSON, "{\"status\":\"UP\"}")));
        this.server.createContext("/api/v1/formats", ex -> this.handle(ex, this::handleFormats));
        this.server.createContext("/api/v1/parse", ex -> this.handle(ex, this::handleParse));
        this.server.createContext("/icons/", ex -> this.handle(ex, this::handleIcon));
    }

    /// Start the server.
    public void start() {
        this.server.start();
        LOG.log(Level.INFO, "Listening on http://{0}:{1,number,#} with {2} format plug-ins",
                new Object[]{this.config.host(), this.getPort(), this.parser.getPlugins().size()});
    }

    /// Stop the server.
    public void stop() {
        this.server.stop(1);
        this.executor.shutdownNow();
    }

    /// Get the port listened on, useful when the configured port is `0`.
    ///
    /// @return The port
    public int getPort() {
        return this.server.getAddress().getPort();
    }

    @FunctionalInterface
    private interface Handler {

        void handle(HttpExchange ex) throws IOException, HttpError;
    }

    private void handle(final HttpExchange ex, final Handler handler) {
        try {
            ex.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            if (ex.getRequestURI().getPath().startsWith(API_PREFIX) && !this.config.corsOrigin().isEmpty()) {
                ex.getResponseHeaders().set("Access-Control-Allow-Origin", this.config.corsOrigin());
                ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                ex.getResponseHeaders().set("Access-Control-Allow-Headers", CONTENT_TYPE);
                if (METHOD_OPTIONS.equals(ex.getRequestMethod())) {
                    ex.sendResponseHeaders(204, -1);
                    return;
                }
            }
            handler.handle(ex);
        } catch (HttpError e) {
            this.sendQuietly(ex, e.status, Json.error(e.status, e.getMessage()));
        } catch (IOException | RuntimeException e) {
            LOG.log(Level.WARNING, "Request failed: " + ex.getRequestURI(), e);
            this.sendQuietly(ex, 500, Json.error(500, "Internal server error"));
        } finally {
            ex.close();
        }
    }

    private void handleIndex(final HttpExchange ex) throws IOException, HttpError {
        if (!"/".equals(ex.getRequestURI().getPath())) {
            throw new HttpError(404, "Not found");
        }
        requireMethod(ex, METHOD_GET);
        try (InputStream in = WebServer.class.getResourceAsStream("index.html")) {
            Objects.requireNonNull(in, "Missing resource index.html");
            ex.getResponseHeaders().set("Content-Security-Policy",
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self'");
            this.send(ex, 200, "text/html; charset=utf-8", in.readAllBytes());
        }
    }

    private void handleFormats(final HttpExchange ex) throws IOException, HttpError {
        requireMethod(ex, METHOD_GET);
        this.send(ex, 200, TYPE_JSON, this.parser.formatsJson());
    }

    private void handleParse(final HttpExchange ex) throws IOException, HttpError {
        requireMethod(ex, METHOD_POST);
        final String name = queryParam(ex, "name");
        if (name == null || name.isBlank()) {
            throw new HttpError(400, "Missing query parameter: name, for example ?name=Foo.class");
        }
        if (this.parser.findPlugin(name).isEmpty()) {
            throw new HttpError(415, "Unsupported file type: " + name);
        }

        final byte[] bytes = this.readBody(ex);
        if (!this.parsePermits.tryAcquire()) {
            throw new HttpError(503, "Server busy, please retry later");
        }
        // The permit is released when the parse ends, even after a timeout,
        // so a stuck parse keeps holding it
        final Future<String> result = this.executor.submit(() -> {
            try {
                return this.parser.parse(bytes, name);
            } finally {
                this.parsePermits.release();
            }
        });
        try {
            this.send(ex, 200, TYPE_JSON, result.get(this.config.parseTimeoutSeconds(), TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            result.cancel(true);
            throw new HttpError(504, "Parse timeout after " + this.config.parseTimeoutSeconds() + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpError(503, "Server shutting down");
        } catch (ExecutionException e) {
            throw parseError(name, e.getCause());
        }
    }

    private static HttpError parseError(final String name, final Throwable cause) {
        return switch (cause) {
            case ParseService.UnsupportedFormatException e -> new HttpError(415, e.getMessage());
            case FileFormatException e -> new HttpError(422, "Invalid file: " + e.getMessage());
            case OutOfMemoryError e -> {
                LOG.log(Level.SEVERE, "Out of memory when parsing " + name, e);
                yield new HttpError(422, "File too complex to parse");
            }
            case null, default -> {
                // Includes StackOverflowError and the plug-in runtime exceptions
                LOG.log(Level.INFO, "Failed to parse {0}: {1}", new Object[]{name, cause});
                LOG.log(Level.FINE, "Failed to parse " + name, cause);
                yield new HttpError(422, "Failed to parse the file: " + (cause == null ? "unknown error" : cause.getClass().getSimpleName()));
            }
        };
    }

    private byte[] readBody(final HttpExchange ex) throws IOException, HttpError {
        final String length = ex.getRequestHeaders().getFirst("Content-Length");
        if (length != null) {
            try {
                if (Long.parseLong(length.trim()) > this.config.maxUploadBytes()) {
                    throw tooLarge();
                }
            } catch (NumberFormatException e) {
                throw new HttpError(400, "Invalid Content-Length");
            }
        }
        try (InputStream in = ex.getRequestBody()) {
            final byte[] bytes = in.readNBytes(this.config.maxUploadBytes() + 1);
            if (bytes.length > this.config.maxUploadBytes()) {
                throw tooLarge();
            }
            if (bytes.length == 0) {
                throw new HttpError(400, "Empty request body, send the file content as the body");
            }
            return bytes;
        }
    }

    private HttpError tooLarge() {
        return new HttpError(413, "File too large, the maximum is " + this.config.maxUploadBytes() / (1024 * 1024) + " MB");
    }

    private void handleIcon(final HttpExchange ex) throws IOException, HttpError {
        requireMethod(ex, METHOD_GET);
        final String path = ex.getRequestURI().getPath();
        final String name = path.substring("/icons/".length());
        if (!name.endsWith(".png")) {
            throw new HttpError(404, "Not found");
        }
        final Icons icon;
        try {
            icon = Icons.valueOf(name.substring(0, name.length() - ".png".length()));
        } catch (IllegalArgumentException e) {
            throw new HttpError(404, "Not found");
        }
        final byte[] png = this.iconCache.computeIfAbsent(icon.name(), _ -> toPng(icon.getIcon()));
        ex.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
        this.send(ex, 200, "image/png", png);
    }

    private static byte[] toPng(final Icon icon) {
        final BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        try {
            icon.paintIcon(null, g, 0, 0);
        } finally {
            g.dispose();
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toByteArray();
    }

    private static void requireMethod(final HttpExchange ex, final String method) throws HttpError {
        if (!method.equals(ex.getRequestMethod())) {
            ex.getResponseHeaders().set("Allow", method);
            throw new HttpError(405, "Method not allowed, use " + method);
        }
    }

    private static String queryParam(final HttpExchange ex, final String key) {
        final String query = ex.getRequestURI().getRawQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            final int eq = pair.indexOf('=');
            final String k = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
            if (key.equals(k)) {
                return eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void send(final HttpExchange ex, final int status, final String type, final String body) throws IOException {
        this.send(ex, status, type, body.getBytes(StandardCharsets.UTF_8));
    }

    private void send(final HttpExchange ex, final int status, final String type, final byte[] body) throws IOException {
        ex.getResponseHeaders().set(CONTENT_TYPE, type);
        // The JSON trees repeat the descriptions, they compress very well
        final String accept = ex.getRequestHeaders().getFirst("Accept-Encoding");
        if (body.length > GZIP_MIN_BYTES && !type.startsWith("image/") && accept != null && accept.contains("gzip")) {
            final ByteArrayOutputStream gz = new ByteArrayOutputStream(body.length / 8);
            try (GZIPOutputStream out = new GZIPOutputStream(gz)) {
                out.write(body);
            }
            ex.getResponseHeaders().set("Content-Encoding", "gzip");
            ex.getResponseHeaders().set("Vary", "Accept-Encoding");
            this.sendBytes(ex, status, gz.toByteArray());
        } else {
            this.sendBytes(ex, status, body);
        }
    }

    private void sendBytes(final HttpExchange ex, final int status, final byte[] body) throws IOException {
        ex.sendResponseHeaders(status, body.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(body);
        }
    }

    private void sendQuietly(final HttpExchange ex, final int status, final String json) {
        try {
            this.send(ex, status, TYPE_JSON, json);
        } catch (IOException | RuntimeException e) {
            // Response already started, or the client is gone
            LOG.log(Level.FINE, "Cannot send the error response", e);
        }
    }

    /// Start the web server.
    ///
    /// @param args Not used, see {@link Config#fromEnv()}
    /// @throws IOException Failed to open the port
    public static void main(final String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        final WebServer web = new WebServer(Config.fromEnv());
        Runtime.getRuntime().addShutdownHook(new Thread(web::stop));
        web.start();
    }
}
