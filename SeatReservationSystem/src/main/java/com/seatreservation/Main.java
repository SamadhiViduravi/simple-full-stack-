package com.seatreservation;

import com.seatreservation.storage.DataStore;
import com.seatreservation.model.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/seats", new SeatHandler());
        server.createContext("/api/reservations", new ReservationHandler());

        server.start();
        System.out.println("✅ Server running on http://localhost:8080");
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String response = "";
            if ("POST".equals(exchange.getRequestMethod())) {
                if (exchange.getRequestURI().getPath().equals("/api/users/login")) {
                    response = handleLogin(exchange);
                } else {
                    response = "{\"success\": true, \"message\": \"User registered\"}";
                }
            }

            sendResponse(exchange, response);
        }

        private String handleLogin(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);

            // Simple login check
            if (body.contains("admin@office.com") && body.contains("admin123")) {
                return "{\"success\": true, \"message\": \"Login successful\", \"userId\": 1, \"name\": \"Admin User\", \"email\": \"admin@office.com\", \"role\": \"ADMIN\"}";
            } else if (body.contains("john@office.com") && body.contains("intern123")) {
                return "{\"success\": true, \"message\": \"Login successful\", \"userId\": 2, \"name\": \"John Intern\", \"email\": \"john@office.com\", \"role\": \"INTERN\"}";
            } else {
                return "{\"success\": false, \"message\": \"Invalid credentials\"}";
            }
        }
    }

    static class SeatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String response = "{\"success\": true, \"seats\": " + seatsToJson() + "}";
            sendResponse(exchange, response);
        }

        private String seatsToJson() {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < DataStore.seats.size(); i++) {
                Seat seat = DataStore.seats.get(i);
                json.append("{\"id\":").append(seat.getId())
                        .append(",\"seatNumber\":\"").append(seat.getSeatNumber())
                        .append("\",\"location\":\"").append(seat.getLocation())
                        .append("\",\"status\":\"").append(seat.getStatus()).append("\"}");
                if (i < DataStore.seats.size() - 1) json.append(",");
            }
            json.append("]");
            return json.toString();
        }
    }

    static class ReservationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String response = "{\"success\": true, \"message\": \"Reservation endpoint\"}";
            sendResponse(exchange, response);
        }
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }
}