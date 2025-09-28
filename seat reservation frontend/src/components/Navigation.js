import { useNavigate } from "react-router-dom"

const Navigation = ({ user, onLogout }) => {
  const navigate = useNavigate()

  const handleLogout = () => {
    onLogout()
    navigate("/login")
  }

  const handleNavigation = (path) => {
    navigate(path)
  }

  return (
    <nav className="nav">
      {user ? (
        <>
          <button className="nav-button" onClick={() => handleNavigation("/seats")}>
            View Seats
          </button>
          <button className="nav-button" onClick={() => handleNavigation("/reservations")}>
            My Reservations
          </button>
          {user.role === "ADMIN" && (
            <button className="nav-button" onClick={() => handleNavigation("/admin")}>
              Admin Dashboard
            </button>
          )}
          <span style={{ color: "white", margin: "0 1rem" }}>
            Welcome, {user.name} ({user.role})
          </span>
          <button className="nav-button" onClick={handleLogout} style={{ backgroundColor: "#e74c3c" }}>
            Logout
          </button>
        </>
      ) : (
        <>
          <button className="nav-button" onClick={() => handleNavigation("/login")}>
            Login
          </button>
          <button className="nav-button" onClick={() => handleNavigation("/register")}>
            Register
          </button>
        </>
      )}
    </nav>
  )
}

export default Navigation