import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

export default function GigDetail() {
  const { id } = useParams();
  const [gig, setGig] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const user = getStoredUser();

  useEffect(() => {
    async function loadGig() {
      try {
        const data = await apiRequest(`/gigs/${id}`);
        setGig(data);
      } catch (err) {
        setError(err.message || 'That gig could not be found.');
      } finally {
        setLoading(false);
      }
    }
    loadGig();
  }, [id]);

  const handleApply = async (e) => {
    e.preventDefault();
    setApplying(true);
    setError('');
    setSuccess('');
    try {
      await apiRequest(`/applications/apply/${id}`, { method: 'POST' });
      setSuccess('Your application was submitted successfully!');
    } catch (err) {
      setError(err.message || 'Failed to submit application.');
    } finally {
      setApplying(false);
    }
  };

  if (loading) {
    return (
      <div className="gigs-page workspace-page">
        <WorkspaceHeader user={user} />
        <main className="container gig-detail-page">
          <p style={{ marginTop: '40px' }}>Loading opportunity details...</p>
        </main>
      </div>
    );
  }

  if (!gig) {
    return (
      <div className="gigs-page workspace-page">
        <WorkspaceHeader user={user} />
        <main className="container gig-detail-page">
          <Link className="gig-back-link" to="/gigs">← Back to opportunities</Link>
          <div className="alert alert-error">{error || 'Gig not found.'}</div>
        </main>
      </div>
    );
  }

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={user} />

      <main className="container gig-detail-page">
        <Link className="gig-back-link" to="/gigs">← Back to opportunities</Link>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <div className="gig-detail-layout">
          <section className="gig-detail-main">
            <div className="gig-detail-labels">
              <span className="gig-category">{gig.category}</span>
              <span className="gig-status">{gig.status}</span>
            </div>

            <h1 className="gig-detail-title">{gig.title}</h1>

            <div className="gig-detail-meta">
              <span>📍 {gig.location || 'Remote'}</span>
              <span>🗓️ Posted {gig.createdAt || 'Recently'}</span>
              <span>Posted by {gig.creatorName || 'GigWork member'}</span>
            </div>

            <div className="gig-detail-divider"></div>

            <h2>About this opportunity</h2>
            <p className="gig-detail-description" style={{ whiteSpace: 'pre-line' }}>
              {gig.description}
            </p>
          </section>

          <aside className="gig-detail-sidebar">
            <div className="gig-detail-budget-label">Project budget</div>
            <div className="gig-detail-budget">
              ₹{Number(gig.budget).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
            </div>
            <p className="gig-detail-sidebar-note">
              Review the project requirements and budget before deciding whether to apply.
            </p>

            <div className="gig-detail-divider"></div>

            <div className="gig-sidebar-info">
              <span>Project category</span>
              <strong>{gig.category}</strong>
            </div>
            <div className="gig-sidebar-info">
              <span>Work location</span>
              <strong>{gig.location || 'Remote'}</strong>
            </div>
            <div className="gig-sidebar-info">
              <span>Availability</span>
              <strong>{gig.status}</strong>
            </div>

            {gig.status === 'OPEN' ? (
              <form onSubmit={handleApply}>
                <button className="button button-primary" type="submit" disabled={applying}>
                  {applying ? 'Submitting...' : 'Apply for this gig'}
                </button>
              </form>
            ) : (
              <p className="gig-detail-sidebar-note">
                This gig is no longer accepting applications.
              </p>
            )}
          </aside>
        </div>
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <span>© {new Date().getFullYear()} GigWork</span>
          <span>Find work. Build skills. Move forward.</span>
        </div>
      </footer>
    </div>
  );
}
