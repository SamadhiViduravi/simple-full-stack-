import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { userAPI } from "../services/api"

const Register = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  })
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
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
    setSuccess("")

    // Basic validation
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match")
      setLoading(false)
      return
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters long")
      setLoading(false)
      return
    }

    try {
      const { confirmPassword, ...submitData } = formData
      const response = await userAPI.register(submitData)

      if (response.data.success) {
        setSuccess("Registration successful! You can now login.")
        setTimeout(() => {
          navigate("/login")
        }, 2000)
      } else {
        setError(response.data.message || "Registration failed")
      }
    } catch (error) {
      setError("Registration failed. Please try again.")
      console.error("Registration error:", error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <div className="form-container">
        <h2 className="page-title">Create Account</h2>
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name:</label>
            <input
              type="text"
              name="name"
              className="form-input"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter your full name"
              required
            />
          </div>

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
              placeholder="Create a password (min 6 characters)"
              required
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Confirm Password:</label>
            <input
              type="password"
              name="confirmPassword"
              className="form-input"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Confirm your password"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: "100%", marginTop: "1rem" }}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </form>

        <p style={{ marginTop: "2rem", textAlign: "center", color: "#6b7280" }}>
          Already have an account?{" "}
          <button
            className="nav-button"
            onClick={() => navigate("/login")}
            style={{
              background: "none",
              border: "none",
              color: "#1e40af",
              cursor: "pointer",
              fontWeight: "600",
              textDecoration: "underline",
            }}
          >
            Sign In
          </button>
        </p>
      </div>
    </div>
  )
}

export default Register