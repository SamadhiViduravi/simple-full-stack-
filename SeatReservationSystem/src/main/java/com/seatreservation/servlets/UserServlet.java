package com.seatreservation.servlets;

import com.seatreservation.model.User;
import com.seatreservation.storage.DataStore;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

// This servlet handles user-related operations like registration and login
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    
    // Handle POST requests (registration and login)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            // Get the path info to determine what operation to perform
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // This is a registration request
                handleRegistration(request, jsonResponse);
            } else if (pathInfo.equals("/login")) {
                // This is a login request
                handleLogin(request, jsonResponse);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid endpoint");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    // Handle user registration
    private void handleRegistration(HttpServletRequest request, JSONObject jsonResponse) {
        try {
            // Read the JSON data from the request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Extract user data from the request
            String name = jsonRequest.getString("name");
            String email = jsonRequest.getString("email");
            String password = jsonRequest.getString("password");
            String role = jsonRequest.optString("role", "INTERN");
            
            // Check if user already exists
            if (DataStore.findUserByEmail(email) != null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "User with this email already exists");
                return;
            }
            
            // Create new user
            int newId = DataStore.getNextUserId();
            User newUser = new User(newId, name, email, password, role);
            DataStore.users.add(newUser);
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "User registered successfully");
            jsonResponse.put("userId", newId);
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Registration failed: " + e.getMessage());
        }
    }
    
    // Handle user login
    private void handleLogin(HttpServletRequest request, JSONObject jsonResponse) {
        try {
            // Read the JSON data from the request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Extract login credentials
            String email = jsonRequest.getString("email");
            String password = jsonRequest.getString("password");
            
            // Find user by email
            User user = DataStore.findUserByEmail(email);
            if (user == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "User not found");
                return;
            }
            
            // Check password (in a real app, we would hash passwords!)
            if (!user.getPassword().equals(password)) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid password");
                return;
            }
            
            // Login successful
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Login successful");
            jsonResponse.put("userId", user.getId());
            jsonResponse.put("name", user.getName());
            jsonResponse.put("email", user.getEmail());
            jsonResponse.put("role", user.getRole());
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Login failed: " + e.getMessage());
        }
    }
}