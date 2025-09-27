import React, { useState, useEffect } from 'react';
import { reservationAPI, seatAPI } from '../services/api';

const AdminDashboard = ({ user }) => {
  const [reservations, setReservations] = useState([]);
  const [seats, setSeats] = useState([]);
  const [activeTab, setActiveTab] = useState('reservations');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ text: '', type: '' });

  // New seat form state
  const [newSeat, setNewSeat] = useState({
    seatNumber: '',
    location: '',
    status: 'Available'
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [resResponse, seatsResponse] = await Promise.all([
        reservationAPI.getAll(),
        seatAPI.getAll()
      ]);

      if (resResponse.data.success) {
        setReservations(resResponse.data.reservations);
      }
      if (seatsResponse.data.success) {
        setSeats(seatsResponse.data.seats);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      setMessage({ text: 'Error loading data', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleAddSeat = async (e) => {
    e.preventDefault();
    try {
      const response = await seatAPI.add(newSeat);
      
      if (response.data.success) {
        setMessage({ text: 'Seat added successfully', type: 'success' });
        setNewSeat({ seatNumber: '', location: '', status: 'Available' });
        fetchData(); // Refresh seats list
      } else {
        setMessage({ text: response.data.message, type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error adding seat', type: 'error' });
      console.error('Add seat error:', error);
    }
  };

  const handleDeleteSeat = async (seatId) => {
    if (!window.confirm('Are you sure you want to delete this seat?')) {
      return;
    }

    try {
      const response = await seatAPI.delete(seatId);
      
      if (response.data.success) {
        setMessage({ text: 'Seat deleted successfully', type: 'success' });
        fetchData(); // Refresh seats list
      } else {
        setMessage({ text: response.data.message, type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error deleting seat', type: 'error' });
      console.error('Delete seat error:', error);
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
      <h2>Admin Dashboard</h2>
      
      {message.text && (
        <div className={`alert alert-${message.type === 'error' ? 'error' : 'success'}`}>
          {message.text}
        </div>
      )}

      <div style={{ marginBottom: '2rem' }}>
        <button 
          className={`btn ${activeTab === 'reservations' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('reservations')}
        >
          All Reservations
        </button>
        <button 
          className={`btn ${activeTab === 'seats' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('seats')}
          style={{ marginLeft: '1rem' }}
        >
          Manage Seats
        </button>
      </div>

      {activeTab === 'reservations' && (
        <div>
          <h3>All Reservations</h3>
          {reservations.length === 0 ? (
            <div className="alert alert-info">No reservations found.</div>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Email</th>
                  <th>Seat Number</th>
                  <th>Location</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map(reservation => (
                  <tr key={reservation.id}>
                    <td>{reservation.userName}</td>
                    <td>{reservation.userEmail}</td>
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
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {activeTab === 'seats' && (
        <div>
          <h3>Manage Seats</h3>
          
          <div className="form-container" style={{ maxWidth: '500px' }}>
            <h4>Add New Seat</h4>
            <form onSubmit={handleAddSeat}>
              <div className="form-group">
                <label className="form-label">Seat Number:</label>
                <input
                  type="text"
                  className="form-input"
                  value={newSeat.seatNumber}
                  onChange={(e) => setNewSeat({...newSeat, seatNumber: e.target.value})}
                  required
                />
              </div>
              
              <div className="form-group">
                <label className="form-label">Location:</label>
                <input
                  type="text"
                  className="form-input"
                  value={newSeat.location}
                  onChange={(e) => setNewSeat({...newSeat, location: e.target.value})}
                  required
                />
              </div>
              
              <div className="form-group">
                <label className="form-label">Status:</label>
                <select
                  className="form-input"
                  value={newSeat.status}
                  onChange={(e) => setNewSeat({...newSeat, status: e.target.value})}
                >
                  <option value="Available">Available</option>
                  <option value="Unavailable">Unavailable</option>
                </select>
              </div>
              
              <button type="submit" className="btn btn-primary">Add Seat</button>
            </form>
          </div>

          <h4 style={{ marginTop: '2rem' }}>Existing Seats</h4>
          {seats.length === 0 ? (
            <div className="alert alert-info">No seats found.</div>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>Seat Number</th>
                  <th>Location</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {seats.map(seat => (
                  <tr key={seat.id}>
                    <td>{seat.seatNumber}</td>
                    <td>{seat.location}</td>
                    <td>
                      <span style={{ 
                        color: seat.status === 'Available' ? 'green' : 'red',
                        fontWeight: 'bold'
                      }}>
                        {seat.status}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-danger"
                        onClick={() => handleDeleteSeat(seat.id)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;