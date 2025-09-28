package com.seatreservation.storage;

import com.seatreservation.models.*;
import java.util.ArrayList;
import java.util.List;

// This class acts as our in-memory database
// We use ArrayLists to store our data instead of a real database
public class DataStore {
    // Static lists that will hold our data while the application is running
    public static List<User> users = new ArrayList<>();
    public static List<Seat> seats = new ArrayList<>();
    public static List<Reservation> reservations = new ArrayList<>();
    
    // This block runs when the class is first loaded
    // We're adding some sample data to start with
    static {
        // Add some sample users
        users.add(new User(1, "Admin User", "admin@office.com", "admin123", "ADMIN"));
        users.add(new User(2, "John Intern", "john@office.com", "intern123", "INTERN"));
        users.add(new User(3, "Jane Intern", "jane@office.com", "intern123", "INTERN"));
        
        // Add some sample seats
        seats.add(new Seat(1, "A1", "Area A", "Available"));
        seats.add(new Seat(2, "A2", "Area A", "Available"));
        seats.add(new Seat(3, "B1", "Area B", "Available"));
        seats.add(new Seat(4, "B2", "Area B", "Unavailable"));
        seats.add(new Seat(5, "C1", "Area C", "Available"));
        
        // Add some sample reservations
        reservations.add(new Reservation(1, 2, 1, "2024-01-15", "Active"));
        reservations.add(new Reservation(2, 3, 3, "2024-01-15", "Active"));
    }
    
    // Method to get the next available ID for users
    public static int getNextUserId() {
        if (users.isEmpty()) {
            return 1;
        }
        return users.get(users.size() - 1).getId() + 1;
    }
    
    // Method to get the next available ID for seats
    public static int getNextSeatId() {
        if (seats.isEmpty()) {
            return 1;
        }
        return seats.get(seats.size() - 1).getId() + 1;
    }
    
    // Method to get the next available ID for reservations
    public static int getNextReservationId() {
        if (reservations.isEmpty()) {
            return 1;
        }
        return reservations.get(reservations.size() - 1).getId() + 1;
    }
    
    // Method to find a user by email
    public static User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    // Method to find a user by ID
    public static User findUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    // Method to find a seat by ID
    public static Seat findSeatById(int id) {
        for (Seat seat : seats) {
            if (seat.getId() == id) {
                return seat;
            }
        }
        return null;
    }
}