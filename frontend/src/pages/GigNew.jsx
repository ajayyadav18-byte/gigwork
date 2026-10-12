import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import WorkspaceHeader from '../components/WorkspaceHeader';
import { apiRequest, getStoredUser } from '../api';

export default function GigNew() {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    location: '',
    budget: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const user = getStoredUser();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const gig = await apiRequest('/gigs', {
        method: 'POST',
        body: JSON.stringify({
          ...formData,
          budget: parseFloat(formData.budget),
        }),
      });
      navigate(`/gigs/${gig.id}`);
    } catch (err) {
      setError(err.message || 'Failed to publish opportunity.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="gigs-page workspace-page">
      <WorkspaceHeader user={user} />

      <main className="container post-gig-page">
        <Link className="gig-back-link" to="/gigs">← Back to opportunities</Link>

        <section className="post-gig-heading">
          <span className="section-eyebrow">FOR CLIENTS &amp; CREATORS</span>
          <h1>Post an opportunity<span className="gradient-text">.</span></h1>
          <p>Describe your project, set a budget, and connect with the right talent.</p>
        </section>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} className="post-gig-form">
          <div className="form-field">
            <label htmlFor="title">Gig title</label>
            <input
              id="title"
              name="title"
              type="text"
              placeholder="e.g. Design a premium brand identity"
              maxLength="120"
              required
              value={formData.title}
              onChange={handleChange}
            />
          </div>

          <div className="form-field">
            <label htmlFor="description">Project description</label>
            <textarea
              id="description"
              name="description"
              rows="7"
              maxLength="5000"
              placeholder="Describe the work, deliverables, timeline, and skills required..."
              required
              value={formData.description}
              onChange={handleChange}
            ></textarea>
            <p className="field-hint">Include the deliverables and expectations so applicants understand the work.</p>
          </div>

          <div className="post-gig-fields-grid">
            <div className="form-field">
              <label htmlFor="category">Category</label>
              <select
                id="category"
                name="category"
                required
                value={formData.category}
                onChange={handleChange}
              >
                <option value="">Choose a category</option>
                <option value="Design & Creative">Design &amp; Creative</option>
                <option value="Technology">Technology</option>
                <option value="Writing">Writing</option>
                <option value="Marketing">Marketing</option>
                <option value="Education">Education</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="location">Work location</label>
              <input
                id="location"
                name="location"
                type="text"
                placeholder="Remote or Hyderabad"
                maxLength="120"
                value={formData.location}
                onChange={handleChange}
              />
              <p className="field-hint">Leave blank if the work can be done remotely.</p>
            </div>
          </div>

          <div className="form-field">
            <label htmlFor="budget">Total project budget (₹)</label>
            <input
              id="budget"
              name="budget"
              type="number"
              min="1"
              max="1000000"
              step="0.01"
              placeholder="5000.00"
              required
              value={formData.budget}
              onChange={handleChange}
            />
            <p className="field-hint">Minimum ₹1 · Maximum ₹10,00,000 · Up to two decimal places.</p>
          </div>

          <div className="post-gig-actions">
            <Link className="button button-secondary" to="/gigs">Cancel</Link>
            <button className="button button-primary" type="submit" disabled={loading}>
              {loading ? 'Publishing...' : 'Publish opportunity →'}
            </button>
          </div>
        </form>
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <Link className="brand footer-brand" to="/dashboard">
            <span className="brand-mark">G</span>
            <span>Gig<span className="brand-accent">Work</span></span>
          </Link>
          <span>© {new Date().getFullYear()} GigWork</span>
          <Link className="back-to-top" to="/gigs">Browse opportunities ↑</Link>
        </div>
      </footer>
    </div>
  );
}
