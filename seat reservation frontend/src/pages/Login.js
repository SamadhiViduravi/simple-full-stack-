import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userAPI } from '../services/api';

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await userAPI.login(formData);
      
      if (response.data.success) {
        // Save user info to localStorage
        localStorage.setItem('user', JSON.stringify(response.data));
        onLogin(response.data);
        navigate('/seats');
      } else {
        setError(response.data.message);
      }
    } catch (error) {
      setError('Login failed. Please check your credentials and try again.');
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="form-container">
        <h2>Login to Seat Reservation System</h2>
        {error && <div className="alert alert-error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email:</label>
            <input
              type="email"
              name="email"
              className="form-input"
              value={formData.email}
              onChange={handleChange}
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
              required
            />
          </div>
          
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        
        <p style={{ marginTop: '1rem', textAlign: 'center' }}>
          Don't have an account?{' '}
          <button 
            className="nav-button" 
            onClick={() => navigate('/register')}
            style={{ background: 'none', border: 'none', color: '#3498db', cursor: 'pointer' }}
          >
            Register here
          </button>
        </p>

        <div style={{ marginTop: '2rem', padding: '1rem', background: '#f8f9fa', borderRadius: '4px' }}>
          <h4>Demo Accounts:</h4>
          <p><strong>Admin:</strong> admin@office.com / admin123</p>
          <p><strong>Intern:</strong> john@office.com / intern123</p>
        </div>
      </div>
    </div>
  );
};

export default Login;