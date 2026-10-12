import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { clearAuthSession } from '../api';

export default function WorkspaceHeader({ user }) {
  const navigate = useNavigate();

  const handleLogout = (e) => {
    e.preventDefault();
    clearAuthSession();
    navigate('/login?logout=true');
  };

  const initial = user?.name ? user.name.charAt(0).toUpperCase() : 'U';

  return (
    <header className="site-header">
      <nav className="navbar container" aria-label="Main navigation">
        <Link className="brand" to="/dashboard">
          <span className="brand-mark">G</span>
          <span>gig<span className="brand-accent">work</span></span>
        </Link>
        <div className="dashboard-nav">
          <Link className="dashboard-nav-link" to="/gigs">Explore gigs</Link>
          <Link className="dashboard-nav-link" to="/gigs/new">Post a gig</Link>
          <Link className="dashboard-nav-link" to="/applications/my">My applications</Link>
          <Link className="dashboard-nav-link" to="/applications/received">Manage applicants</Link>
          <Link className="dashboard-nav-link" to="/dashboard">Dashboard</Link>
          {user?.role === 'ADMIN' && (
            <Link className="dashboard-nav-link" to="/admin">Admin</Link>
          )}
        </div>
        <div className="gigs-header-actions">
          <Link className="workspace-profile-link" to="/profile" aria-label="Open your profile">
            <span className="workspace-avatar">{initial}</span>
            <span>{user?.name || 'Member'}</span>
          </Link>
          <button className="button button-secondary button-small" type="button" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </nav>
    </header>
  );
}
