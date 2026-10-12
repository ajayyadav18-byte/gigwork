import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import GigList from './pages/GigList';
import GigDetail from './pages/GigDetail';
import GigNew from './pages/GigNew';
import MyApplications from './pages/MyApplications';
import ReceivedApplications from './pages/ReceivedApplications';
import Profile from './pages/Profile';
import Admin from './pages/Admin';
import { getAuthToken } from './api';

function ProtectedRoute({ children }) {
  const token = getAuthToken();
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Protected routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/gigs"
          element={
            <ProtectedRoute>
              <GigList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/gigs/new"
          element={
            <ProtectedRoute>
              <GigNew />
            </ProtectedRoute>
          }
        />
        <Route
          path="/gigs/:id"
          element={
            <ProtectedRoute>
              <GigDetail />
            </ProtectedRoute>
          }
        />
        <Route
          path="/applications/my"
          element={
            <ProtectedRoute>
              <MyApplications />
            </ProtectedRoute>
          }
        />
        <Route
          path="/applications/received"
          element={
            <ProtectedRoute>
              <ReceivedApplications />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <Admin />
            </ProtectedRoute>
          }
        />

        {/* Fallback route */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
