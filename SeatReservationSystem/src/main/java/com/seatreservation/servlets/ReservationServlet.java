package com.seatreservation.servlets;

import com.seatreservation.model.Reservation;
import com.seatreservation.model.Seat;
import com.seatreservation.storage.DataStore;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// This servlet handles reservation-related operations
@WebServlet("/api/reservations/*")
public class ReservationServlet extends HttpServlet {
    
    // Handle GET requests (get reservations)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all reservations or filter by user
                String userIdParam = request.getParameter("userId");
                
                List<Reservation> reservationsToReturn = new ArrayList<>();
                
                if (userIdParam != null) {
                    // Get reservations for specific user
                    int userId = Integer.parseInt(userIdParam);
                    for (Reservation res : DataStore.reservations) {
                        if (res.getUserId() == userId) {
                            reservationsToReturn.add(res);
                        }
                    }
                } else {
                    // Get all reservations
                    reservationsToReturn = DataStore.reservations;
                }
                
                // Convert reservations to JSON
                JSONArray resArray = new JSONArray();
                for (Reservation res : reservationsToReturn) {
                    JSONObject resJson = new JSONObject();
                    resJson.put("id", res.getId());
                    resJson.put("userId", res.getUserId());
                    resJson.put("seatId", res.getSeatId());
                    resJson.put("date", res.getDate());
                    resJson.put("status", res.getStatus());
                    
                    // Add seat details
                    Seat seat = DataStore.findSeatById(res.getSeatId());
                    if (seat != null) {
                        resJson.put("seatNumber", seat.getSeatNumber());
                        resJson.put("location", seat.getLocation());
                    }
                    
                    // Add user details
                    var user = DataStore.findUserById(res.getUserId());
                    if (user != null) {
                        resJson.put("userName", user.getName());
                        resJson.put("userEmail", user.getEmail());
                    }
                    
                    resArray.put(resJson);
                }
                
                jsonResponse.put("success", true);
                jsonResponse.put("reservations", resArray);
            } else {
                // Get specific reservation by ID
                String idStr = pathInfo.substring(1);
                int id = Integer.parseInt(idStr);
                
                Reservation reservation = null;
                for (Reservation res : DataStore.reservations) {
                    if (res.getId() == id) {
                        reservation = res;
                        break;
                    }
                }
                
                if (reservation != null) {
                    JSONObject resJson = new JSONObject();
                    resJson.put("id", reservation.getId());
                    resJson.put("userId", reservation.getUserId());
                    resJson.put("seatId", reservation.getSeatId());
                    resJson.put("date", reservation.getDate());
                    resJson.put("status", reservation.getStatus());
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("reservation", resJson);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Reservation not found");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle POST requests (create new reservation)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            // Read the JSON data from the request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Extract reservation data
            int userId = jsonRequest.getInt("userId");
            int seatId = jsonRequest.getInt("seatId");
            String date = jsonRequest.getString("date");
            
            // Validate the reservation
            String validationError = validateReservation(userId, seatId, date);
            if (validationError != null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", validationError);
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Create new reservation
            int newId = DataStore.getNextReservationId();
            Reservation newReservation = new Reservation(newId, userId, seatId, date, "Active");
            DataStore.reservations.add(newReservation);
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Seat reserved successfully");
            jsonResponse.put("reservationId", newId);
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error reserving seat: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle DELETE requests (cancel reservation)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Reservation ID required");
            } else {
                // Get reservation ID from URL
                String idStr = pathInfo.substring(1);
                int id = Integer.parseInt(idStr);
                
                // Find the reservation and cancel it
                Reservation reservationToCancel = null;
                for (Reservation res : DataStore.reservations) {
                    if (res.getId() == id) {
                        reservationToCancel = res;
                        break;
                    }
                }
                
                if (reservationToCancel != null) {
                    // Check if it's a future reservation
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date reservationDate = sdf.parse(reservationToCancel.getDate());
                    Date today = new Date();
                    
                    if (reservationDate.before(today)) {
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "Cannot cancel past reservations");
                    } else {
                        reservationToCancel.setStatus("Cancelled");
                        jsonResponse.put("success", true);
                        jsonResponse.put("message", "Reservation cancelled successfully");
                    }
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Reservation not found");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error cancelling reservation: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Validate reservation against business rules
    private String validateReservation(int userId, int seatId, String date) {
        try {
            // Check if seat exists and is available
            Seat seat = DataStore.findSeatById(seatId);
            if (seat == null) {
                return "Seat not found";
            }
            if (!"Available".equals(seat.getStatus())) {
                return "Seat is not available";
            }
            
            // Check if user exists
            if (DataStore.findUserById(userId) == null) {
                return "User not found";
            }
            
            // Check if date is in the past
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date reservationDate = sdf.parse(date);
            Date today = new Date();
            
            if (reservationDate.before(today)) {
                return "Cannot reserve seats for past dates";
            }
            
            // Check if user already has a reservation for this date
            for (Reservation res : DataStore.reservations) {
                if (res.getUserId() == userId && res.getDate().equals(date) && "Active".equals(res.getStatus())) {
                    return "You already have a reservation for this date";
                }
            }
            
            // Check if seat is already reserved for this date
            for (Reservation res : DataStore.reservations) {
                if (res.getSeatId() == seatId && res.getDate().equals(date) && "Active".equals(res.getStatus())) {
                    return "This seat is already reserved for the selected date";
                }
            }
            
            return null; // No errors
        } catch (Exception e) {
            return "Validation error: " + e.getMessage();
        }
    }
}