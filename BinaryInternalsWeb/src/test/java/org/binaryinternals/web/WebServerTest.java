package org.binaryinternals.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WebServerTest {

    private static final int MAX_UPLOAD = 1024 * 1024;

    @TempDir
    static Path tempDir;

    private static WebServer server;

    /// HTTP response.
    private record Response(int statusCode, String body, byte[] bytes, HttpURLConnection connection) {

        String header(final String name) {
            return this.connection.getHeaderField(name);
        }
    }

    @BeforeAll
    static void start() throws IOException {
        System.setProperty("java.awt.headless", "true");
        server = new WebServer(new WebServer.Config("127.0.0.1", 0, MAX_UPLOAD, 2, 30, "*", tempDir));
        server.start();
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    private static Response request(final String method, final String path, final byte[] body) throws IOException {
        final HttpURLConnection c = (HttpURLConnection) URI.create("http://127.0.0.1:" + server.getPort() + path).toURL().openConnection();
        c.setRequestMethod(method);
        if (body != null) {
            c.setDoOutput(true);
            c.setFixedLengthStreamingMode(body.length);
            try (OutputStream out = c.getOutputStream()) {
                out.write(body);
            } catch (IOException e) {
                // The server may reject the body before reading all of it
            }
        }
        final int status = c.getResponseCode();
        try (InputStream in = status < 400 ? c.getInputStream() : c.getErrorStream()) {
            final byte[] bytes = in == null ? new byte[0] : in.readAllBytes();
            return new Response(status, new String(bytes, StandardCharsets.UTF_8), bytes, c);
        }
    }

    private static Response get(final String path) throws IOException {
        return request("GET", path, null);
    }

    private static Response post(final String path, final byte[] body) throws IOException {
        return request("POST", path, body);
    }

    private static byte[] ownClassFile() throws IOException {
        try (InputStream in = WebServerTest.class.getResourceAsStream("WebServerTest.class")) {
            return in.readAllBytes();
        }
    }

    @Test
    void health() throws IOException {
        final Response r = get("/health");
        assertEquals(200, r.statusCode());
        assertEquals("{\"status\":\"UP\"}", r.body());
    }

    @Test
    void formats() throws IOException {
        final Response r = get("/api/v1/formats");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"class\""), r.body());
        assertEquals("*", r.header("Access-Control-Allow-Origin"));
    }

    @Test
    void parseClassFile() throws IOException {
        final byte[] bytes = ownClassFile();
        final Response r = post("/api/v1/parse?name=WebServerTest.class", bytes);
        assertEquals(200, r.statusCode(), r.body());
        assertTrue(r.body().startsWith("{\"text\":\"WebServerTest.class\",\"start\":0,\"length\":" + bytes.length), r.body());
        assertTrue(r.body().contains("\"start\":0,\"length\":4"), "magic node");
        assertTrue(r.body().contains("\"icon\":"), "icon names");
        assertTrue(r.body().contains("\"description\":"), "descriptions");
    }

    @Test
    void gzip() throws IOException {
        final HttpURLConnection c = (HttpURLConnection) URI.create("http://127.0.0.1:" + server.getPort()
                + "/api/v1/parse?name=WebServerTest.class").toURL().openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Accept-Encoding", "gzip");
        c.setDoOutput(true);
        try (OutputStream out = c.getOutputStream()) {
            out.write(ownClassFile());
        }
        assertEquals(200, c.getResponseCode());
        assertEquals("gzip", c.getContentEncoding());
        try (InputStream in = new GZIPInputStream(c.getInputStream())) {
            assertTrue(new String(in.readAllBytes(), StandardCharsets.UTF_8).startsWith("{\"text\":\"WebServerTest.class\""));
        }
    }

    @Test
    void parseErrors() throws IOException {
        assertEquals(400, post("/api/v1/parse", new byte[]{1}).statusCode());
        assertEquals(415, post("/api/v1/parse?name=a.unknown", new byte[]{1}).statusCode());
        assertEquals(413, post("/api/v1/parse?name=a.class", new byte[MAX_UPLOAD + 1]).statusCode());
        assertEquals(405, get("/api/v1/parse?name=a.class").statusCode());

        final Response invalid = post("/api/v1/parse?name=a.class", new byte[]{1, 2, 3});
        assertEquals(422, invalid.statusCode(), invalid.body());
        assertTrue(invalid.body().startsWith("{\"status\":422,\"error\":"), invalid.body());
    }

    @Test
    void staticContent() throws IOException {
        final Response index = get("/");
        assertEquals(200, index.statusCode());
        assertTrue(index.body().contains("<title>"));
        assertEquals(404, get("/no-such-page").statusCode());

        final Response icon = get("/icons/Magic.png");
        assertEquals(200, icon.statusCode());
        assertEquals("image/png", icon.header("Content-Type"));
        assertTrue(icon.bytes().length > 8 && icon.bytes()[1] == 'P', "PNG signature");
        assertEquals(404, get("/icons/NoSuchIcon.png").statusCode());
    }

    @Test
    void jsonEscape() {
        assertEquals("\"a\\\"b\\\\c\\n\\u003c/script\\u003e\\u0001\"", Json.string("a\"b\\c\n</script>\u0001"));
    }
}
