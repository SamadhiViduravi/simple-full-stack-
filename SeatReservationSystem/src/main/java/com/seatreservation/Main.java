package com.seatreservation;

import com.seatreservation.storage.DataStore;
import com.seatreservation.models.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class Main {

    // Constants for HTTP methods
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    // Constants for JSON keys
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_SEAT_NUMBER = "seatNumber";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SEAT_ID = "seatId";
    private static final String KEY_DATE = "date";
    private static final String KEY_SEATS = "seats";
    private static final String KEY_RESERVATIONS = "reservations";

    // Constants for status values
    private static final String STATUS_AVAILABLE = "Available";
    private static final String STATUS_UNAVAILABLE = "Unavailable";
    private static final String STATUS_ACTIVE = "Active";
    private static final String STATUS_CANCELLED = "Cancelled";

    // User roles
    private static final String ROLE_INTERN = "INTERN";
    private static final String DEFAULT_ROLE = ROLE_INTERN;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/seats", new SeatHandler());
        server.createContext("/api/reservations", new ReservationHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server running on http://localhost:8080");
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if (METHOD_OPTIONS.equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String response = "";
            if (METHOD_POST.equals(exchange.getRequestMethod())) {
                if (exchange.getRequestURI().getPath().equals("/api/users/login")) {
                    response = handleLogin(exchange);
                } else {
                    response = handleRegister(exchange);
                }
            }

            sendResponse(exchange, response);
        }

        private String handleLogin(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            JSONObject jsonResponse = new JSONObject();

            try {
                JSONObject loginData = new JSONObject(body);
                String email = loginData.getString(KEY_EMAIL);
                String password = loginData.getString(KEY_PASSWORD);

                User user = DataStore.findUserByEmail(email);
                if (user != null && user.getPassword().equals(password)) {
                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "Login successful");
                    jsonResponse.put(KEY_USER_ID, user.getId());
                    jsonResponse.put(KEY_NAME, user.getName());
                    jsonResponse.put(KEY_EMAIL, user.getEmail());
                    jsonResponse.put(KEY_ROLE, user.getRole());
                } else {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Invalid credentials");
                }
            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Login error: " + e.getMessage());
            }

            return jsonResponse.toString();
        }

        private String handleRegister(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            JSONObject jsonResponse = new JSONObject();

            try {
                JSONObject userData = new JSONObject(body);
                String name = userData.getString(KEY_NAME);
                String email = userData.getString(KEY_EMAIL);
                String password = userData.getString(KEY_PASSWORD);

                // Use default role if not provided (fixes the JSONObject["role"] not found error)
                String role = DEFAULT_ROLE;
                if (userData.has(KEY_ROLE)) {
                    role = userData.getString(KEY_ROLE);
                }

                // Check if user already exists
                if (DataStore.findUserByEmail(email) != null) {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "User with this email already exists");
                } else {
                    // Create new user
                    int newId = DataStore.getNextUserId();
                    User newUser = new User(newId, name, email, password, role);
                    DataStore.users.add(newUser);

                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "User registered successfully");
                    jsonResponse.put(KEY_USER_ID, newId);
                }
            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Registration error: " + e.getMessage());
            }

            return jsonResponse.toString();
        }
    }

    static class SeatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if (METHOD_OPTIONS.equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            JSONObject jsonResponse = new JSONObject();

            if (METHOD_GET.equals(method)) {
                handleGetSeats(jsonResponse);
            } else if (METHOD_POST.equals(method)) {
                handleAddSeat(exchange, jsonResponse);
            } else if (METHOD_PUT.equals(method)) {
                handleUpdateSeat(exchange, path, jsonResponse);
            } else if (METHOD_DELETE.equals(method)) {
                handleDeleteSeat(path, jsonResponse);
            }

            sendResponse(exchange, jsonResponse.toString());
        }

        private void handleGetSeats(JSONObject jsonResponse) {
            JSONArray seatsArray = new JSONArray();

            for (Seat seat : DataStore.seats) {
                JSONObject seatJson = new JSONObject();
                seatJson.put("id", seat.getId());
                seatJson.put(KEY_SEAT_NUMBER, seat.getSeatNumber());
                seatJson.put(KEY_LOCATION, seat.getLocation());
                seatJson.put(KEY_STATUS, seat.getStatus());
                seatsArray.put(seatJson);
            }

            jsonResponse.put(KEY_SUCCESS, true);
            jsonResponse.put(KEY_SEATS, seatsArray);
        }

        private void handleAddSeat(HttpExchange exchange, JSONObject jsonResponse) throws IOException {
            try {
                String body = readRequestBody(exchange);
                JSONObject seatData = new JSONObject(body);

                String seatNumber = seatData.getString(KEY_SEAT_NUMBER);
                String location = seatData.getString(KEY_LOCATION);
                String status = seatData.getString(KEY_STATUS);

                int newId = DataStore.getNextSeatId();
                Seat newSeat = new Seat(newId, seatNumber, location, status);
                DataStore.seats.add(newSeat);

                jsonResponse.put(KEY_SUCCESS, true);
                jsonResponse.put(KEY_MESSAGE, "Seat added successfully");
                jsonResponse.put("seatId", newId);

            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Error adding seat: " + e.getMessage());
            }
        }

        private void handleUpdateSeat(HttpExchange exchange, String path, JSONObject jsonResponse) throws IOException {
            try {
                String[] pathParts = path.split("/");
                int seatId = Integer.parseInt(pathParts[pathParts.length - 1]);
                String body = readRequestBody(exchange);
                JSONObject seatData = new JSONObject(body);

                Seat seat = DataStore.findSeatById(seatId);
                if (seat != null) {
                    if (seatData.has(KEY_SEAT_NUMBER)) {
                        seat.setSeatNumber(seatData.getString(KEY_SEAT_NUMBER));
                    }
                    if (seatData.has(KEY_LOCATION)) {
                        seat.setLocation(seatData.getString(KEY_LOCATION));
                    }
                    if (seatData.has(KEY_STATUS)) {
                        seat.setStatus(seatData.getString(KEY_STATUS));
                    }

                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "Seat updated successfully");
                } else {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Seat not found");
                }

            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Error updating seat: " + e.getMessage());
            }
        }

        private void handleDeleteSeat(String path, JSONObject jsonResponse) {
            try {
                String[] pathParts = path.split("/");
                int seatId = Integer.parseInt(pathParts[pathParts.length - 1]);

                Seat seatToRemove = null;
                for (Seat seat : DataStore.seats) {
                    if (seat.getId() == seatId) {
                        seatToRemove = seat;
                        break;
                    }
                }

                if (seatToRemove != null) {
                    DataStore.seats.remove(seatToRemove);
                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "Seat deleted successfully");
                } else {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Seat not found");
                }

            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Error deleting seat: " + e.getMessage());
            }
        }
    }

    static class ReservationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            if (METHOD_OPTIONS.equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            JSONObject jsonResponse = new JSONObject();

            if (METHOD_GET.equals(method)) {
                handleGetReservations(query, jsonResponse);
            } else if (METHOD_POST.equals(method)) {
                handleCreateReservation(exchange, jsonResponse);
            } else if (METHOD_DELETE.equals(method)) {
                handleCancelReservation(path, jsonResponse);
            }

            sendResponse(exchange, jsonResponse.toString());
        }

        private void handleGetReservations(String query, JSONObject jsonResponse) {
            JSONArray reservationsArray = new JSONArray();

            Integer userId = extractUserIdFromQuery(query);

            for (Reservation res : DataStore.reservations) {
                if (userId == null || res.getUserId() == userId) {
                    JSONObject resJson = createReservationJson(res);
                    reservationsArray.put(resJson);
                }
            }

            jsonResponse.put(KEY_SUCCESS, true);
            jsonResponse.put(KEY_RESERVATIONS, reservationsArray);
        }

        private Integer extractUserIdFromQuery(String query) {
            if (query != null && query.contains("userId=")) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("userId=")) {
                        return Integer.parseInt(param.substring(7));
                    }
                }
            }
            return null;
        }

        private JSONObject createReservationJson(Reservation res) {
            JSONObject resJson = new JSONObject();
            resJson.put("id", res.getId());
            resJson.put(KEY_USER_ID, res.getUserId());
            resJson.put(KEY_SEAT_ID, res.getSeatId());
            resJson.put(KEY_DATE, res.getDate());
            resJson.put(KEY_STATUS, res.getStatus());

            // Add seat details
            Seat seat = DataStore.findSeatById(res.getSeatId());
            if (seat != null) {
                resJson.put(KEY_SEAT_NUMBER, seat.getSeatNumber());
                resJson.put(KEY_LOCATION, seat.getLocation());
            }

            // Add user details
            User user = DataStore.findUserById(res.getUserId());
            if (user != null) {
                resJson.put("userName", user.getName());
                resJson.put("userEmail", user.getEmail());
            }

            return resJson;
        }

        private void handleCreateReservation(HttpExchange exchange, JSONObject jsonResponse) throws IOException {
            try {
                String body = readRequestBody(exchange);
                JSONObject reservationData = new JSONObject(body);

                int userId = reservationData.getInt(KEY_USER_ID);
                int seatId = reservationData.getInt(KEY_SEAT_ID);
                String date = reservationData.getString(KEY_DATE);

                // Check if seat is available
                Seat seat = DataStore.findSeatById(seatId);
                if (seat == null) {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Seat not found");
                } else if (!STATUS_AVAILABLE.equals(seat.getStatus())) {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Seat is not available");
                } else {
                    // Create new reservation
                    int newId = DataStore.getNextReservationId();
                    Reservation newReservation = new Reservation(newId, userId, seatId, date, STATUS_ACTIVE);
                    DataStore.reservations.add(newReservation);

                    // Update seat status
                    seat.setStatus(STATUS_UNAVAILABLE);

                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "Seat reserved successfully");
                    jsonResponse.put("reservationId", newId);
                }

            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Error creating reservation: " + e.getMessage());
            }
        }

        private void handleCancelReservation(String path, JSONObject jsonResponse) {
            try {
                String[] pathParts = path.split("/");
                int reservationId = Integer.parseInt(pathParts[pathParts.length - 1]);

                // Find and cancel reservation
                Reservation reservationToCancel = null;
                for (Reservation res : DataStore.reservations) {
                    if (res.getId() == reservationId) {
                        reservationToCancel = res;
                        break;
                    }
                }

                if (reservationToCancel != null) {
                    reservationToCancel.setStatus(STATUS_CANCELLED);

                    // Make the seat available again
                    Seat seat = DataStore.findSeatById(reservationToCancel.getSeatId());
                    if (seat != null) {
                        seat.setStatus(STATUS_AVAILABLE);
                    }

                    jsonResponse.put(KEY_SUCCESS, true);
                    jsonResponse.put(KEY_MESSAGE, "Reservation cancelled successfully");
                } else {
                    jsonResponse.put(KEY_SUCCESS, false);
                    jsonResponse.put(KEY_MESSAGE, "Reservation not found");
                }

            } catch (Exception e) {
                jsonResponse.put(KEY_SUCCESS, false);
                jsonResponse.put(KEY_MESSAGE, "Error cancelling reservation: " + e.getMessage());
            }
        }
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        reader.close();
        return body.toString();
    }
}