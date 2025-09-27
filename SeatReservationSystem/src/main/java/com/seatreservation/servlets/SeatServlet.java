package com.seatreservation.servlets;

import com.seatreservation.model.Seat;
import com.seatreservation.storage.DataStore;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// This servlet handles seat-related operations
@WebServlet("/api/seats/*")
public class SeatServlet extends HttpServlet {
    
    // Handle GET requests (get all seats or get seat by ID)
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
                // Get all seats
                JSONArray seatsArray = new JSONArray();
                for (Seat seat : DataStore.seats) {
                    JSONObject seatJson = new JSONObject();
                    seatJson.put("id", seat.getId());
                    seatJson.put("seatNumber", seat.getSeatNumber());
                    seatJson.put("location", seat.getLocation());
                    seatJson.put("status", seat.getStatus());
                    seatsArray.put(seatJson);
                }
                
                jsonResponse.put("success", true);
                jsonResponse.put("seats", seatsArray);
            } else {
                // Get specific seat by ID
                String idStr = pathInfo.substring(1); // Remove the leading "/"
                int id = Integer.parseInt(idStr);
                
                Seat seat = DataStore.findSeatById(id);
                if (seat != null) {
                    JSONObject seatJson = new JSONObject();
                    seatJson.put("id", seat.getId());
                    seatJson.put("seatNumber", seat.getSeatNumber());
                    seatJson.put("location", seat.getLocation());
                    seatJson.put("status", seat.getStatus());
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("seat", seatJson);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Seat not found");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle POST requests (add new seat - admin only)
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
            
            // Extract seat data
            String seatNumber = jsonRequest.getString("seatNumber");
            String location = jsonRequest.getString("location");
            String status = jsonRequest.optString("status", "Available");
            
            // Create new seat
            int newId = DataStore.getNextSeatId();
            Seat newSeat = new Seat(newId, seatNumber, location, status);
            DataStore.seats.add(newSeat);
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Seat added successfully");
            jsonResponse.put("seatId", newId);
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error adding seat: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle PUT requests (update seat - admin only)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Seat ID required");
            } else {
                // Get seat ID from URL
                String idStr = pathInfo.substring(1);
                int id = Integer.parseInt(idStr);
                
                // Find the seat
                Seat seat = DataStore.findSeatById(id);
                if (seat == null) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Seat not found");
                } else {
                    // Read the JSON data from the request
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = request.getReader().readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject jsonRequest = new JSONObject(sb.toString());
                    
                    // Update seat properties
                    if (jsonRequest.has("seatNumber")) {
                        seat.setSeatNumber(jsonRequest.getString("seatNumber"));
                    }
                    if (jsonRequest.has("location")) {
                        seat.setLocation(jsonRequest.getString("location"));
                    }
                    if (jsonRequest.has("status")) {
                        seat.setStatus(jsonRequest.getString("status"));
                    }
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Seat updated successfully");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error updating seat: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle DELETE requests (remove seat - admin only)
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
                jsonResponse.put("message", "Seat ID required");
            } else {
                // Get seat ID from URL
                String idStr = pathInfo.substring(1);
                int id = Integer.parseInt(idStr);
                
                // Find the seat and remove it
                Seat seatToRemove = null;
                for (Seat seat : DataStore.seats) {
                    if (seat.getId() == id) {
                        seatToRemove = seat;
                        break;
                    }
                }
                
                if (seatToRemove != null) {
                    DataStore.seats.remove(seatToRemove);
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Seat deleted successfully");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Seat not found");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error deleting seat: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}