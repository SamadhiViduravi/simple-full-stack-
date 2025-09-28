import { useState, useEffect } from "react"
import { seatAPI, reservationAPI } from "../services/api"

const SeatList = ({ user }) => {
  const [seats, setSeats] = useState([])
  const [selectedDate, setSelectedDate] = useState("")
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState({ text: "", type: "" })

  // Set default date to today
  useEffect(() => {
    const today = new Date().toISOString().split("T")[0]
    setSelectedDate(today)
  }, [])

  // Fetch seats
  useEffect(() => {
    fetchSeats()
  }, [])

  const fetchSeats = async () => {
    try {
      const response = await seatAPI.getAll()
      if (response.data.success) {
        setSeats(response.data.seats || [])
      } else {
        setSeats([])
      }
    } catch (error) {
      console.error("Error fetching seats:", error)
      setMessage({ text: "Error loading seats", type: "error" })
      setSeats([])
    } finally {
      setLoading(false)
    }
  }

  const handleReserve = async (seatId) => {
    if (!selectedDate) {
      setMessage({ text: "Please select a date", type: "error" })
      return
    }

    try {
      const reservationData = {
        userId: user.userId,
        seatId: seatId,
        date: selectedDate,
      }

      const response = await reservationAPI.create(reservationData)

      if (response.data.success) {
        setMessage({ text: "Seat reserved successfully!", type: "success" })
        fetchSeats() // Refresh seats to show updated status
      } else {
        setMessage({ text: response.data.message || "Failed to reserve seat", type: "error" })
      }
    } catch (error) {
      setMessage({ text: "Error reserving seat", type: "error" })
      console.error("Reservation error:", error)
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
      <h2 className="page-title">Available Seats</h2>

      {message.text && (
        <div className={`alert alert-${message.type === "error" ? "error" : "success"}`}>{message.text}</div>
      )}

      <div
        style={{
          marginBottom: "3rem",
          padding: "1.5rem",
          background: "white",
          borderRadius: "16px",
          boxShadow: "0 4px 20px rgba(0, 0, 0, 0.08)",
          border: "1px solid rgba(30, 64, 175, 0.05)",
        }}
      >
        <label className="form-label" style={{ color: "#1e40af", fontWeight: "600" }}>
          Select Date for Reservation:
        </label>
        <input
          type="date"
          className="form-input"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          min={new Date().toISOString().split("T")[0]}
          style={{ maxWidth: "250px", marginTop: "0.5rem" }}
        />
      </div>

      <div className="seat-grid">
        {seats.map((seat) => (
          <div key={seat.id} className={`seat-card ${seat.status ? seat.status.toLowerCase() : ""}`}>
            <div className="seat-number">{seat.seatNumber || "N/A"}</div>
            <div className="seat-location">📍 {seat.location || "N/A"}</div>
            <div style={{ marginBottom: "1rem" }}>
              <span className={`status-badge ${seat.status === "Available" ? "status-active" : "status-inactive"}`}>
                {seat.status || "Unknown"}
              </span>
            </div>

            {seat.status === "Available" && user && (
              <button className="btn btn-success" onClick={() => handleReserve(seat.id)} style={{ width: "100%" }}>
                🎯 Reserve This Seat
              </button>
            )}
          </div>
        ))}
      </div>

      {!seats || seats.length === 0 && (
        <div className="alert alert-info">
          <strong>No seats available</strong>
          <br />
          Please check back later or contact support.
        </div>
      )}
    </div>
  )
}

export default SeatList