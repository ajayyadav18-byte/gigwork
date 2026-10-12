import React from 'react';
import { Link } from 'react-router-dom';

export default function SiteFooter() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="site-footer">
      <div className="container footer-layout">
        <Link className="brand footer-brand" to="/">
          <span className="brand-mark">G</span>
          <span>gig<span className="brand-accent">work</span></span>
        </Link>
        <p>Make your skills count. © <span id="currentYear">{currentYear}</span> GigWork College Project.</p>
        <a href="#home" className="back-to-top">Back to top ↑</a>
      </div>
    </footer>
  );
}
