import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

export default function MyApplications() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const user = getStoredUser();

  useEffect(() => {
    async function loadApplications() {
      try {
        const data = await apiRequest('/applications/my');
        setApplications(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    loadApplications();
  }, []);

  const pendingCount = applications.filter(a => a.status?.toUpperCase() === 'PENDING').length;
  const acceptedCount = applications.filter(a => a.status?.toUpperCase() === 'ACCEPTED').length;

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={user} />

      <main className="container applications-page">
        <section className="applications-hero">
          <div>
            <Link className="applications-back-link" to="/dashboard">← Back to dashboard</Link>
            <span className="section-eyebrow">YOUR CAREER TRACKER</span>
            <h1>My applications<span className="gradient-text">.</span></h1>
            <p>Every opportunity you’ve pursued, in one place. Keep an eye on your status and next steps.</p>
          </div>
          <div className="applications-hero-art" aria-hidden="true">
            <span>◎</span>
            <small>YOUR<br />NEXT MOVE</small>
          </div>
        </section>

        {error && <div className="alert alert-error">{error}</div>}

        <section className="application-stats" aria-label="Application summary">
          <div className="application-stat">
            <span className="application-stat-icon">↗</span>
            <span>Applications sent</span>
            <strong>{applications.length}</strong>
          </div>
          <div className="application-stat">
            <span className="application-stat-icon pending-icon">◷</span>
            <span>Awaiting a reply</span>
            <strong>{pendingCount}</strong>
          </div>
          <div className="application-stat">
            <span className="application-stat-icon accepted-icon">✓</span>
            <span>Accepted</span>
            <strong>{acceptedCount}</strong>
          </div>
        </section>

        {loading ? (
          <p style={{ marginTop: '30px' }}>Loading application history...</p>
        ) : applications.length > 0 ? (
          <>
            <div className="applications-list-heading">
              <div>
                <span className="section-eyebrow">YOUR ACTIVITY</span>
                <h2>Application history</h2>
              </div>
              <span className="applications-count">{applications.length} total</span>
            </div>

            <div className="application-list application-list-polished">
              {applications.map((record) => (
                <article key={record.id} className="gig-card application-card">
                  <div className="application-card-main">
                    <div className="application-card-mark" aria-hidden="true">✦</div>
                    <div className="gig-card-content">
                      <div className="application-card-meta">
                        <span className="gig-category">{record.gigCategory}</span>
                        <span className="application-location">⌖ {record.gigLocation || 'Remote'}</span>
                      </div>
                      <h2>{record.gigTitle}</h2>
                      <p className="application-date">Applied {record.createdAt}</p>
                    </div>
                  </div>
                  <div className="application-card-side">
                    <span className={`status-badge status-${(record.status || '').toLowerCase()}`}>
                      {record.status}
                    </span>
                    <strong className="application-budget">
                      ₹{Number(record.gigBudget || 0).toLocaleString('en-IN')}<small>gig budget</small>
                    </strong>
                    <Link className="button button-secondary button-small" to={`/gigs/${record.gigId}`}>
                      View opportunity <span aria-hidden="true">→</span>
                    </Link>
                  </div>
                </article>
              ))}
            </div>
          </>
        ) : (
          <section className="applications-empty-state">
            <div className="empty-orbit" aria-hidden="true"><span>↗</span></div>
            <span className="section-eyebrow">YOUR NEXT CHAPTER</span>
            <h2>Your first application is one good gig away.</h2>
            <p>Explore projects that fit your interests, then apply with confidence. Your updates will show here.</p>
            <Link className="button button-primary" to="/gigs">
              Explore opportunities <span aria-hidden="true">→</span>
            </Link>
          </section>
        )}
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <Link className="brand footer-brand" to="/dashboard">
            <span className="brand-mark">G</span>
            <span>gig<span className="brand-accent">work</span></span>
          </Link>
          <p>© {new Date().getFullYear()} GigWork · College Project</p>
          <Link className="back-to-top" to="/applications/received">Manage your listings ↑</Link>
        </div>
      </footer>
    </div>
  );
}
