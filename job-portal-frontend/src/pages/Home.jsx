import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { HiSearch, HiBriefcase, HiUserGroup, HiShieldCheck } from 'react-icons/hi';
import { BsStars } from 'react-icons/bs';
import HolographicCard from '../components/HolographicCard';

export default function Home() {
  const { user } = useAuth();

  return (
    <div className="relative">
      {/* Hero */}
      <section className="relative pt-32 pb-20 md:pt-48 md:pb-32 overflow-hidden">
        <div className="absolute top-20 left-10 w-72 h-72 bg-neon-magenta/20 rounded-full blur-[120px] mix-blend-screen animate-pulse-glow" />
        <div className="absolute bottom-10 right-10 w-96 h-96 bg-neon-cyan/20 rounded-full blur-[150px] mix-blend-screen animate-pulse-glow" style={{ animationDelay: '1s' }} />

        {/* Orbital ring behind hero */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 pointer-events-none">
          <div className="w-[500px] h-[500px] md:w-[700px] md:h-[700px] border border-neon-cyan/10 rounded-full animate-orbital" />
          <div className="absolute inset-10 border border-neon-purple/10 rounded-full animate-orbital-reverse" style={{ borderStyle: 'dashed' }} />
          <div className="absolute inset-20 border border-neon-magenta/5 rounded-full animate-spin-slower" />
          {/* Orbital dots */}
          <div className="absolute top-0 left-1/2 w-3 h-3 rounded-full bg-neon-cyan shadow-[0_0_15px_rgba(0,240,255,0.8)] -translate-x-1/2 -translate-y-1/2" />
          <div className="absolute bottom-0 left-1/2 w-2 h-2 rounded-full bg-neon-purple shadow-[0_0_15px_rgba(176,38,255,0.8)] -translate-x-1/2 translate-y-1/2" />
          <div className="absolute top-1/2 right-0 w-2 h-2 rounded-full bg-neon-magenta shadow-[0_0_15px_rgba(255,0,60,0.8)] translate-x-1/2 -translate-y-1/2" />
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center max-w-4xl mx-auto backdrop-blur-sm p-4 rounded-3xl">
            <div className="inline-flex items-center space-x-2 bg-space-800/80 border border-neon-cyan/30 px-4 py-2 rounded-full mb-8 shadow-[0_0_15px_rgba(0,240,255,0.2)] animate-float">
              <BsStars className="text-neon-cyan w-5 h-5" />
              <span className="text-neon-cyan font-orbitron font-medium text-xs tracking-widest uppercase">Next-Gen Job Matching</span>
            </div>
            <h1 className="text-5xl md:text-7xl font-extrabold mb-8 tracking-tight leading-tight">
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-white via-slate-200 to-slate-400">Launch Your Career Into</span>
              <br className="hidden md:block" />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-neon-cyan to-neon-purple drop-shadow-[0_0_20px_rgba(0,240,255,0.5)] glitch-text-subtle font-orbitron">The Future</span>
            </h1>
            <p className="text-xl md:text-2xl text-slate-400 mb-12 font-light max-w-2xl mx-auto leading-relaxed">
              Connect with top cutting-edge employers. Explore thousands of opportunities across the galaxy of tech.
            </p>
            <div className="flex flex-col sm:flex-row gap-6 justify-center items-center">
              <Link to="/jobs"
                className="group relative px-8 py-4 bg-neon-cyan/10 border border-neon-cyan text-neon-cyan font-bold text-lg rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all duration-500 shadow-[0_0_20px_rgba(0,240,255,0.3)] hover:shadow-[0_0_40px_rgba(0,240,255,0.6)] hover:-translate-y-1 overflow-hidden w-full sm:w-auto font-orbitron tracking-wider warp-btn">
                <div className="absolute inset-0 w-full h-full bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full group-hover:animate-[shimmer_1.5s_infinite]" />
                <span className="relative flex items-center justify-center">
                  <HiSearch className="w-6 h-6 mr-2" />
                  Explore Jobs
                </span>
              </Link>
              {!user && (
                <Link to="/register"
                  className="px-8 py-4 glass-panel text-white font-bold text-lg rounded-xl hover:border-neon-magenta/50 hover:shadow-[0_0_30px_rgba(255,0,60,0.3)] transition-all duration-500 hover:-translate-y-1 w-full sm:w-auto text-center font-orbitron tracking-wider warp-btn">
                  Initialize Account
                </Link>
              )}
            </div>
          </div>
        </div>

        {/* Floating space debris */}
        <div className="absolute top-20 right-[15%] w-1 h-1 rounded-full bg-white/60 animate-float" style={{ animationDelay: '0.5s', animationDuration: '7s' }} />
        <div className="absolute top-40 left-[20%] w-1.5 h-1.5 rounded-full bg-neon-cyan/40 animate-float" style={{ animationDelay: '1.5s', animationDuration: '9s' }} />
        <div className="absolute bottom-20 right-[30%] w-1 h-1 rounded-full bg-neon-purple/50 animate-float" style={{ animationDelay: '2s', animationDuration: '6s' }} />
        <div className="absolute top-60 left-[10%] w-0.5 h-0.5 rounded-full bg-white/40 animate-float" style={{ animationDelay: '3s', animationDuration: '11s' }} />
        <div className="absolute bottom-40 left-[60%] w-1 h-1 rounded-full bg-neon-magenta/30 animate-float" style={{ animationDelay: '4s', animationDuration: '8s' }} />
      </section>

      {/* Stats Bar */}
      <section className="relative border-t border-b border-white/5 py-8 bg-space-900/80 backdrop-blur-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center">
            {[
              { value: '10K+', label: 'Active Jobs', color: 'text-neon-cyan' },
              { value: '5K+', label: 'Companies', color: 'text-neon-purple' },
              { value: '50K+', label: 'Candidates', color: 'text-neon-green' },
              { value: '99.9%', label: 'Uptime', color: 'text-neon-magenta' },
            ].map((stat) => (
              <div key={stat.label} className="group">
                <p className={`text-3xl md:text-4xl font-extrabold font-orbitron ${stat.color} animate-count-glow`}>
                  {stat.value}
                </p>
                <p className="text-xs text-slate-500 uppercase tracking-widest mt-2 font-orbitron group-hover:text-slate-300 transition-colors">
                  {stat.label}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-24 relative border-t border-white/5 bg-space-900/50 backdrop-blur-lg">
        <div className="absolute inset-0 grid-dots pointer-events-none" />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-white mb-4 tracking-wider font-orbitron uppercase">System Capabilities</h2>
            <div className="h-1 w-24 bg-gradient-to-r from-neon-cyan to-neon-purple mx-auto rounded-full blur-[1px]" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <HolographicCard className="glass-card p-8 rounded-2xl text-center" glowColor="rgba(0,240,255,0.3)">
              <div className="w-20 h-20 bg-neon-cyan/10 rounded-2xl flex items-center justify-center mx-auto mb-6 border border-neon-cyan/30 group-hover:border-neon-cyan group-hover:shadow-[0_0_30px_rgba(0,240,255,0.4)] transition-all duration-500 rotate-3 relative pulse-ring-anim">
                <HiBriefcase className="w-10 h-10 text-neon-cyan" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-3 font-orbitron">Vast Database</h3>
              <p className="text-slate-400 font-light leading-relaxed">Access our quantum-linked database of opportunities across all sectors and star systems.</p>
            </HolographicCard>

            <HolographicCard className="glass-card p-8 rounded-2xl text-center" glowColor="rgba(176,38,255,0.3)">
              <div className="w-20 h-20 bg-neon-purple/10 rounded-2xl flex items-center justify-center mx-auto mb-6 border border-neon-purple/30 transition-all duration-500 -rotate-3 relative pulse-ring-anim" style={{ color: '#B026FF' }}>
                <HiUserGroup className="w-10 h-10 text-neon-purple" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-3 font-orbitron">Elite Network</h3>
              <p className="text-slate-400 font-light leading-relaxed">Connect synchronously with top-tier organizations seeking exceptional neural pathways.</p>
            </HolographicCard>

            <HolographicCard className="glass-card p-8 rounded-2xl text-center" glowColor="rgba(255,0,60,0.3)">
              <div className="w-20 h-20 bg-neon-magenta/10 rounded-2xl flex items-center justify-center mx-auto mb-6 border border-neon-magenta/30 transition-all duration-500 rotate-3 relative pulse-ring-anim" style={{ color: '#FF003C' }}>
                <HiShieldCheck className="w-10 h-10 text-neon-magenta" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-3 font-orbitron">Secure Protocol</h3>
              <p className="text-slate-400 font-light leading-relaxed">Zero-trust security protocol ensures your data remains encrypted and sovereign.</p>
            </HolographicCard>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-24 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-b from-space-900 via-space-800 to-space-900 opacity-80" />
        <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-[200px] bg-neon-cyan/5 blur-[100px] pointer-events-none" />
        <div className="absolute inset-0 neural-grid-bg opacity-50 pointer-events-none" />

        <div className="max-w-4xl mx-auto px-4 text-center relative z-10 glass-panel p-12 rounded-3xl border border-neon-cyan/20 scan-line-overlay">
          <h2 className="text-4xl font-bold mb-6 text-white tracking-widest uppercase font-orbitron"><span className="text-neon-cyan glitch-text-subtle">Initiate</span> Sequence</h2>
          <p className="text-slate-400 mb-10 text-xl font-light">
            Whether you are calculating your next career trajectory or acquiring new talent nodes.
          </p>
          <div className="flex flex-col sm:flex-row gap-6 justify-center">
            <Link to="/register?role=CANDIDATE"
              className="px-8 py-4 bg-neon-cyan text-space-900 font-bold text-lg rounded-xl hover:bg-white hover:text-neon-cyan hover:shadow-[0_0_30px_rgba(0,240,255,0.6)] transition-all duration-300 transform hover:-translate-y-1 uppercase tracking-wider font-orbitron warp-btn">
              Candidate Portal
            </Link>
            <Link to="/register?role=EMPLOYER"
              className="px-8 py-4 border border-slate-500 text-slate-300 font-bold text-lg rounded-xl hover:border-white hover:text-white hover:bg-white/5 transition-all duration-300 transform hover:-translate-y-1 uppercase tracking-wider font-orbitron warp-btn">
              Employer Nexus
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
