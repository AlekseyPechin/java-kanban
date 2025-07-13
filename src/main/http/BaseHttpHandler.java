package main.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.adapters.DurationAdapter;
import main.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {

    protected final Charset defaultCharset = StandardCharsets.UTF_8;

    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void sendNoSuchEndpoint(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "There is no such endpoint", 404);
    }

    protected void send200(HttpExchange exchange, String responseBody) throws IOException {
        writeResponse(exchange, responseBody, 200);
    }

    protected void send201(HttpExchange exchange, String responseBody) throws IOException {
        writeResponse(exchange, responseBody, 201);
    }

    protected void send400(HttpExchange exchange, String responseBody) throws IOException {
        writeResponse(exchange, responseBody, 400);
    }

    protected void send404NotFound(HttpExchange exchange, String responseBody) throws IOException {
        writeResponse(exchange, responseBody, 404);
    }

    protected void send406HasInteractions(HttpExchange exchange, String responseBody) throws IOException {
        writeResponse(exchange, responseBody, 406);
    }

    //для отправки ответа, если произошла ошибка сервера
    protected void send500(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, "Внутренняя ошибка сервера", 500);
    }

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String taskId = exchange.getRequestURI().getPath().split("/")[2];

        try {
            return Optional.of(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange, String responseText, int responseCode) throws IOException {
        exchange.getResponseHeaders()
                .add("Content-Type", "application/json; charset=" + defaultCharset);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseText.getBytes(defaultCharset));
        }
        exchange.close();
    }


}
