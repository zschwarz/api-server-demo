package org.kornys.api_server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class HttpListener extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(HttpListener.class);
    private final UUID id = UUID.randomUUID();

    @Override
    public void start() {
        vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            if (request.method() == HttpMethod.GET) {
                getHandler(request);
            }
        });
        int port = 8899;
        httpServer.listen(port);
        logger.info("Client started listening on port: {}", port);
    }

    private void getHandler(HttpServerRequest request) {
        request.bodyHandler(handler -> {
            logger.info("Incoming GET request");

            HttpServerResponse response = successfulResponse(request);
            response.end(String.format("Hello from http server %s", id.toString()));
        });
    }

    private HttpServerResponse successfulResponse(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        response.setStatusCode(200);
        response.headers().add("Content-Type", "application/text");
        return response;
    }
}
