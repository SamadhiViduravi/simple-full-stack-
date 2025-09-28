import { useState, useEffect } from "react"
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import Login from "./pages/Login"
import Register from "./pages/Register"
import SeatList from "./pages/SeatList"
import MyReservations from "./pages/MyReservations"
import AdminDashboard from "./pages/AdminDashboard"
import Navigation from "./components/Navigation"
import "./App.css"

function App() {
  // State to track if user is logged in
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Check if user is logged in when app starts
  useEffect(() => {
    const savedUser = localStorage.getItem("user")
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser))
      } catch (error) {
        console.error("Error parsing saved user:", error)
        localStorage.removeItem("user")
      }
    }
    setLoading(false)
  }, [])

  // Handle user login
  const handleLogin = (userData) => {
    setUser(userData)
    localStorage.setItem("user", JSON.stringify(userData))
  }

  // Handle user logout
  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem("user")
  }

  // Show loading spinner while checking authentication
  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <p>Loading...</p>
      </div>
    )
  }

  return (
    <Router>
      <div className="App">
        <header className="header">
          <div className="container">
            <div className="header-content">
              <div className="logo">Seat Reservation System</div>
              <Navigation user={user} onLogout={handleLogout} />
            </div>
          </div>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<Navigate to={user ? "/seats" : "/login"} />} />
            <Route path="/login" element={user ? <Navigate to="/seats" /> : <Login onLogin={handleLogin} />} />
            <Route path="/register" element={user ? <Navigate to="/seats" /> : <Register />} />
            <Route path="/seats" element={user ? <SeatList user={user} /> : <Navigate to="/login" />} />
            <Route path="/reservations" element={user ? <MyReservations user={user} /> : <Navigate to="/login" />} />
            <Route
              path="/admin"
              element={user && user.role === "ADMIN" ? <AdminDashboard user={user} /> : <Navigate to="/login" />}
            />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App