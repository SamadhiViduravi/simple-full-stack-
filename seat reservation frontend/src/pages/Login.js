import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { userAPI } from "../services/api"

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  })
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    try {
      const response = await userAPI.login(formData)

      if (response.data.success) {
        // Save user info to localStorage
        localStorage.setItem("user", JSON.stringify(response.data))
        onLogin(response.data)
        navigate("/seats")
      } else {
        setError(response.data.message || "Login failed")
      }
    } catch (error) {
      setError("Login failed. Please check your credentials and try again.")
      console.error("Login error:", error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <div className="form-container">
        <h2 className="page-title">Welcome Back</h2>
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address:</label>
            <input
              type="email"
              name="email"
              className="form-input"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password:</label>
            <input
              type="password"
              name="password"
              className="form-input"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: "100%", marginTop: "1rem" }}
          >
            {loading ? "Signing In..." : "Sign In"}
          </button>
        </form>

        <p style={{ marginTop: "2rem", textAlign: "center", color: "#6b7280" }}>
          Don't have an account?{" "}
          <button
            className="nav-button"
            onClick={() => navigate("/register")}
            style={{
              background: "none",
              border: "none",
              color: "#1e40af",
              cursor: "pointer",
              fontWeight: "600",
              textDecoration: "underline",
            }}
          >
            Create Account
          </button>
        </p>

        <div
          style={{
            marginTop: "2rem",
            padding: "1.5rem",
            background: "linear-gradient(135deg, rgba(30, 64, 175, 0.05) 0%, rgba(34, 197, 94, 0.05) 100%)",
            borderRadius: "12px",
            border: "1px solid rgba(30, 64, 175, 0.1)",
          }}
        >
          <h4 style={{ color: "#1e40af", marginBottom: "1rem", fontWeight: "600" }}>Demo Accounts:</h4>
          <div style={{ fontSize: "0.9rem", lineHeight: "1.8" }}>
            <p>
              <strong style={{ color: "#22c55e" }}>Admin:</strong> admin@office.com / admin123
            </p>
            <p>
              <strong style={{ color: "#1e40af" }}>User:</strong> john@office.com / intern123
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login