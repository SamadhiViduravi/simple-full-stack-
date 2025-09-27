package com.seatreservation.model;

// This class represents a reservation made by an intern
public class Reservation {
    private int id;
    private int userId;
    private int seatId;
    private String date;
    private String status; // Can be "Active" or "Cancelled"
    
    // Constructor
    public Reservation(int id, int userId, int seatId, String date, String status) {
        this.id = id;
        this.userId = userId;
        this.seatId = seatId;
        this.date = date;
        this.status = status;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getSeatId() {
        return seatId;
    }
    
    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}