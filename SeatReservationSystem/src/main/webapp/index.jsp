<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Seat Reservation System</title>
</head>
<body>
    <h1>Seat Reservation System Backend</h1>
    <p>Backend server is running successfully!</p>
    <p>Use the React frontend to interact with the system.</p>
    
    <h2>API Endpoints:</h2>
    <ul>
        <li>POST /api/users - Register new user</li>
        <li>POST /api/users/login - User login</li>
        <li>GET /api/seats - Get all seats</li>
        <li>POST /api/seats - Add new seat (admin only)</li>
        <li>GET /api/reservations - Get reservations</li>
        <li>POST /api/reservations - Create reservation</li>
    </ul>
</body>
</html>