import { Link, useNavigate } from 'react-router-dom';
import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { HiMenu, HiX, HiBriefcase } from 'react-icons/hi';
import { BsStars } from 'react-icons/bs';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const logoRef = useRef(null);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 30);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const roleLinks = () => {
    if (!user) return null;
    switch (user.role) {
      case 'CANDIDATE':
        return (
          <>
            <Link to="/candidate/dashboard" className="nav-link" onClick={() => setOpen(false)}>Dashboard</Link>
            <Link to="/candidate/applications" className="nav-link" onClick={() => setOpen(false)}>My Applications</Link>
            <Link to="/candidate/saved-jobs" className="nav-link" onClick={() => setOpen(false)}>Saved Jobs</Link>
            <Link to="/candidate/profile" className="nav-link" onClick={() => setOpen(false)}>Profile</Link>
          </>
        );
      case 'EMPLOYER':
        return (
          <>
            <Link to="/employer/dashboard" className="nav-link" onClick={() => setOpen(false)}>Dashboard</Link>
            <Link to="/employer/jobs" className="nav-link" onClick={() => setOpen(false)}>My Jobs</Link>
            <Link to="/employer/post-job" className="nav-link" onClick={() => setOpen(false)}>Post Job</Link>
            <Link to="/employer/profile" className="nav-link" onClick={() => setOpen(false)}>Profile</Link>
          </>
        );
      case 'ADMIN':
        return (
          <>
            <Link to="/admin/dashboard" className="nav-link" onClick={() => setOpen(false)}>Dashboard</Link>
            <Link to="/admin/users" className="nav-link" onClick={() => setOpen(false)}>Users</Link>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <nav className={`glass-nav sticky top-0 z-50 transition-all duration-500 ${scrolled ? 'shadow-[0_0_30px_rgba(0,0,0,0.5)]' : ''}`}>
      {/* Top energy bar */}
      <div className="absolute top-0 left-0 w-full h-[2px] overflow-hidden">
        <div className="w-full h-full bg-gradient-to-r from-transparent via-neon-cyan to-transparent animate-hologram" style={{ backgroundSize: '200% 100%' }} />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-3 group" ref={logoRef}>
            <div className="relative">
              <div className="w-10 h-10 bg-neon-cyan/20 border border-neon-cyan/50 rounded-xl flex items-center justify-center group-hover:shadow-[0_0_20px_rgba(0,240,255,0.6)] group-hover:border-neon-cyan transition-all duration-500 relative z-10 overflow-hidden">
                <BsStars className="absolute top-1 right-1 w-3 h-3 text-white animate-pulse" />
                <HiBriefcase className="w-6 h-6 text-neon-cyan drop-shadow-[0_0_8px_rgba(0,240,255,0.8)] group-hover:scale-110 transition-transform duration-300" />
              </div>
              {/* Pulsing orbital ring around logo */}
              <div className="absolute -inset-2 border border-neon-cyan/20 rounded-xl animate-pulse-glow pointer-events-none group-hover:border-neon-cyan/40 transition-all" />
              <div className="absolute -inset-1 border border-neon-purple/20 rounded-xl animate-spin-slow pointer-events-none" style={{ borderStyle: 'dashed' }} />
            </div>
            <span className="text-xl font-bold tracking-widest uppercase font-orbitron">
              <span className="text-white">JOB</span>
              <span className="text-neon-cyan drop-shadow-[0_0_10px_rgba(0,240,255,0.6)]">PORTAL</span>
            </span>
          </Link>

          {/* Desktop Links */}
          <div className="hidden md:flex items-center space-x-6">
            <Link to="/jobs" className="nav-link">
              Browse Jobs
            </Link>
            {roleLinks()}
            {user ? (
              <div className="flex items-center space-x-4">
                <span className="text-sm text-slate-300 flex items-center">
                  <span className="mr-1">Hi,</span> <span className="font-semibold text-white tracking-wide">{user.name}</span>
                  <span className="ml-3 px-2 py-0.5 bg-neon-cyan/10 border border-neon-cyan/30 text-neon-cyan text-xs rounded-full uppercase tracking-wider shadow-[0_0_8px_rgba(0,240,255,0.2)] font-orbitron">
                    {user.role}
                  </span>
                </span>
                <button onClick={handleLogout}
                  className="px-4 py-2 text-sm font-medium border border-neon-magenta text-neon-magenta rounded-lg hover:bg-neon-magenta/10 hover:shadow-neon-magenta transition-all duration-300 group relative overflow-hidden">
                  <span className="relative z-10">Logout</span>
                  <div className="absolute inset-0 bg-gradient-to-r from-neon-magenta/0 via-neon-magenta/10 to-neon-magenta/0 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4 pl-2 border-l border-white/10">
                <Link to="/login" className="btn-outline text-sm">
                  Login
                </Link>
                <Link to="/register" className="btn-primary text-sm relative overflow-hidden group">
                  <span className="relative z-10">Register</span>
                  <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
                </Link>
              </div>
            )}
          </div>

          {/* Mobile Toggle */}
          <button className="md:hidden text-slate-300 hover:text-neon-cyan transition-colors" onClick={() => setOpen(!open)}>
            {open ? <HiX className="w-7 h-7" /> : <HiMenu className="w-7 h-7" />}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {open && (
        <div className="md:hidden border-t border-white/10 bg-space-900/95 backdrop-blur-3xl px-4 py-4 space-y-3 shadow-2xl">
          <Link to="/jobs" className="block py-2 text-slate-300 hover:text-neon-cyan hover:translate-x-1 transition-all" onClick={() => setOpen(false)}>
            Browse Jobs
          </Link>
          {roleLinks()}
          <div className="pt-4 mt-2 border-t border-white/10">
            {user ? (
              <button onClick={() => { handleLogout(); setOpen(false); }}
                className="w-full text-center py-2.5 border border-neon-magenta text-neon-magenta rounded-lg hover:bg-neon-magenta/10 hover:shadow-neon-magenta transition-all">Logout</button>
            ) : (
              <div className="flex flex-col space-y-3">
                <Link to="/login" className="btn-outline text-center py-2.5" onClick={() => setOpen(false)}>Login</Link>
                <Link to="/register" className="btn-primary text-center py-2.5" onClick={() => setOpen(false)}>Register</Link>
              </div>
            )}
          </div>
        </div>
      )}

      <style>{`
        .nav-link {
          color: #94a3b8;
          font-weight: 500;
          letter-spacing: 0.05em;
          transition: all 0.3s ease;
          position: relative;
          text-transform: uppercase;
          font-size: 0.75rem;
          font-family: 'Orbitron', sans-serif;
        }
        .nav-link:hover {
          color: #00F0FF;
          text-shadow: 0 0 12px rgba(0, 240, 255, 0.6);
        }
        .nav-link::after {
          content: '';
          position: absolute;
          width: 0;
          height: 2px;
          bottom: -4px;
          left: 0;
          background: linear-gradient(90deg, #00F0FF, #B026FF);
          box-shadow: 0 0 8px rgba(0, 240, 255, 0.8);
          transition: width 0.3s ease;
        }
        .nav-link:hover::after {
          width: 100%;
        }
      `}</style>
    </nav>
  );
}
