import React, { useState, useEffect } from 'react';
import { reservationAPI } from '../services/api';

const MyReservations = ({ user }) => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ text: '', type: '' });

  useEffect(() => {
    if (user) {
      fetchReservations();
    }
  }, [user]);

  const fetchReservations = async () => {
    try {
      const response = await reservationAPI.getAll(user.userId);
      if (response.data.success) {
        setReservations(response.data.reservations);
      }
    } catch (error) {
      console.error('Error fetching reservations:', error);
      setMessage({ text: 'Error loading reservations', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (reservationId) => {
    if (!window.confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }

    try {
      const response = await reservationAPI.cancel(reservationId);
      
      if (response.data.success) {
        setMessage({ text: 'Reservation cancelled successfully', type: 'success' });
        fetchReservations(); // Refresh the list
      } else {
        setMessage({ text: response.data.message, type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error cancelling reservation', type: 'error' });
      console.error('Cancellation error:', error);
    }
  };

  const isFutureDate = (date) => {
    return new Date(date) >= new Date().setHours(0, 0, 0, 0);
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
      <h2>My Reservations</h2>
      
      {message.text && (
        <div className={`alert alert-${message.type === 'error' ? 'error' : 'success'}`}>
          {message.text}
        </div>
      )}

      {reservations.length === 0 ? (
        <div className="alert alert-info">
          You don't have any reservations yet.
        </div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Seat Number</th>
              <th>Location</th>
              <th>Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservations.map(reservation => (
              <tr key={reservation.id}>
                <td>{reservation.seatNumber}</td>
                <td>{reservation.location}</td>
                <td>{reservation.date}</td>
                <td>
                  <span style={{ 
                    color: reservation.status === 'Active' ? 'green' : 'red',
                    fontWeight: 'bold'
                  }}>
                    {reservation.status}
                  </span>
                </td>
                <td>
                  {reservation.status === 'Active' && isFutureDate(reservation.date) && (
                    <button
                      className="btn btn-danger"
                      onClick={() => handleCancel(reservation.id)}
                    >
                      Cancel
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default MyReservations;