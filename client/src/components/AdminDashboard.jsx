import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { Card, Button } from 'react-bootstrap';
import Menubar from './Menubar';

function AdminDashboard() {
  const navigate = useNavigate();
  const { userData } = useContext(AppContext);

  return (
    <div>
    <Menubar/>
    <div className="position-relative min-vh-100 text-white" style={{ background: 'linear-gradient(90deg, #6a5af9, #8268f9)' }}>
      

      <div className="container py-5">
        <h2 className="text-center mb-5 fw-bold">Welcome, {userData?.name || 'Admin'}</h2>

        <div className="row g-4">
          <div className="col-md-4">
            <Card className="h-100 shadow" style={{ backgroundColor: '#ffffff1a', border: 'none', color: 'white' }}>
              <Card.Body>
                <Card.Title>Manage Users</Card.Title>
                <Card.Text>View, promote, or remove registered users from the system.</Card.Text>
                <Button variant="light" onClick={() => navigate('/admin/users')}>
                  Go to Users
                </Button>
              </Card.Body>
            </Card>
          </div>

          <div className="col-md-4">
            <Card className="h-100 shadow" style={{ backgroundColor: '#ffffff1a', border: 'none', color: 'white' }}>
              <Card.Body>
                <Card.Title>System Logs</Card.Title>
                <Card.Text>Monitor login activity, password resets, and OTP sends.</Card.Text>
                <Button variant="light" onClick={() => navigate('/admin/logs')}>
                  View Logs
                </Button>
              </Card.Body>
            </Card>
          </div>

          <div className="col-md-4">
            <Card className="h-100 shadow" style={{ backgroundColor: '#ffffff1a', border: 'none', color: 'white' }}>
              <Card.Body>
                <Card.Title>Account Settings</Card.Title>
                <Card.Text>Update your profile, change your password, or manage privileges.</Card.Text>
                <Button variant="light" onClick={() => navigate('/admin/settings')}>
                  Settings
                </Button>
              </Card.Body>
            </Card>
          </div>
        </div>
      </div>
    </div>
    </div>
  );
}

export default AdminDashboard;
