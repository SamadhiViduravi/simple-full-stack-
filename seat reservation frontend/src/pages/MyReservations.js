import { useState, useEffect } from "react"
import { reservationAPI } from "../services/api"

const MyReservations = ({ user }) => {
  const [reservations, setReservations] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState({ text: "", type: "" })

  useEffect(() => {
    if (user) {
      fetchReservations()
    }
  }, [user])

  const fetchReservations = async () => {
    try {
      const response = await reservationAPI.getAll(user.userId)
      if (response.data.success) {
        setReservations(response.data.reservations || [])
      } else {
        setReservations([])
      }
    } catch (error) {
      console.error("Error fetching reservations:", error)
      setMessage({ text: "Error loading reservations", type: "error" })
      setReservations([])
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = async (reservationId) => {
    if (!window.confirm("Are you sure you want to cancel this reservation?")) {
      return
    }

    try {
      const response = await reservationAPI.cancel(reservationId)

      if (response.data.success) {
        setMessage({ text: "Reservation cancelled successfully", type: "success" })
        fetchReservations() // Refresh the list
      } else {
        setMessage({ text: response.data.message || "Failed to cancel reservation", type: "error" })
      }
    } catch (error) {
      setMessage({ text: "Error cancelling reservation", type: "error" })
      console.error("Cancellation error:", error)
    }
  }

  const isFutureDate = (date) => {
    return new Date(date) >= new Date().setHours(0, 0, 0, 0)
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
      <h2 className="page-title">My Reservations</h2>

      {message.text && (
        <div className={`alert alert-${message.type === "error" ? "error" : "success"}`}>{message.text}</div>
      )}

      {!reservations || reservations.length === 0 ? (
        <div className="alert alert-info">
          <strong>No reservations found</strong>
          <br />
          You haven't made any reservations yet. Visit the seats page to book your first seat!
        </div>
      ) : (
        <div style={{ overflowX: "auto" }}>
          <table className="table">
            <thead>
              <tr>
                <th>🪑 Seat Number</th>
                <th>📍 Location</th>
                <th>📅 Date</th>
                <th>📊 Status</th>
                <th>⚡ Actions</th>
              </tr>
            </thead>
            <tbody>
              {reservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td style={{ fontWeight: "600", color: "#1e40af" }}>{reservation.seatNumber || "N/A"}</td>
                  <td>{reservation.location || "N/A"}</td>
                  <td>{reservation.date ? new Date(reservation.date).toLocaleDateString() : "N/A"}</td>
                  <td>
                    <span
                      className={`status-badge ${reservation.status === "Active" ? "status-active" : "status-inactive"}`}
                    >
                      {reservation.status || "Unknown"}
                    </span>
                  </td>
                  <td>
                    {reservation.status === "Active" && reservation.date && isFutureDate(reservation.date) && (
                      <button
                        className="btn btn-danger"
                        onClick={() => handleCancel(reservation.id)}
                        style={{ padding: "0.5rem 1rem", fontSize: "0.85rem" }}
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default MyReservations