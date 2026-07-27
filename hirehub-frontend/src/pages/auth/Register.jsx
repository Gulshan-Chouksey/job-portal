import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const defaultRole = searchParams.get('role') || 'CANDIDATE';

  const [form, setForm] = useState({
    name: '', email: '', password: '', role: defaultRole,
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password.length < 6) {
      toast.error('Password must be at least 6 characters');
      return;
    }
    setLoading(true);
    try {
      const data = await register(form.name, form.email, form.password, form.role);
      toast.success('Account created!');
      if (data.role === 'EMPLOYER') navigate('/employer/profile');
      else navigate('/candidate/profile');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 relative z-10 py-12">
      {/* Neural grid background */}
      <div className="absolute inset-0 neural-grid-bg pointer-events-none" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-neon-purple/5 rounded-full blur-[150px] pointer-events-none" />

      <div className="w-full max-w-[480px] relative z-10">
        <div className="glass-panel rounded-3xl p-8 md:p-10 relative overflow-hidden border border-neon-purple/30 shadow-[0_0_30px_rgba(0,0,0,0.5)] animate-border-pulse scan-line-overlay">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-neon-purple to-transparent opacity-80" />

          {/* Icon */}
          <div className="flex justify-center mb-6">
            <div className="relative w-16 h-16">
              <div className="absolute inset-0 rounded-full border-2 border-neon-purple/30 animate-pulse-glow" />
              <div className="absolute inset-2 rounded-full border border-dashed border-neon-purple/20 animate-spin-slow" style={{ animationDirection: 'reverse' }} />
              <div className="absolute inset-0 flex items-center justify-center text-2xl">🚀</div>
            </div>
          </div>

          <h2 className="text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-white to-slate-400 text-center mb-2 tracking-tight font-orbitron">Establish Profile</h2>
          <p className="text-slate-500 text-center mb-10 font-orbitron tracking-widest uppercase text-[10px]">Join The Nexus</p>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-[10px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1 font-orbitron">Designation (Full Name)</label>
              <input type="text" required value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all focus:shadow-[0_0_15px_rgba(176,38,255,0.2)]"
                placeholder="Operative Name" />
            </div>
            <div>
              <label className="block text-[10px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1 font-orbitron">Neural ID (Email)</label>
              <input type="email" required value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all focus:shadow-[0_0_15px_rgba(176,38,255,0.2)]"
                placeholder="you@nexus.com" />
            </div>
            <div>
              <label className="block text-[10px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1 font-orbitron">Passcode</label>
              <input type="password" required value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all focus:shadow-[0_0_15px_rgba(176,38,255,0.2)]"
                placeholder="Min 6 secure characters" />
            </div>

            {/* Role Selection with orbital animation */}
            <div className="pt-2">
              <label className="block text-[10px] font-bold text-slate-400 mb-3 uppercase tracking-widest text-center font-orbitron">Entity Class Configuration</label>
              <div className="grid grid-cols-2 gap-4">
                <button type="button"
                  onClick={() => setForm({ ...form, role: 'CANDIDATE' })}
                  className={`py-5 rounded-xl border flex flex-col items-center justify-center space-y-2 transition-all duration-500 relative overflow-hidden group ${form.role === 'CANDIDATE'
                    ? 'bg-neon-cyan/10 border-neon-cyan text-neon-cyan shadow-[0_0_20px_rgba(0,240,255,0.3)] animate-border-pulse'
                    : 'border-white/10 text-slate-500 hover:border-white/30 hover:text-slate-300 bg-space-900/50'
                    }`}>
                  {/* Orbit ring behind selected icon */}
                  <div className={`relative ${form.role === 'CANDIDATE' ? '' : 'opacity-50'}`}>
                    <span className="text-3xl relative z-10">👩‍🚀</span>
                    {form.role === 'CANDIDATE' && (
                      <div className="absolute -inset-3 border border-neon-cyan/30 rounded-full animate-spin-slow" style={{ borderStyle: 'dashed' }} />
                    )}
                  </div>
                  <span className="text-[10px] font-bold uppercase tracking-widest font-orbitron">Candidate</span>
                </button>
                <button type="button"
                  onClick={() => setForm({ ...form, role: 'EMPLOYER' })}
                  className={`py-5 rounded-xl border flex flex-col items-center justify-center space-y-2 transition-all duration-500 relative overflow-hidden group ${form.role === 'EMPLOYER'
                    ? 'bg-neon-magenta/10 border-neon-magenta text-neon-magenta shadow-[0_0_20px_rgba(255,0,60,0.3)] animate-border-pulse'
                    : 'border-white/10 text-slate-500 hover:border-white/30 hover:text-slate-300 bg-space-900/50'
                    }`} style={form.role === 'EMPLOYER' ? { '--charge-color': 'rgba(255,0,60,0.3)' } : {}}>
                  <div className={`relative ${form.role === 'EMPLOYER' ? '' : 'opacity-50'}`}>
                    <span className="text-3xl relative z-10">🏢</span>
                    {form.role === 'EMPLOYER' && (
                      <div className="absolute -inset-3 border border-neon-magenta/30 rounded-full animate-spin-slow" style={{ borderStyle: 'dashed', animationDirection: 'reverse' }} />
                    )}
                  </div>
                  <span className="text-[10px] font-bold uppercase tracking-widest font-orbitron">Corporation</span>
                </button>
              </div>
            </div>

            <button type="submit" disabled={loading}
              className="w-full py-4 mt-6 bg-neon-purple/10 border border-neon-purple text-neon-purple rounded-xl hover:bg-neon-purple hover:text-white transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(176,38,255,0.4)] disabled:opacity-50 font-orbitron text-sm group relative overflow-hidden warp-btn">
              <span className="relative z-10">{loading ? 'Initializing...' : 'Confirm Registration'}</span>
              <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
            </button>
          </form>

          <div className="mt-8 text-center text-sm text-slate-400">
            Profile already exists?{' '}
            <Link to="/login" className="text-neon-cyan font-bold hover:drop-shadow-[0_0_8px_rgba(0,240,255,0.8)] transition-all font-orbitron text-xs">Authenticate Here</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
