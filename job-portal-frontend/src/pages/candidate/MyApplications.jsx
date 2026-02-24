import { useState, useEffect } from 'react';
import { applicationService } from '../../services/dataService';
import toast from 'react-hot-toast';

const statusColors = {
  PENDING: 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/30',
  REVIEWED: 'bg-blue-500/10 text-blue-400 border border-blue-500/30',
  SHORTLISTED: 'bg-neon-green/10 text-neon-green border border-neon-green/30 shadow-[0_0_10px_rgba(0,255,102,0.2)]',
  INTERVIEW: 'bg-neon-purple/10 text-neon-purple border border-neon-purple/30 shadow-[0_0_10px_rgba(176,38,255,0.2)]',
  OFFERED: 'bg-neon-cyan/10 text-neon-cyan border border-neon-cyan/30 shadow-[0_0_10px_rgba(0,240,255,0.2)]',
  REJECTED: 'bg-neon-magenta/10 text-neon-magenta border border-neon-magenta/30 shadow-[0_0_10px_rgba(255,0,60,0.2)]',
  WITHDRAWN: 'bg-white/10 text-slate-400 border border-white/20',
};

export default function MyApplications() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchApps = async (p = 0) => {
    setLoading(true);
    try {
      const res = await applicationService.getMyApplications(p, 10);
      setApplications(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setPage(p);
    } catch {
      toast.error('Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchApps(); }, []);

  const handleWithdraw = async (id) => {
    if (!confirm('Abort this transmission?')) return;
    try {
      await applicationService.withdraw(id);
      toast.success('Transmission aborted');
      fetchApps(page);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to abort');
    }
  };

  if (loading) return (
    <div className="flex justify-center py-32">
      <div className="relative w-16 h-16">
        <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin"></div>
        <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }}></div>
        <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin"></div>
      </div>
    </div>
  );

  return (
    <div className="max-w-5xl mx-auto px-4 py-12 relative z-10">
      <div className="absolute top-10 left-10 w-96 h-96 bg-neon-cyan/5 rounded-full blur-[150px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-neon-cyan block mr-3 shadow-[0_0_10px_rgba(0,240,255,0.8)]"></span>
        Transmission Logs
      </h1>

      {applications.length === 0 ? (
        <div className="glass-panel text-center py-20 border border-white/5 rounded-3xl">
          <p className="text-lg text-slate-400 font-light tracking-wide uppercase">No active transmissions detected.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {applications.map((app) => (
            <div key={app.id} className="glass-card rounded-xl border border-white/5 p-6 hover:border-white/20 hover:shadow-[0_0_20px_rgba(255,255,255,0.05)] transition-all duration-300">
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div>
                  <h3 className="font-bold text-lg text-white tracking-wide uppercase">{app.jobTitle}</h3>
                  <p className="text-sm text-neon-cyan/80 mt-1 uppercase tracking-widest text-[11px] font-bold">{app.companyName}</p>
                  <p className="text-xs text-slate-500 mt-2 font-mono">Logged: {new Date(app.appliedAt).toLocaleString()}</p>
                </div>
                <div className="flex items-center gap-4">
                  <span className={`px-4 py-1 text-xs rounded-full font-bold uppercase tracking-widest ${statusColors[app.status] || 'bg-white/10 border-white/20 text-slate-300'}`}>
                    {app.status}
                  </span>
                  {app.status === 'PENDING' && (
                    <button onClick={() => handleWithdraw(app.id)}
                      className="px-4 py-1.5 text-xs font-bold uppercase tracking-wider border border-neon-magenta/50 text-neon-magenta rounded-lg hover:bg-neon-magenta hover:text-white hover:shadow-[0_0_15px_rgba(255,0,60,0.4)] transition-all">
                      Abort
                    </button>
                  )}
                </div>
              </div>
              {app.coverLetter && (
                <div className="mt-4 pt-4 border-t border-white/5">
                  <p className="text-xs text-slate-500 uppercase tracking-widest mb-2 font-bold">Attached Data:</p>
                  <p className="text-sm text-slate-400 leading-relaxed max-w-3xl line-clamp-3 italic">"{app.coverLetter}"</p>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center mt-12 gap-3 glass-panel p-4 rounded-full max-w-md mx-auto border border-white/10">
          <button disabled={page === 0} onClick={() => fetchApps(page - 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Prev</button>
          <span className="px-4 py-2 text-sm text-neon-cyan font-bold font-mono">
            {page + 1} / {totalPages}
          </span>
          <button disabled={page >= totalPages - 1} onClick={() => fetchApps(page + 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Next</button>
        </div>
      )}
    </div>
  );
}