import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const user = getStoredUser();

  useEffect(() => {
    async function loadDashboard() {
      try {
        const res = await apiRequest('/dashboard');
        setData(res);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div className="gigs-page workspace-page">
        <WorkspaceHeader user={user} />
        <main className="container dashboard-page">
          <p style={{ marginTop: '40px' }}>Loading workspace...</p>
        </main>
      </div>
    );
  }

  const currentUser = data?.user || user;
  const gigCount = data?.gigCount || 0;
  const applicationCount = data?.applicationCount || 0;
  const receivedCount = data?.receivedApplicationCount || 0;
  const latestGigs = data?.latestGigs || [];

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={currentUser} />

      <main className="container dashboard-page">
        {error && <div className="alert alert-error">{error}</div>}

        <section className="dashboard-welcome">
          <div className="dashboard-welcome-copy">
            <span className="section-eyebrow">
              <span className="status-dot"></span> YOUR GIGWORK WORKSPACE
            </span>
            <h1>
              Welcome back, <span className="gradient-text">{currentUser?.name}</span>.
            </h1>
            <p>Your next opportunity, application update, or great hire starts here.</p>
            <div className="dashboard-primary-actions">
              <Link className="button button-primary" to="/gigs">
                Explore opportunities <span aria-hidden="true">→</span>
              </Link>
              <Link className="button button-secondary" to="/gigs/new">
                ＋ Post a gig
              </Link>
            </div>
          </div>
          <div className="dashboard-hero-note" aria-hidden="true">
            <span className="dashboard-note-orbit orbit-one"></span>
            <span className="dashboard-note-orbit orbit-two"></span>
            <div className="dashboard-note-center">
              <span>GW</span>
              <small>MAKE IT<br />HAPPEN</small>
            </div>
          </div>
        </section>

        <section className="workspace-stats" aria-label="Your activity at a glance">
          <div className="workspace-stat">
            <span className="workspace-stat-icon">↗</span>
            <span className="workspace-stat-label">Gigs posted</span>
            <strong>{gigCount}</strong>
            <span className="workspace-stat-foot">Your open opportunities</span>
          </div>
          <Link className="workspace-stat" to="/applications/my">
            <span className="workspace-stat-icon">◎</span>
            <span className="workspace-stat-label">Applications sent</span>
            <strong>{applicationCount}</strong>
            <span className="workspace-stat-foot">Track your progress</span>
          </Link>
          <Link className="workspace-stat" to="/applications/received">
            <span className="workspace-stat-icon">✦</span>
            <span className="workspace-stat-label">Applicants to review</span>
            <strong>{receivedCount}</strong>
            <span className="workspace-stat-foot">Keep your hiring moving</span>
          </Link>
        </section>

        <section className="dashboard-content-grid">
          <div className="dashboard-opportunities-panel">
            <div className="dashboard-section-heading">
              <div>
                <span className="section-eyebrow">FRESH FROM THE MARKETPLACE</span>
                <h2>Opportunities for you</h2>
                <p>A few of the latest open gigs from the community.</p>
              </div>
              <Link className="text-action" to="/gigs">
                See all <span aria-hidden="true">↗</span>
              </Link>
            </div>

            {latestGigs.length > 0 ? (
              <div className="dashboard-gig-list">
                {latestGigs.map((gig) => (
                  <Link key={gig.id} className="dashboard-gig-row" to={`/gigs/${gig.id}`}>
                    <span className="dashboard-gig-symbol" aria-hidden="true">✦</span>
                    <span className="dashboard-gig-copy">
                      <span className="gig-category">{gig.category}</span>
                      <strong>{gig.title}</strong>
                      <small>{gig.location || 'Remote'} · Posted by a member</small>
                    </span>
                    <span className="dashboard-gig-budget">
                      ₹{Number(gig.budget).toLocaleString('en-IN')}<small>budget</small>
                    </span>
                    <span className="dashboard-gig-arrow" aria-hidden="true">→</span>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="dashboard-feed-empty">
                <span className="feed-empty-mark">✦</span>
                <div>
                  <h3>Your marketplace is ready.</h3>
                  <p>New community gigs will appear here. Start by exploring or posting an opportunity.</p>
                </div>
                <Link className="button button-primary button-small" to="/gigs/new">
                  Post the first gig
                </Link>
              </div>
            )}
          </div>

          <aside className="dashboard-shortcuts">
            <span className="section-eyebrow">QUICK ACCESS</span>
            <h2>Keep things moving</h2>
            <Link className="workspace-shortcut" to="/applications/my">
              <span className="shortcut-icon">◎</span>
              <span><strong>My applications</strong><small>See status updates</small></span>
              <span>↗</span>
            </Link>
            <Link className="workspace-shortcut" to="/applications/received">
              <span className="shortcut-icon">✦</span>
              <span><strong>Manage applicants</strong><small>Review your candidates</small></span>
              <span>↗</span>
            </Link>
            <div className="dashboard-account-note">
              <span className="account-note-mark">✓</span>
              <span><strong>You're all set</strong><small>{currentUser?.email}</small></span>
            </div>
          </aside>
        </section>
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <Link className="brand footer-brand" to="/dashboard">
            <span className="brand-mark">G</span>
            <span>gig<span className="brand-accent">work</span></span>
          </Link>
          <p>© {new Date().getFullYear()} GigWork · College Project</p>
          <Link className="back-to-top" to="/gigs">Explore opportunities ↑</Link>
        </div>
      </footer>
    </div>
  );
}
