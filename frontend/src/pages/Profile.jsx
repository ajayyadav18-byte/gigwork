import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser, setAuthSession } from '../api';

export default function Profile() {
  const [user, setUser] = useState(getStoredUser());
  const [name, setName] = useState(user?.name || '');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadProfile() {
      try {
        const data = await apiRequest('/profile');
        setUser(data);
        setName(data.name || '');
      } catch (err) {
        setError(err.message);
      }
    }
    loadProfile();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess('');
    setError('');

    if (!name.trim()) {
      setError('Your display name cannot be empty.');
      return;
    }
    if (name.trim().length > 100) {
      setError('Your display name must be 100 characters or fewer.');
      return;
    }

    setLoading(true);
    try {
      const updated = await apiRequest('/profile/update', {
        method: 'POST',
        body: JSON.stringify({ name: name.trim() }),
      });
      setUser(updated);
      setAuthSession(updated);
      setSuccess('Your profile was updated successfully.');
    } catch (err) {
      setError(err.message || 'Failed to update profile.');
    } finally {
      setLoading(false);
    }
  };

  const initial = user?.name ? user.name.charAt(0).toUpperCase() : 'U';

  return (
    <div className="gigs-page profile-page">
      <header className="navbar">
        <Link className="brand" to="/dashboard">GigWork<span>.</span></Link>

        <nav className="dashboard-nav">
          <Link className="dashboard-nav-link" to="/gigs">Explore gigs</Link>
          <Link className="dashboard-nav-link" to="/gigs/new">Post a gig</Link>
          <Link className="dashboard-nav-link" to="/applications/my">My applications</Link>
          <Link className="dashboard-nav-link" to="/applications/received">Manage applicants</Link>
          <Link className="dashboard-nav-link" to="/dashboard">Dashboard</Link>
          {user?.role === 'ADMIN' && (
            <Link className="dashboard-nav-link" to="/admin">Admin</Link>
          )}
        </nav>

        <div className="gigs-header-actions">
          <Link className="workspace-profile-link" to="/profile" aria-label="Open your profile">
            <span className="workspace-avatar">{initial}</span>
            <span>{user?.name || 'Member'}</span>
          </Link>
          <Link className="button button-secondary" to="/login?logout=true">Log out</Link>
        </div>
      </header>

      <main className="profile-container">
        <section className="profile-heading">
          <p className="eyebrow">ACCOUNT SETTINGS</p>
          <h1>Your profile<span>.</span></h1>
          <p>Manage your personal information and account details.</p>
        </section>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <section className="profile-card">
          <div className="profile-identity">
            <div className="profile-avatar">{initial}</div>
            <div>
              <h2>{user?.name}</h2>
              <p>GigWork member</p>
            </div>
          </div>

          <div className="profile-divider"></div>

          <form onSubmit={handleSubmit} className="profile-form">
            <h3>Personal information</h3>
            <p className="profile-description">
              Update the name displayed on your account.
            </p>

            <label htmlFor="name">Display name</label>
            <input
              id="name"
              name="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              minLength="1"
              maxLength="100"
              autoComplete="name"
              required
            />

            <label htmlFor="email">Email address</label>
            <input
              id="email"
              type="email"
              value={user?.email || ''}
              readOnly
            />
            <p className="field-hint">Your email is currently read-only.</p>

            <label htmlFor="role">Account role</label>
            <input
              id="role"
              type="text"
              value={user?.role || 'USER'}
              readOnly
            />
            <p className="field-hint">Account permissions cannot be changed here.</p>

            <div className="profile-actions">
              <button className="button button-primary" type="submit" disabled={loading}>
                {loading ? 'Saving...' : 'Save changes'}
              </button>
              <Link className="button button-secondary" to="/dashboard">Cancel</Link>
            </div>
          </form>
        </section>

        <section className="profile-shortcuts">
          <Link className="profile-shortcut" to="/gigs">
            <span>01</span>
            <div>
              <h3>Explore opportunities</h3>
              <p>Discover new gigs on the marketplace.</p>
            </div>
            <span className="shortcut-arrow">↗</span>
          </Link>

          <Link className="profile-shortcut" to="/applications/my">
            <span>02</span>
            <div>
              <h3>My applications</h3>
              <p>Keep track of the gigs you've applied for.</p>
            </div>
            <span className="shortcut-arrow">↗</span>
          </Link>
        </section>
      </main>
    </div>
  );
}
