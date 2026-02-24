import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { savedJobService } from '../../services/dataService';
import { HiLocationMarker, HiCurrencyRupee, HiTrash } from 'react-icons/hi';
import toast from 'react-hot-toast';

export default function SavedJobs() {
  const [saved, setSaved] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchSaved = async (p = 0) => {
    setLoading(true);
    try {
      const res = await savedJobService.getMy(p, 10);
      setSaved(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setPage(p);
    } catch {
      toast.error('Failed to load saved data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchSaved(); }, []);

  const handleUnsave = async (jobId) => {
    try {
      await savedJobService.unsave(jobId);
      toast.success('Data purged from cache');
      fetchSaved(page);
    } catch (err) {
      toast.error('Purge failed');
    }
  };

  const formatSalary = (min, max) => {
    const fmt = (n) => (n >= 100000 ? `${(n / 100000).toFixed(1)}L` : `${(n / 1000).toFixed(0)}K`);
    return `₹${fmt(min)} - ₹${fmt(max)}`;
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
      <div className="absolute top-10 right-10 w-96 h-96 bg-neon-magenta/5 rounded-full blur-[150px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-neon-magenta block mr-3 shadow-[0_0_10px_rgba(255,0,60,0.8)]"></span>
        Cached Data (Saved Jobs)
      </h1>

      {saved.length === 0 ? (
        <div className="glass-panel text-center py-20 border border-white/5 rounded-3xl">
          <p className="text-lg text-slate-400 font-light tracking-wide uppercase">No cached data found in your active sector.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {saved.map((s) => (
            <div key={s.id} className="glass-card rounded-xl border border-white/5 p-6 hover:border-neon-magenta/30 hover:shadow-[0_0_20px_rgba(255,0,60,0.1)] transition-all flex items-center justify-between group">
              <Link to={`/jobs/${s.jobId}`} className="flex-1">
                <h3 className="font-bold text-lg text-white tracking-wide uppercase group-hover:text-neon-magenta transition-colors">{s.jobTitle}</h3>
                <p className="text-sm text-neon-cyan/80 mt-1 uppercase tracking-widest text-[11px] font-bold">{s.companyName}</p>
                <div className="flex items-center gap-6 mt-3 text-sm text-slate-400 font-mono">
                  <span className="flex items-center text-neon-purple"><HiLocationMarker className="mr-2 w-4 h-4" />{s.location}</span>
                  <span className="flex items-center text-neon-green"><HiCurrencyRupee className="mr-2 w-4 h-4" />{formatSalary(s.salaryMin, s.salaryMax)}</span>
                </div>
              </Link>
              <button onClick={() => handleUnsave(s.jobId)}
                className="ml-6 p-3 text-neon-magenta border border-neon-magenta/30 hover:bg-neon-magenta hover:text-white rounded-xl transition-all shadow-[0_0_10px_rgba(255,0,60,0.2)]">
                <HiTrash className="w-5 h-5" />
              </button>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center mt-12 gap-3 glass-panel p-4 rounded-full max-w-md mx-auto border border-white/10">
          <button disabled={page === 0} onClick={() => fetchSaved(page - 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Prev</button>
          <span className="px-4 py-2 text-sm text-neon-magenta font-bold font-mono">
            {page + 1} / {totalPages}
          </span>
          <button disabled={page >= totalPages - 1} onClick={() => fetchSaved(page + 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Next</button>
        </div>
      )}
    </div>
  );
}
