import axios from 'axios';

// Base URL for our backend API
const API_BASE_URL = 'http://localhost:8080/SeatReservationSystem/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// User API calls
export const userAPI = {
  // Register a new user
  register: (userData) => api.post('/users', userData),
  
  // Login user
  login: (credentials) => api.post('/users/login', credentials),
};

// Seat API calls
export const seatAPI = {
  // Get all seats
  getAll: () => api.get('/seats'),
  
  // Add new seat (admin only)
  add: (seatData) => api.post('/seats', seatData),
  
  // Update seat (admin only)
  update: (id, seatData) => api.put(`/seats/${id}`, seatData),
  
  // Delete seat (admin only)
  delete: (id) => api.delete(`/seats/${id}`),
};

// Reservation API calls
export const reservationAPI = {
  // Get all reservations or by user
  getAll: (userId = null) => 
    userId ? api.get(`/reservations?userId=${userId}`) : api.get('/reservations'),
  
  // Create new reservation
  create: (reservationData) => api.post('/reservations', reservationData),
  
  // Cancel reservation
  cancel: (id) => api.delete(`/reservations/${id}`),
};

export default api;