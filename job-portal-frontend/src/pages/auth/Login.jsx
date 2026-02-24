import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await login(form.email, form.password);
      toast.success('Welcome back!');
      if (data.role === 'ADMIN') navigate('/admin/dashboard');
      else if (data.role === 'EMPLOYER') navigate('/employer/dashboard');
      else navigate('/candidate/dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 relative z-10">
      {/* Neural grid background */}
      <div className="absolute inset-0 neural-grid-bg pointer-events-none" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-neon-cyan/5 rounded-full blur-[120px] pointer-events-none" />

      <div className="w-full max-w-md relative z-10">
        <div className="glass-panel rounded-3xl p-8 md:p-10 relative overflow-hidden border border-neon-cyan/20 shadow-[0_0_30px_rgba(0,0,0,0.5)] scan-line-overlay">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-neon-cyan to-transparent opacity-80" />

          {/* Biometric scan visual */}
          <div className="flex justify-center mb-6">
            <div className="relative w-16 h-16">
              <div className="absolute inset-0 rounded-full border-2 border-neon-cyan/30 animate-pulse-glow" />
              <div className="absolute inset-2 rounded-full border border-dashed border-neon-cyan/20 animate-spin-slow" />
              <div className="absolute inset-0 flex items-center justify-center text-2xl">🔐</div>
            </div>
          </div>

          <h2 className="text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-white to-slate-400 text-center mb-2 tracking-tight font-orbitron">Access Port</h2>
          <p className="text-slate-500 text-center mb-10 font-orbitron tracking-widest uppercase text-[10px]">Authenticate Identity</p>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-[10px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1 font-orbitron">Neural ID (Email)</label>
              <input type="email" required value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all focus:shadow-[0_0_15px_rgba(0,240,255,0.2)]"
                placeholder="operative@nexus.com" />
            </div>
            <div>
              <label className="block text-[10px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1 font-orbitron">Passcode</label>
              <input type="password" required value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all focus:shadow-[0_0_15px_rgba(0,240,255,0.2)]"
                placeholder="••••••••" />
            </div>
            <button type="submit" disabled={loading}
              className="w-full py-4 mt-2 bg-neon-cyan/10 border border-neon-cyan text-neon-cyan rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(0,240,255,0.4)] disabled:opacity-50 font-orbitron text-sm group relative overflow-hidden warp-btn">
              <span className="relative z-10">{loading ? 'Authenticating...' : 'Initiate Login'}</span>
              <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
            </button>
          </form>

          <div className="mt-8 text-center text-sm text-slate-400">
            Unregistered entity?{' '}
            <Link to="/register" className="text-neon-cyan font-bold hover:drop-shadow-[0_0_8px_rgba(0,240,255,0.8)] transition-all font-orbitron text-xs">Establish Profile</Link>
          </div>

          {/* Quick Login Hint */}
          <div className="mt-10 p-5 glass-card rounded-xl border border-white/5 relative overflow-hidden">
            <div className="absolute inset-0 grid-dots opacity-50 pointer-events-none" />
            <p className="text-[10px] text-neon-magenta font-bold mb-3 uppercase tracking-widest flex items-center font-orbitron relative z-10">
              <span className="w-1.5 h-1.5 rounded-full bg-neon-magenta animate-pulse mr-2 shadow-[0_0_5px_rgba(255,0,60,0.8)]" />
              Demo Protocols (pass: password123)
            </p>
            <div className="space-y-2 text-[11px] tracking-wide text-slate-400 font-mono pl-3 border-l hover:border-neon-magenta/50 border-white/10 transition-colors relative z-10">
              <p><span className="text-slate-500 mr-2">ADMIN:</span> admin@jobportal.com</p>
              <p><span className="text-slate-500 mr-2">CORP: </span> john@techcorp.com</p>
              <p><span className="text-slate-500 mr-2">CAND: </span> alice@email.com</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
