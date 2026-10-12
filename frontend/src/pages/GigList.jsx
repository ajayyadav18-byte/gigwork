import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

const CATEGORIES = [
  'Design & Creative',
  'Technology',
  'Writing',
  'Marketing',
  'Education',
  'Other',
];

export default function GigList() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const selectedCategory = searchParams.get('category') || 'all';

  const [gigs, setGigs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState(query);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const user = getStoredUser();

  const fetchGigs = async (q, cat) => {
    setLoading(true);
    try {
      const data = await apiRequest(`/gigs?q=${encodeURIComponent(q)}&category=${encodeURIComponent(cat)}`);
      setGigs(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGigs(query, selectedCategory);
  }, [query, selectedCategory]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setSearchParams({ q: searchTerm, category: selectedCategory });
  };

  const handleCategoryChange = (cat) => {
    setSearchParams({ q: searchTerm, category: cat });
  };

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={user} />

      <main className="gigs-main container">
        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <section className="gigs-hero">
          <span className="section-eyebrow">THE OPPORTUNITY MARKETPLACE</span>
          <h1>Find work that <span className="gradient-text">moves you.</span></h1>
          <p>Discover projects, share your skills and turn your time into opportunity.</p>

          <form onSubmit={handleSearchSubmit} className="gig-search">
            <span className="search-icon" aria-hidden="true">⌕</span>
            <input
              type="search"
              name="q"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search design, development, writing..."
              aria-label="Search gigs"
            />
            <button type="submit" className="button button-primary">Search gigs →</button>
          </form>
        </section>

        <section className="gigs-results">
          <div className="gigs-results-heading">
            <div>
              <span className="section-eyebrow">DISCOVER</span>
              <h2>Open opportunities</h2>
              <p>Explore gigs posted by members of the community.</p>
            </div>

            <span className="results-count">
              {gigs.length} {gigs.length === 1 ? 'opportunity' : 'opportunities'}
            </span>
          </div>

          <div className="category-filters">
            <button
              type="button"
              className={`category-chip ${selectedCategory === 'all' ? 'active' : ''}`}
              onClick={() => handleCategoryChange('all')}
            >
              All categories
            </button>
            {CATEGORIES.map((item) => (
              <button
                key={item}
                type="button"
                className={`category-chip ${selectedCategory.toLowerCase() === item.toLowerCase() ? 'active' : ''}`}
                onClick={() => handleCategoryChange(item)}
              >
                {item}
              </button>
            ))}
          </div>

          {loading ? (
            <p style={{ marginTop: '30px' }}>Loading marketplace listings...</p>
          ) : gigs.length > 0 ? (
            <div className="gig-list">
              {gigs.map((gig) => (
                <article key={gig.id} className="gig-card">
                  <div className="gig-card-main">
                    <div className="gig-category-icon" aria-hidden="true">✦</div>
                    <div className="gig-card-content">
                      <div className="gig-card-meta">
                        <span className="gig-category">{gig.category}</span>
                        <span className="gig-status">
                          <span className="status-dot"></span> Open
                        </span>
                      </div>
                      <h3>
                        <Link to={`/gigs/${gig.id}`}>{gig.title}</Link>
                      </h3>
                      <p className="gig-description">
                        {gig.description && gig.description.length > 180
                          ? gig.description.substring(0, 180) + '...'
                          : gig.description}
                      </p>
                      <div className="gig-card-details">
                        <span>⌖ {gig.location || 'Remote'}</span>
                        <span>Posted by {gig.creatorName || 'a GigWork member'}</span>
                      </div>
                    </div>
                  </div>

                  <div className="gig-card-side">
                    <span className="budget-label">BUDGET</span>
                    <strong className="gig-budget">
                      ₹{Number(gig.budget).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </strong>
                    <Link className="gig-view-link" to={`/gigs/${gig.id}`}>
                      View details →
                    </Link>
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <div className="gig-empty-state">
              <div className="empty-state-icon">⌕</div>
              <h3>No gigs found yet</h3>
              <p>
                There are no matching open gigs in the database.
                Try another category or publish the first opportunity.
              </p>
              <Link className="button button-primary" to="/gigs/new">
                + Post the first gig
              </Link>
            </div>
          )}
        </section>
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <Link className="brand footer-brand" to="/dashboard">
            <span className="brand-mark">G</span>
            <span>gig<span className="brand-accent">work</span></span>
          </Link>
          <p>© {new Date().getFullYear()} GigWork · College Project</p>
          <Link className="back-to-top" to="/dashboard">My dashboard ↑</Link>
        </div>
      </footer>
    </div>
  );
}
