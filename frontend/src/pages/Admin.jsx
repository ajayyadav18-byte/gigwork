import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest, getStoredUser } from '../api';

export default function Admin() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const user = getStoredUser();

  const loadAdminData = async () => {
    try {
      const res = await apiRequest('/admin');
      setData(res);
    } catch (err) {
      setError(err.message || 'Access denied.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAdminData();
  }, []);

  const handleGigStatus = async (id, status) => {
    const confirmMsg = status === 'REMOVED'
      ? 'Remove this listing from the public marketplace?'
      : 'Reopen this listing?';
    if (!window.confirm(confirmMsg)) return;

    setError('');
    setSuccess('');
    try {
      await apiRequest(`/admin/gigs/${id}/status`, {
        method: 'POST',
        body: JSON.stringify({ status }),
      });
      setSuccess(status === 'REMOVED' ? 'The gig has been removed.' : 'The gig has been reopened.');
      loadAdminData();
    } catch (err) {
      setError(err.message || 'Failed to update gig status.');
    }
  };

  const initial = user?.name ? user.name.charAt(0).toUpperCase() : 'U';

  return (
    <div className="gigs-page admin-page">
      <header className="navbar">
        <Link className="brand" to="/dashboard">GigWork<span>.</span></Link>

        <nav className="dashboard-nav">
          <Link className="dashboard-nav-link" to="/gigs">Explore gigs</Link>
          <Link className="dashboard-nav-link" to="/gigs/new">Post a gig</Link>
          <Link className="dashboard-nav-link" to="/applications/my">My applications</Link>
          <Link className="dashboard-nav-link" to="/applications/received">Manage applicants</Link>
          <Link className="dashboard-nav-link" to="/dashboard">Dashboard</Link>
          <Link className="dashboard-nav-link active" to="/admin">Admin</Link>
        </nav>

        <div className="gigs-header-actions">
          <Link className="workspace-profile-link" to="/profile" aria-label="Open your profile">
            <span className="workspace-avatar">{initial}</span>
            <span>{user?.name || 'Member'}</span>
          </Link>
          <Link className="button button-secondary" to="/login?logout=true">Log out</Link>
        </div>
      </header>

      <main className="admin-container">
        <section className="admin-hero">
          <div>
            <p className="eyebrow">PLATFORM MANAGEMENT</p>
            <h1>Control center<span>.</span></h1>
            <p>Monitor members, review listings, and keep the marketplace trustworthy.</p>
          </div>
          <div className="admin-live-label">
            <span></span> Admin workspace
          </div>
        </section>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        {loading ? (
          <p style={{ marginTop: '30px' }}>Loading admin dashboard...</p>
        ) : data ? (
          <>
            <section className="admin-stats">
              <article className="admin-stat-card">
                <span>REGISTERED MEMBERS</span>
                <strong>{data.totalUsers}</strong>
                <p>Accounts in the database</p>
              </article>
              <article className="admin-stat-card">
                <span>TOTAL LISTINGS</span>
                <strong>{data.totalGigs}</strong>
                <p>All recorded gigs</p>
              </article>
              <article className="admin-stat-card">
                <span>OPEN OPPORTUNITIES</span>
                <strong>{data.openGigs}</strong>
                <p>Currently available listings</p>
              </article>
            </section>

            <section className="admin-section">
              <div className="admin-section-heading">
                <div>
                  <p className="eyebrow">COMMUNITY</p>
                  <h2>Registered users</h2>
                </div>
                <span className="admin-count">{data.users.length} members</span>
              </div>

              <div className="admin-table-wrap">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>Member</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Member ID</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.users.map((u) => {
                      const uInit = u.name ? u.name.charAt(0).toUpperCase() : 'U';
                      return (
                        <tr key={u.id}>
                          <td>
                            <div className="admin-member">
                              <div className="admin-avatar">{uInit}</div>
                              <strong>{u.name}</strong>
                            </div>
                          </td>
                          <td>{u.email}</td>
                          <td>
                            <span className="admin-role">{u.role}</span>
                          </td>
                          <td>{u.id}</td>
                        </tr>
                      );
                    })}
                    {data.users.length === 0 && (
                      <tr>
                        <td colSpan="4">No registered users found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>

            <section className="admin-section">
              <div className="admin-section-heading">
                <div>
                  <p className="eyebrow">CONTENT MODERATION</p>
                  <h2>Marketplace listings</h2>
                </div>
                <Link className="button button-secondary" to="/gigs">View marketplace ↗</Link>
              </div>

              <div className="admin-listings">
                {data.gigs.map((g) => (
                  <article key={g.id} className="admin-listing">
                    <div className="admin-listing-main">
                      <div className="admin-listing-top">
                        <span className="gig-category">{g.category}</span>
                        <span className={`admin-listing-status ${g.status === 'OPEN' ? 'listing-open' : 'listing-other'}`}>
                          {g.status}
                        </span>
                      </div>
                      <h3>{g.title}</h3>
                      <p className="admin-listing-description">{g.description}</p>
                      <div className="admin-listing-meta">
                        <span>{g.location || 'Remote'}</span>
                        <span>₹{Number(g.budget).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
                        <span>Listing #{g.id}</span>
                      </div>
                    </div>

                    <div className="admin-listing-actions">
                      {g.status === 'OPEN' ? (
                        <button
                          className="button btn-danger"
                          type="button"
                          onClick={() => handleGigStatus(g.id, 'REMOVED')}
                        >
                          Remove listing
                        </button>
                      ) : (
                        <button
                          className="button button-primary"
                          type="button"
                          onClick={() => handleGigStatus(g.id, 'OPEN')}
                        >
                          Reopen listing
                        </button>
                      )}
                    </div>
                  </article>
                ))}

                {data.gigs.length === 0 && (
                  <div className="admin-empty">
                    <h3>No listings yet</h3>
                    <p>Gigs published on the platform will appear here.</p>
                  </div>
                )}
              </div>
            </section>
          </>
        ) : null}
      </main>
    </div>
  );
}
