import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import PublicHeader from '../components/PublicHeader';
import SiteFooter from '../components/SiteFooter';
import { apiRequest, setAuthSession } from '../api';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(window.location.search);
  const isLogout = queryParams.get('logout') === 'true';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });
      setAuthSession(data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'The email or password you entered is incorrect.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <header className="site-header">
        <nav className="navbar container" aria-label="Main navigation">
          <Link className="brand" to="/">
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
              WELCOME BACK
            </div>

            <h1>
              Your next opportunity<br />
              is <span className="gradient-text">waiting.</span>
            </h1>

            <p className="auth-intro-description">
              Sign in to explore gigs, manage your applications
              and keep your professional journey moving forward.
            </p>

            <div className="auth-benefits">
              <div className="auth-benefit">
                <span className="benefit-icon">✦</span>
                <div>
                  <strong>Explore opportunities</strong>
                  <p>Find work that matches your interests.</p>
                </div>
              </div>

              <div className="auth-benefit">
                <span className="benefit-icon">◎</span>
                <div>
                  <strong>Your journey, organized</strong>
                  <p>Keep track of your gigs and applications.</p>
                </div>
              </div>
            </div>
          </div>

          <div className="auth-card">
            <div className="auth-card-heading">
              <span className="section-eyebrow">YOUR GIGWORK ACCOUNT</span>
              <h2>Welcome back</h2>
              <p>Enter your credentials to continue.</p>
            </div>

            {isLogout && (
              <div className="form-alert form-alert-success" role="status">
                You've been logged out successfully.
              </div>
            )}

            {error && (
              <div className="form-alert form-alert-error" role="alert">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="auth-form">
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
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">Password</label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>

              <button className="button button-primary auth-submit" type="submit" disabled={loading}>
                {loading ? 'Logging in...' : 'Log in to GigWork'} <span aria-hidden="true">→</span>
              </button>
            </form>

            <div className="auth-switch">
              New to GigWork? <Link to="/register">Create an account</Link>
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
