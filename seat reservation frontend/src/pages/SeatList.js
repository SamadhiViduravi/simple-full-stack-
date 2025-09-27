import React, { useState, useEffect } from 'react';
import { seatAPI, reservationAPI } from '../services/api';

const SeatList = ({ user }) => {
  const [seats, setSeats] = useState([]);
  const [selectedDate, setSelectedDate] = useState('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ text: '', type: '' });

  // Set default date to today
  useEffect(() => {
    const today = new Date().toISOString().split('T')[0];
    setSelectedDate(today);
  }, []);

  // Fetch seats
  useEffect(() => {
    fetchSeats();
  }, []);

  const fetchSeats = async () => {
    try {
      const response = await seatAPI.getAll();
      if (response.data.success) {
        setSeats(response.data.seats);
      }
    } catch (error) {
      console.error('Error fetching seats:', error);
      setMessage({ text: 'Error loading seats', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleReserve = async (seatId) => {
    if (!selectedDate) {
      setMessage({ text: 'Please select a date', type: 'error' });
      return;
    }

    try {
      const reservationData = {
        userId: user.userId,
        seatId: seatId,
        date: selectedDate
      };

      const response = await reservationAPI.create(reservationData);
      
      if (response.data.success) {
        setMessage({ text: 'Seat reserved successfully!', type: 'success' });
        fetchSeats(); // Refresh seats to show updated status
      } else {
        setMessage({ text: response.data.message, type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error reserving seat', type: 'error' });
      console.error('Reservation error:', error);
    }
  };

  if (loading) {
    return (
      <div className="container">
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <h2>Available Seats</h2>
      
      {message.text && (
        <div className={`alert alert-${message.type === 'error' ? 'error' : 'success'}`}>
          {message.text}
        </div>
      )}

      <div style={{ marginBottom: '2rem' }}>
        <label className="form-label">Select Date for Reservation:</label>
        <input
          type="date"
          className="form-input"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          min={new Date().toISOString().split('T')[0]}
          style={{ width: '200px' }}
        />
      </div>

      <div className="seat-grid">
        {seats.map(seat => (
          <div key={seat.id} className={`seat-card ${seat.status.toLowerCase()}`}>
            <div className="seat-number">{seat.seatNumber}</div>
            <div className="seat-location">Area: {seat.location}</div>
            <div>Status: <strong>{seat.status}</strong></div>
            
            {seat.status === 'Available' && user && (
              <button
                className="btn btn-success"
                onClick={() => handleReserve(seat.id)}
                style={{ marginTop: '1rem', width: '100%' }}
              >
                Reserve This Seat
              </button>
            )}
          </div>
        ))}
      </div>

      {seats.length === 0 && (
        <div className="alert alert-info">
          No seats available at the moment.
        </div>
      )}
    </div>
  );
};

export default SeatList;