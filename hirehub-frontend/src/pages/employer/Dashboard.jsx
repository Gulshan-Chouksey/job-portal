import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { employerService } from '../../services/dataService';
import { HiBriefcase, HiDocumentText, HiCheckCircle, HiClock, HiPencil } from 'react-icons/hi';
import toast from 'react-hot-toast';
import HolographicCard from '../../components/HolographicCard';

function AnimatedCounter({ value, duration = 1500 }) {
  const [display, setDisplay] = useState(0);
  const ref = useRef(null);
  useEffect(() => {
    let end = Number(value) || 0;
    if (end === 0) { setDisplay(0); return; }
    const startTime = Date.now();
    const step = () => {
      const elapsed = Date.now() - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplay(Math.floor(eased * end));
      if (progress < 1) ref.current = requestAnimationFrame(step);
    };
    ref.current = requestAnimationFrame(step);
    return () => cancelAnimationFrame(ref.current);
  }, [value, duration]);
  return <span>{display}</span>;
}

export default function EmployerDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    employerService.getDashboard()
      .then((res) => setStats(res.data.data))
      .catch(() => toast.error('Failed to load dashboard'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="flex flex-col items-center justify-center py-32 gap-4">
      <div className="relative w-20 h-20">
        <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin" />
        <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }} />
        <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin" />
        <div className="absolute inset-6 rounded-full border-l-2 border-neon-green animate-spin" style={{ animationDuration: '3s' }} />
      </div>
      <p className="text-sm text-slate-500 font-orbitron tracking-widest animate-pulse">INITIALIZING MISSION CONTROL...</p>
    </div>
  );

  if (!stats) return null;

  const cards = [
    { label: 'Total Postings', value: stats.totalJobs, icon: HiBriefcase, color: 'cyan', glow: 'rgba(0,240,255,0.3)' },
    { label: 'Active Missions', value: stats.activeJobs, icon: HiCheckCircle, color: 'green', glow: 'rgba(0,255,102,0.3)' },
    { label: 'Encrypted (Drafts)', value: stats.draftJobs, icon: HiPencil, color: 'yellow', glow: 'rgba(234,179,8,0.3)' },
    { label: 'Archived', value: stats.closedJobs, icon: HiClock, color: 'magenta', glow: 'rgba(255,0,60,0.3)' },
    { label: 'Incoming Transmissions', value: stats.totalApplicationsReceived, icon: HiDocumentText, color: 'purple', glow: 'rgba(176,38,255,0.3)' },
  ];

  const colorMap = {
    cyan: { icon: 'bg-neon-cyan/10 text-neon-cyan', border: 'border-neon-cyan/30', shadow: 'shadow-[0_0_15px_rgba(0,240,255,0.3)]', text: 'text-neon-cyan' },
    green: { icon: 'bg-neon-green/10 text-neon-green', border: 'border-neon-green/30', shadow: 'shadow-[0_0_15px_rgba(0,255,102,0.3)]', text: 'text-neon-green' },
    yellow: { icon: 'bg-yellow-500/10 text-yellow-400', border: 'border-yellow-500/30', shadow: 'shadow-[0_0_15px_rgba(234,179,8,0.3)]', text: 'text-yellow-400' },
    magenta: { icon: 'bg-neon-magenta/10 text-neon-magenta', border: 'border-neon-magenta/30', shadow: 'shadow-[0_0_15px_rgba(255,0,60,0.3)]', text: 'text-neon-magenta' },
    purple: { icon: 'bg-neon-purple/10 text-neon-purple', border: 'border-neon-purple/30', shadow: 'shadow-[0_0_15px_rgba(176,38,255,0.3)]', text: 'text-neon-purple' },
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
      {/* Radar sweep in background */}
      <div className="absolute top-20 right-10 w-96 h-96 bg-neon-cyan/10 rounded-full blur-[120px] pointer-events-none" />
      <div className="absolute inset-0 grid-dots pointer-events-none opacity-30" />

      {/* Header with radar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-6 border-b border-white/10 pb-6 relative z-10">
        <div className="flex items-center gap-6">
          {/* Radar indicator */}
          <div className="hidden md:block relative w-16 h-16">
            <div className="absolute inset-0 rounded-full border border-neon-cyan/20" />
            <div className="absolute inset-2 rounded-full border border-neon-cyan/10" />
            <div className="absolute inset-4 rounded-full border border-neon-cyan/5" />
            <div className="absolute inset-0 rounded-full overflow-hidden">
              <div className="absolute top-1/2 left-1/2 w-1/2 h-[1px] bg-gradient-to-r from-neon-cyan to-transparent origin-left animate-radar" />
            </div>
            <div className="absolute top-1/2 left-1/2 w-2 h-2 rounded-full bg-neon-cyan -translate-x-1/2 -translate-y-1/2 shadow-[0_0_8px_rgba(0,240,255,0.8)]" />
          </div>
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-white uppercase tracking-widest flex items-center font-orbitron">
              <span className="w-3 h-3 rounded-full bg-neon-cyan block mr-3 shadow-[0_0_10px_rgba(0,240,255,0.8)] animate-pulse md:hidden" />
              Mission Control
            </h1>
            <p className="text-slate-400 mt-2 font-mono text-sm">EMPLOYER_ACCESS_LEVEL // <span className="text-neon-green">GRANTED</span></p>
          </div>
        </div>
        <Link to="/employer/post-job"
          className="px-6 py-3 bg-neon-cyan/10 border border-neon-cyan text-neon-cyan font-bold rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all duration-300 uppercase tracking-wider text-sm shadow-[0_0_15px_rgba(0,240,255,0.2)] font-orbitron text-xs warp-btn group relative overflow-hidden">
          <span className="relative z-10">+ Initialize New Mission</span>
          <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6 relative z-10">
        {cards.map((card) => {
          const cm = colorMap[card.color];
          return (
            <HolographicCard key={card.label}
              className={`glass-card rounded-2xl p-6 flex flex-col justify-between border ${cm.border}`}
              glowColor={card.glow}>
              <div className="flex flex-col gap-4">
                <div className={`w-12 h-12 rounded-full flex items-center justify-center border border-white/10 ${cm.icon} ${cm.shadow} relative pulse-ring-anim`}>
                  <card.icon className="w-6 h-6" />
                </div>
                <div>
                  <p className={`text-3xl font-extrabold tracking-tighter font-orbitron ${cm.text} animate-count-glow`}>
                    <AnimatedCounter value={card.value} />
                  </p>
                  <p className="text-[9px] text-slate-400 uppercase tracking-widest font-bold mt-1 font-orbitron">{card.label}</p>
                </div>
              </div>
            </HolographicCard>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div className="mt-12 relative z-10">
        <h2 className="text-lg font-bold text-white uppercase tracking-widest mb-6 border-l-2 border-neon-cyan pl-3 font-orbitron text-sm">Sub-Routines</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <HolographicCard as={Link} to="/employer/jobs"
            className="glass-card rounded-xl border border-white/5 p-6 flex items-center gap-4" glowColor="rgba(0,240,255,0.3)">
            <div className="p-3 bg-neon-cyan/10 rounded-lg text-neon-cyan group-hover:scale-110 transition-transform">
              <span className="text-xl">📋</span>
            </div>
            <div>
              <p className="font-bold text-white uppercase tracking-wider text-sm font-orbitron text-xs">Manage Missions</p>
              <p className="text-xs text-slate-500 mt-1">Oversee active postings</p>
            </div>
          </HolographicCard>
          <HolographicCard as={Link} to="/employer/profile"
            className="glass-card rounded-xl border border-white/5 p-6 flex items-center gap-4" glowColor="rgba(176,38,255,0.3)">
            <div className="p-3 bg-neon-purple/10 rounded-lg text-neon-purple group-hover:scale-110 transition-transform">
              <span className="text-xl">🏢</span>
            </div>
            <div>
              <p className="font-bold text-white uppercase tracking-wider text-sm font-orbitron text-xs">Corp Profile</p>
              <p className="text-xs text-slate-500 mt-1">Update entity metrics</p>
            </div>
          </HolographicCard>
          <HolographicCard as={Link} to="/change-password"
            className="glass-card rounded-xl border border-white/5 p-6 flex items-center gap-4" glowColor="rgba(255,0,60,0.3)">
            <div className="p-3 bg-neon-magenta/10 rounded-lg text-neon-magenta group-hover:scale-110 transition-transform">
              <span className="text-xl">🔒</span>
            </div>
            <div>
              <p className="font-bold text-white uppercase tracking-wider text-sm font-orbitron text-xs">Security Key</p>
              <p className="text-xs text-slate-500 mt-1">Cycle access codes</p>
            </div>
          </HolographicCard>
        </div>
      </div>
    </div>
  );
}
