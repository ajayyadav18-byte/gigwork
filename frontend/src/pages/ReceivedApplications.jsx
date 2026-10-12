import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

export default function ReceivedApplications() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const user = getStoredUser();

  const loadApplications = async () => {
    try {
      const data = await apiRequest('/applications/received');
      setApplications(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadApplications();
  }, []);

  const handleStatusUpdate = async (id, status) => {
    setError('');
    setSuccess('');
    try {
      await apiRequest(`/applications/${id}/status`, {
        method: 'POST',
        body: JSON.stringify({ status }),
      });
      setSuccess(`Application marked as ${status.toLowerCase()}.`);
      loadApplications();
    } catch (err) {
      setError(err.message || 'Failed to update application status.');
    }
  };

  const pendingCount = applications.filter(a => a.status?.toUpperCase() === 'PENDING').length;
  const acceptedCount = applications.filter(a => a.status?.toUpperCase() === 'ACCEPTED').length;

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={user} />

      <main className="container applications-page">
        <section className="applications-hero applicants-hero">
          <div>
            <Link className="applications-back-link" to="/dashboard">← Back to dashboard</Link>
            <span className="section-eyebrow">EMPLOYER WORKSPACE</span>
            <h1>Manage applicants<span className="gradient-text">.</span></h1>
            <p>Meet the people interested in your gigs. Review each application and keep candidates updated.</p>
          </div>
          <div className="applications-hero-art applicants-art" aria-hidden="true">
            <span>✦</span>
            <small>GREAT WORK<br />STARTS HERE</small>
          </div>
        </section>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <section className="application-stats" aria-label="Applicant summary">
          <div className="application-stat">
            <span className="application-stat-icon">◎</span>
            <span>Total applications</span>
            <strong>{applications.length}</strong>
          </div>
          <div className="application-stat">
            <span className="application-stat-icon pending-icon">◷</span>
            <span>Need your review</span>
            <strong>{pendingCount}</strong>
          </div>
          <div className="application-stat">
            <span className="application-stat-icon accepted-icon">✓</span>
            <span>Accepted</span>
            <strong>{acceptedCount}</strong>
          </div>
        </section>

        {loading ? (
          <p style={{ marginTop: '30px' }}>Loading applicants...</p>
        ) : applications.length > 0 ? (
          <>
            <div className="applications-list-heading">
              <div>
                <span className="section-eyebrow">YOUR CANDIDATES</span>
                <h2>Recent applications</h2>
              </div>
              <span className="applications-count">{pendingCount} awaiting review</span>
            </div>

            <section className="application-list application-list-polished">
              {applications.map((record) => {
                const initial = record.applicantName ? record.applicantName.charAt(0).toUpperCase() : 'A';
                return (
                  <article key={record.id} className="gig-card application-card received-application-card">
                    <div className="application-card-main">
                      <div className="candidate-avatar">{initial}</div>
                      <div className="gig-card-content">
                        <div className="application-card-meta">
                          <span className="gig-category">{record.gigCategory}</span>
                          <span className="application-date">Applied {record.createdAt}</span>
                        </div>
                        <h2>{record.applicantName}</h2>
                        <p>
                          <a className="candidate-email" href={`mailto:${record.applicantEmail}`}>
                            {record.applicantEmail}
                          </a>
                        </p>
                        <p className="candidate-for-gig">
                          For <strong>{record.gigTitle}</strong>
                        </p>
                      </div>
                    </div>

                    <div className="application-card-side received-card-side">
                      <span className={`status-badge status-${(record.status || '').toLowerCase()}`}>
                        {record.status}
                      </span>
                      {record.status?.toUpperCase() === 'PENDING' && (
                        <div className="employer-actions">
                          <button
                            className="button button-primary button-small"
                            type="button"
                            onClick={() => handleStatusUpdate(record.id, 'ACCEPTED')}
                          >
                            Accept
                          </button>
                          <button
                            className="button button-secondary button-small"
                            type="button"
                            onClick={() => handleStatusUpdate(record.id, 'REJECTED')}
                          >
                            Decline
                          </button>
                        </div>
                      )}
                      <Link className="application-detail-link" to={`/gigs/${record.gigId}`}>
                        View gig details →
                      </Link>
                    </div>
                  </article>
                );
              })}
            </section>
          </>
        ) : (
          <section className="applications-empty-state">
            <div className="empty-orbit employer-empty-orbit" aria-hidden="true"><span>✦</span></div>
            <span className="section-eyebrow">YOUR HIRING PIPELINE</span>
            <h2>Good people start with a great opportunity.</h2>
            <p>When someone applies to one of your gigs, their profile and application status will appear here.</p>
            <Link className="button button-primary" to="/gigs/new">
              Post your first gig <span aria-hidden="true">→</span>
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
          <Link className="back-to-top" to="/applications/my">My applications ↑</Link>
        </div>
      </footer>
    </div>
  );
}
