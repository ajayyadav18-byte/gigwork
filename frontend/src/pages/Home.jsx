import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import PublicHeader from '../components/PublicHeader';
import SiteFooter from '../components/SiteFooter';

const SAMPLE_GIGS = [
  {
    category: 'design',
    categoryLabel: 'DESIGN & CREATIVE',
    search: 'social media designer design creative instagram',
    iconClass: 'icon-lilac',
    icon: '✳',
    type: 'Remote',
    title: 'Social Media Design',
    description: "Create scroll-stopping visuals for a growing brand's social channels.",
    tags: ['Canva', 'Design', 'Social'],
    budget: '₹1,500–₹3,000',
  },
  {
    category: 'technology',
    categoryLabel: 'TECHNOLOGY',
    search: 'website developer technology frontend landing page',
    iconClass: 'icon-blue',
    icon: '⌘',
    type: 'Remote',
    title: 'Landing Page Development',
    description: 'Build a clean, responsive landing page for a small business.',
    tags: ['HTML', 'CSS', 'Web'],
    budget: '₹3,000–₹6,000',
  },
  {
    category: 'writing',
    categoryLabel: 'WRITING',
    search: 'content writing blog writer articles research',
    iconClass: 'icon-peach',
    icon: '✎',
    type: 'Flexible',
    title: 'Blog & Content Writing',
    description: 'Turn research into useful, well-structured articles for readers.',
    tags: ['Writing', 'Research'],
    budget: '₹800–₹2,000',
  },
  {
    category: 'marketing',
    categoryLabel: 'MARKETING',
    search: 'digital marketing social media campaign marketing',
    iconClass: 'icon-green',
    icon: '↗',
    type: 'Remote',
    title: 'Digital Marketing Support',
    description: 'Help plan a campaign and organize a small business content calendar.',
    tags: ['Marketing', 'Planning'],
    budget: '₹2,000–₹4,000',
  },
  {
    category: 'education',
    categoryLabel: 'EDUCATION',
    search: 'online tutor education mathematics teaching',
    iconClass: 'icon-yellow',
    icon: '▤',
    type: 'Flexible',
    title: 'Online Tutoring',
    description: 'Help a school student understand concepts through friendly lessons.',
    tags: ['Teaching', 'Learning'],
    budget: '₹300–₹700',
  },
  {
    category: 'technology',
    categoryLabel: 'TECHNOLOGY',
    search: 'data entry spreadsheet excel technology administration',
    iconClass: 'icon-teal',
    icon: '▦',
    type: 'Remote',
    title: 'Data & Spreadsheet Cleanup',
    description: 'Organize spreadsheet information and make records easier to use.',
    tags: ['Excel', 'Data'],
    budget: '₹700–₹1,500',
  },
];

export default function Home() {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  const filteredGigs = SAMPLE_GIGS.filter((gig) => {
    const matchesCategory = selectedCategory === 'all' || gig.category === selectedCategory;
    const matchesSearch =
      searchTerm.trim() === '' ||
      gig.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      gig.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      gig.search.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div>
      <PublicHeader />

      <main>
        <section className="hero section-space" id="home">
          <div className="hero-glow hero-glow-one"></div>
          <div className="hero-glow hero-glow-two"></div>

          <div className="container hero-layout">
            <div className="hero-copy">
              <div className="eyebrow">
                <span className="status-dot"></span>
                OPPORTUNITY STARTS HERE
              </div>

              <h1>
                Your next big thing<br />
                starts with a <span className="gradient-text">small gig.</span>
              </h1>

              <p className="hero-description">
                Your skills. Your opportunities. Your next gig.
              </p>

              <div className="hero-actions">
                <a className="button button-primary button-large" href="#gigs">
                  Explore opportunities <span aria-hidden="true">→</span>
                </a>
                <Link className="button button-secondary button-large" to="/register">
                  I'm looking to hire
                </Link>
              </div>

              <div className="hero-proof">
                <div className="avatar-stack" aria-hidden="true">
                  <span className="avatar avatar-one">A</span>
                  <span className="avatar avatar-two">S</span>
                  <span className="avatar avatar-three">R</span>
                  <span className="avatar avatar-four">+</span>
                </div>
                <div>
                  <strong>Skills meet opportunity</strong>
                  <p>One gig can start something great.</p>
                </div>
              </div>
            </div>

            <div className="hero-visual" aria-label="GigWork opportunity preview">
              <div className="visual-orbit orbit-one"></div>
              <div className="visual-orbit orbit-two"></div>

              <div className="floating-label floating-label-top">
                <span className="floating-icon purple-icon">✦</span>
                <span><strong>Show your skills</strong><small>Your next chapter awaits</small></span>
              </div>

              <article className="preview-card">
                <div className="preview-topline">
                  <span className="preview-badge"><span></span> OPPORTUNITY SPOTLIGHT</span>
                  <span className="preview-menu">•••</span>
                </div>
                <div className="preview-symbol">✧</div>
                <p className="preview-kicker">YOUR NEXT PROJECT</p>
                <h2>Make your talent<br />work for you.</h2>
                <p className="preview-copy">
                  Find meaningful work, build experience and turn your
                  abilities into opportunity.
                </p>
                <div className="preview-divider"></div>
                <div className="preview-bottom">
                  <div>
                    <small>YOUR POTENTIAL</small>
                    <strong>Unlimited <span>↗</span></strong>
                  </div>
                  <div className="preview-mini-mark">GW</div>
                </div>
              </article>

              <div className="floating-label floating-label-bottom">
                <span className="floating-icon green-icon">↗</span>
                <span><strong>Ready for what's next?</strong><small>Start exploring today</small></span>
              </div>
            </div>
          </div>

          <div className="container trust-strip">
            <span>BUILT FOR AMBITION</span>
            <span className="trust-line"></span>
            <span>STUDENTS</span>
            <span className="trust-separator">·</span>
            <span>FREELANCERS</span>
            <span className="trust-separator">·</span>
            <span>SMALL BUSINESSES</span>
          </div>
        </section>

        <section className="opportunities section-space" id="gigs">
          <div className="container">
            <div className="section-heading">
              <div>
                <span className="section-eyebrow">FIND YOUR FIT</span>
                <h2>Opportunities worth<br className="desktop-break" /> showing up for.</h2>
                <p className="section-description">
                  Explore example gigs across skills, interests and industries.
                </p>
              </div>
              <span className="heading-note"><span className="status-dot"></span> Explore the possibilities</span>
            </div>

            <div className="search-panel">
              <label className="search-box" htmlFor="gigSearch">
                <span className="search-icon" aria-hidden="true">⌕</span>
                <input
                  id="gigSearch"
                  type="search"
                  placeholder="Search gigs, skills or keywords..."
                  autoComplete="off"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </label>
              <label className="filter-box" htmlFor="categoryFilter">
                <span aria-hidden="true">▦</span>
                <select
                  id="categoryFilter"
                  value={selectedCategory}
                  onChange={(e) => setSelectedCategory(e.target.value)}
                >
                  <option value="all">All categories</option>
                  <option value="design">Design & Creative</option>
                  <option value="technology">Technology</option>
                  <option value="writing">Writing</option>
                  <option value="marketing">Marketing</option>
                  <option value="education">Education</option>
                </select>
              </label>
              <button className="button button-primary search-button" id="searchButton" type="button">
                Find gigs <span aria-hidden="true">→</span>
              </button>
            </div>

            <div className="category-pills" id="categories">
              <button
                className={`category-pill ${selectedCategory === 'all' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('all')}
                type="button"
              >
                ✦ All gigs
              </button>
              <button
                className={`category-pill ${selectedCategory === 'design' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('design')}
                type="button"
              >
                ◈ Design
              </button>
              <button
                className={`category-pill ${selectedCategory === 'technology' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('technology')}
                type="button"
              >
                ⌘ Technology
              </button>
              <button
                className={`category-pill ${selectedCategory === 'writing' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('writing')}
                type="button"
              >
                ✎ Writing
              </button>
              <button
                className={`category-pill ${selectedCategory === 'marketing' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('marketing')}
                type="button"
              >
                ↗ Marketing
              </button>
              <button
                className={`category-pill ${selectedCategory === 'education' ? 'selected' : ''}`}
                onClick={() => setSelectedCategory('education')}
                type="button"
              >
                ▤ Education
              </button>
            </div>

            <div className="gig-grid" id="gigGrid">
              {filteredGigs.map((gig, idx) => (
                <article key={idx} className="gig-card" data-category={gig.category}>
                  <div className="gig-card-top">
                    <span className={`gig-icon ${gig.iconClass}`}>{gig.icon}</span>
                  </div>
                  <div className="gig-meta">
                    <span>{gig.categoryLabel}</span>
                    <span className="gig-type">{gig.type}</span>
                  </div>
                  <h3>{gig.title}</h3>
                  <p className="gig-description">{gig.description}</p>
                  <div className="skill-tags">
                    {gig.tags.map((tag, tIdx) => (
                      <span key={tIdx}>{tag}</span>
                    ))}
                  </div>
                  <div className="gig-card-bottom">
                    <div>
                      <small>EXAMPLE BUDGET</small>
                      <strong>{gig.budget}</strong>
                    </div>
                    <Link to="/register" className="gig-arrow" aria-label="Explore this gig">↗</Link>
                  </div>
                </article>
              ))}
            </div>

            {filteredGigs.length === 0 && (
              <p className="empty-state" id="emptyState">
                No matching example gigs. Try a different keyword or category.
              </p>
            )}
            <p className="demo-notice">
              These are illustrative sample listings for the interface preview, not verified paid jobs.
              Real listings will come from the GigWork database.
            </p>
          </div>
        </section>

        <section className="how-section section-space" id="how-it-works">
          <div className="container">
            <div className="section-heading centered-heading">
              <span className="section-eyebrow">SIMPLE BY DESIGN</span>
              <h2>From looking to landing.</h2>
              <p className="section-description">A simple path to your next opportunity.</p>
            </div>

            <div className="steps-grid">
              <article className="step-card">
                <span className="step-number">01</span>
                <div className="step-symbol step-symbol-purple">◎</div>
                <h3>Create your profile</h3>
                <p>Show what you can do and what kind of opportunities interest you.</p>
              </article>
              <article className="step-card">
                <span className="step-number">02</span>
                <div className="step-symbol step-symbol-blue">⌕</div>
                <h3>Discover a gig</h3>
                <p>Explore listings that fit your skills, schedule and goals.</p>
              </article>
              <article className="step-card">
                <span className="step-number">03</span>
                <div className="step-symbol step-symbol-green">↗</div>
                <h3>Make your move</h3>
                <p>Apply for work, connect with an opportunity and build experience.</p>
              </article>
            </div>
          </div>
        </section>

        <section className="final-cta">
          <div className="container cta-layout">
            <div>
              <span className="section-eyebrow">YOUR NEXT CHAPTER</span>
              <h2>Good at something?<br /><span>Put it to work.</span></h2>
              <p>Create your account and take the first step toward your next gig.</p>
            </div>
            <Link className="button button-light button-large" to="/register">
              Get started for free <span aria-hidden="true">↗</span>
            </Link>
            <div className="cta-decoration" aria-hidden="true">✳</div>
          </div>
        </section>
      </main>

      <SiteFooter />
    </div>
  );
}
