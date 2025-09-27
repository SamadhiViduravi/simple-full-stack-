import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import SeatList from './pages/SeatList';
import MyReservations from './pages/MyReservations';
import AdminDashboard from './pages/AdminDashboard';
import Navigation from './components/Navigation';
import './App.css';

function App() {
  // State to track if user