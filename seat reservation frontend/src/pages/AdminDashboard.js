"use client"

import { useState, useEffect } from "react"
import { reservationAPI, seatAPI } from "../services/api"

const AdminDashboard = ({ user }) => {
  const [reservations, setReservations] = useState([])
  const [seats, setSeats] = useState([])
  const [activeTab, setActiveTab] = useState("reservations")
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState({ text: "", type: "" })

  // New seat form state
  const [newSeat, setNewSeat] = useState({
    seatNumber: "",
    location: "",
    status: "Available",
  })

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [resResponse, seatsResponse] = await Promise.all([reservationAPI.getAll(), seatAPI.getAll()])

      if (resResponse.data.success) {
        setReservations(resResponse.data.reservations)
      }
      if (seatsResponse.data.success) {
        setSeats(seatsResponse.data.seats)
      }
    } catch (error) {
      console.error("Error fetching data:", error)
      setMessage({ text: "Error loading data", type: "error" })
    } finally {
      setLoading(false)
    }
  }

  const handleAddSeat = async (e) => {
    e.preventDefault()
    try {
      const response = await seatAPI.add(newSeat)

      if (response.data.success) {
        setMessage({ text: "Seat added successfully", type: "success" })
        setNewSeat({ seatNumber: "", location: "", status: "Available" })
        fetchData() // Refresh seats list
      } else {
        setMessage({ text: response.data.message, type: "error" })
      }
    } catch (error) {
      setMessage({ text: "Error adding seat", type: "error" })
      console.error("Add seat error:", error)
    }
  }

  const handleDeleteSeat = async (seatId) => {
    if (!window.confirm("Are you sure you want to delete this seat?")) {
      return
    }

    try {
      const response = await seatAPI.delete(seatId)

      if (response.data.success) {
        setMessage({ text: "Seat deleted successfully", type: "success" })
        fetchData() // Refresh seats list
      } else {
        setMessage({ text: response.data.message, type: "error" })
      }
    } catch (error) {
      setMessage({ text: "Error deleting seat", type: "error" })
      console.error("Delete seat error:", error)
    }
  }

  if (loading) {
    return (
      <div className="container">
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    )
  }

  return (
    <div className="container">
      <h2 className="page-title">Admin Dashboard</h2>

      {message.text && (
        <div className={`alert alert-${message.type === "error" ? "error" : "success"}`}>{message.text}</div>
      )}

      <div
        style={{
          marginBottom: "3rem",
          display: "flex",
          gap: "1rem",
          justifyContent: "center",
          flexWrap: "wrap",
        }}
      >
        <button
          className={`btn ${activeTab === "reservations" ? "btn-primary" : "btn-secondary"}`}
          onClick={() => setActiveTab("reservations")}
          style={{ minWidth: "160px" }}
        >
          📋 All Reservations
        </button>
        <button
          className={`btn ${activeTab === "seats" ? "btn-primary" : "btn-secondary"}`}
          onClick={() => setActiveTab("seats")}
          style={{ minWidth: "160px" }}
        >
          🪑 Manage Seats
        </button>
      </div>

      {activeTab === "reservations" && (
        <div>
          <h3 style={{ color: "#1e40af", marginBottom: "2rem", fontWeight: "600" }}>📊 All Reservations</h3>
          {reservations.length === 0 ? (
            <div className="alert alert-info">
              <strong>No reservations found</strong>
              <br />
              No reservations have been made yet.
            </div>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>👤 User</th>
                    <th>📧 Email</th>
                    <th>🪑 Seat Number</th>
                    <th>📍 Location</th>
                    <th>📅 Date</th>
                    <th>📊 Status</th>
                  </tr>
                </thead>
                <tbody>
                  {reservations.map((reservation) => (
                    <tr key={reservation.id}>
                      <td style={{ fontWeight: "600" }}>{reservation.userName}</td>
                      <td>{reservation.userEmail}</td>
                      <td style={{ fontWeight: "600", color: "#1e40af" }}>{reservation.seatNumber}</td>
                      <td>{reservation.location}</td>
                      <td>{new Date(reservation.date).toLocaleDateString()}</td>
                      <td>
                        <span
                          className={`status-badge ${reservation.status === "Active" ? "status-active" : "status-inactive"}`}
                        >
                          {reservation.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === "seats" && (
        <div>
          <h3 style={{ color: "#1e40af", marginBottom: "2rem", fontWeight: "600" }}>🪑 Manage Seats</h3>

          <div className="form-container" style={{ maxWidth: "600px", margin: "0 auto 3rem" }}>
            <h4 style={{ color: "#22c55e", marginBottom: "1.5rem", fontWeight: "600" }}>➕ Add New Seat</h4>
            <form onSubmit={handleAddSeat}>
              <div className="form-group">
                <label className="form-label">Seat Number:</label>
                <input
                  type="text"
                  className="form-input"
                  value={newSeat.seatNumber}
                  onChange={(e) => setNewSeat({ ...newSeat, seatNumber: e.target.value })}
                  placeholder="e.g., A-101"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Location:</label>
                <input
                  type="text"
                  className="form-input"
                  value={newSeat.location}
                  onChange={(e) => setNewSeat({ ...newSeat, location: e.target.value })}
                  placeholder="e.g., Floor 1, Wing A"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Status:</label>
                <select
                  className="form-input"
                  value={newSeat.status}
                  onChange={(e) => setNewSeat({ ...newSeat, status: e.target.value })}
                >
                  <option value="Available">Available</option>
                  <option value="Unavailable">Unavailable</option>
                </select>
              </div>

              <button type="submit" className="btn btn-success" style={{ width: "100%" }}>
                ➕ Add Seat
              </button>
            </form>
          </div>

          <h4 style={{ color: "#1e40af", marginBottom: "2rem", fontWeight: "600" }}>📋 Existing Seats</h4>
          {seats.length === 0 ? (
            <div className="alert alert-info">
              <strong>No seats found</strong>
              <br />
              Add your first seat using the form above.
            </div>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>🪑 Seat Number</th>
                    <th>📍 Location</th>
                    <th>📊 Status</th>
                    <th>⚡ Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {seats.map((seat) => (
                    <tr key={seat.id}>
                      <td style={{ fontWeight: "600", color: "#1e40af" }}>{seat.seatNumber}</td>
                      <td>{seat.location}</td>
                      <td>
                        <span
                          className={`status-badge ${seat.status === "Available" ? "status-active" : "status-inactive"}`}
                        >
                          {seat.status}
                        </span>
                      </td>
                      <td>
                        <button
                          className="btn btn-danger"
                          onClick={() => handleDeleteSeat(seat.id)}
                          style={{ padding: "0.5rem 1rem", fontSize: "0.85rem" }}
                        >
                          🗑️ Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default AdminDashboard
