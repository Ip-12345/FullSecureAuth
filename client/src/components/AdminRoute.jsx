import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';

const AdminRoute = ({ children }) => {
  const { userRole, isLoggedIn } = useContext(AppContext);
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }
  if(userRole !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }
  return children; 
};

export default AdminRoute;
