import React from 'react';
import { Link } from 'react-router-dom';

export default function PublicHeader() {
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false);

  return (
    <header className="site-header">
      <nav className="navbar container" aria-label="Main navigation">
        <Link className="brand" to="/" aria-label="GigWork home">
          <span className="brand-mark">G</span>
          <span>gig<span className="brand-accent">work</span></span>
        </Link>

        <button
          className="menu-toggle"
          id="menuToggle"
          aria-label="Toggle navigation"
          aria-expanded={mobileMenuOpen}
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
        >
          <span></span><span></span><span></span>
        </button>

        <div className={`nav-links ${mobileMenuOpen ? 'is-open' : ''}`} id="navLinks">
          <a className="nav-link active" href="/#home" onClick={() => setMobileMenuOpen(false)}>Explore</a>
          <a className="nav-link" href="/#categories" onClick={() => setMobileMenuOpen(false)}>Categories</a>
          <a className="nav-link" href="/#how-it-works" onClick={() => setMobileMenuOpen(false)}>How it works</a>
        </div>

        <div className="nav-actions">
          <Link className="login-link" to="/login">Log in</Link>
          <Link className="button button-small button-primary" to="/register">
            Get started <span aria-hidden="true">↗</span>
          </Link>
        </div>
      </nav>
    </header>
  );
}
