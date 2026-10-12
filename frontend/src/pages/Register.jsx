import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiRequest, setAuthSession } from '../api';

export default function Register() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setLoading(true);
    try {
      const data = await apiRequest('/auth/register', {
        method: 'POST',
        body: JSON.stringify(formData),
      });
      setAuthSession(data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Registration failed. Please check your information.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <header className="site-header">
        <nav className="navbar container" aria-label="Main navigation">
          <Link className="brand" to="/" aria-label="GigWork home">
            <span className="brand-mark">G</span>
            <span>gig<span className="brand-accent">work</span></span>
          </Link>
          <Link className="login-link" to="/">← Back to home</Link>
        </nav>
      </header>

      <main className="auth-main">
        <div className="auth-glow auth-glow-one"></div>
        <div className="auth-glow auth-glow-two"></div>

        <section className="auth-layout container">
          <div className="auth-intro">
            <div className="eyebrow">
              <span className="status-dot"></span>
              YOUR NEXT CHAPTER STARTS HERE
            </div>

            <h1>
              Turn your skills<br />
              into <span className="gradient-text">opportunity.</span>
            </h1>

            <p className="auth-intro-description">
              Create your GigWork account to discover opportunities,
              share your talents and find work that fits your ambitions.
            </p>

            <div className="auth-benefits">
              <div className="auth-benefit">
                <span className="benefit-icon">✦</span>
                <div>
                  <strong>Discover opportunities</strong>
                  <p>Explore gigs across different categories.</p>
                </div>
              </div>

              <div className="auth-benefit">
                <span className="benefit-icon">↗</span>
                <div>
                  <strong>Showcase your skills</strong>
                  <p>Build a profile that reflects your strengths.</p>
                </div>
              </div>

              <div className="auth-benefit">
                <span className="benefit-icon">◎</span>
                <div>
                  <strong>Manage your journey</strong>
                  <p>Keep track of your gigs and applications.</p>
                </div>
              </div>
            </div>

            <div className="auth-note">
              <span className="status-dot"></span>
              One account. New possibilities.
            </div>
          </div>

          <div className="auth-card">
            <div className="auth-card-heading">
              <span className="section-eyebrow">JOIN GIGWORK</span>
              <h2>Create your account</h2>
              <p>Enter your details to get started.</p>
            </div>

            {error && (
              <div className="form-alert form-alert-error" role="alert">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <label htmlFor="name">Full name</label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  placeholder="Enter your full name"
                  autoComplete="name"
                  maxLength="100"
                  required
                  value={formData.name}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="email">Email address</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="you@example.com"
                  autoComplete="email"
                  maxLength="190"
                  required
                  value={formData.email}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">Create password</label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="At least 8 characters"
                  autoComplete="new-password"
                  minLength="8"
                  maxLength="72"
                  required
                  value={formData.password}
                  onChange={handleChange}
                />
                <p className="field-hint">Use at least 8 characters.</p>
              </div>

              <div className="form-group">
                <label htmlFor="confirmPassword">Confirm password</label>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  placeholder="Enter your password again"
                  autoComplete="new-password"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                />
              </div>

              <button className="button button-primary auth-submit" type="submit" disabled={loading}>
                {loading ? 'Creating account...' : 'Create my account'} <span aria-hidden="true">→</span>
              </button>

              <p className="auth-terms">
                By creating an account, you agree to use GigWork
                responsibly and provide accurate information.
              </p>
            </form>

            <div className="auth-switch">
              Already have an account? <Link to="/login">Log in</Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="site-footer">
        <div className="container footer-layout">
          <Link className="brand footer-brand" to="/">
            <span className="brand-mark">G</span>
            <span>gig<span className="brand-accent">work</span></span>
          </Link>
          <p>© {new Date().getFullYear()} GigWork · College Project</p>
          <Link className="back-to-top" to="/">Back to home ↑</Link>
        </div>
      </footer>
    </div>
  );
}
