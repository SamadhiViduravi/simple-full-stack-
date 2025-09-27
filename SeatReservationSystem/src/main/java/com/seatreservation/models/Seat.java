package com.seatreservation.model;

// This class represents a seat in our office
public class Seat {
    private int id;
    private String seatNumber;
    private String location;
    private String status; // Can be "Available" or "Unavailable"
    
    // Constructor
    public Seat(int id, String seatNumber, String location, String status) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.location = location;
        this.status = status;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}