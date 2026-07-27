import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { applicationService } from '../../services/dataService';
import toast from 'react-hot-toast';

const statusColors = {
  PENDING: 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/30 shadow-[0_0_10px_rgba(234,179,8,0.2)]',
  REVIEWED: 'bg-neon-cyan/10 text-neon-cyan border border-neon-cyan/30 shadow-[0_0_10px_rgba(0,240,255,0.2)]',
  SHORTLISTED: 'bg-neon-purple/10 text-neon-purple border border-neon-purple/30 shadow-[0_0_10px_rgba(176,38,255,0.2)]',
  INTERVIEW: 'bg-neon-magenta/10 text-neon-magenta border border-neon-magenta/30 shadow-[0_0_10px_rgba(255,0,60,0.2)]',
  OFFERED: 'bg-neon-green/10 text-neon-green border border-neon-green/30 shadow-[0_0_10px_rgba(0,255,102,0.2)]',
  REJECTED: 'bg-red-500/10 text-red-500 border border-red-500/30',
  WITHDRAWN: 'bg-white/5 text-slate-400 border border-white/10',
};

const allStatuses = ['PENDING', 'REVIEWED', 'SHORTLISTED', 'INTERVIEW', 'OFFERED', 'REJECTED'];

export default function JobApplications() {
  const { jobId } = useParams();
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchApps = async (p = 0) => {
    setLoading(true);
    try {
      const res = await applicationService.getForJob(jobId, p, 10);
      setApplications(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setPage(p);
    } catch {
      toast.error('Failed to intercept transmissions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchApps(); }, [jobId]);

  const handleStatusChange = async (appId, status) => {
    try {
      await applicationService.updateStatus(appId, { status });
      toast.success(`Protocol updated: ${status}`);
      fetchApps(page);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Update failed');
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
    <div className="max-w-6xl mx-auto px-4 py-12 relative z-10">
      <div className="absolute top-10 right-20 w-96 h-96 bg-neon-purple/5 rounded-full blur-[150px] pointer-events-none"></div>

      <div className="flex items-center gap-4 mb-10 border-b border-white/10 pb-6">
        <Link to="/employer/jobs" className="text-neon-cyan hover:text-white transition-colors text-sm uppercase tracking-widest font-bold flex items-center">
          <span className="mr-2">←</span> Return
        </Link>
        <div className="h-6 w-px bg-white/20 mx-2"></div>
        <h1 className="text-2xl font-bold text-white uppercase tracking-widest">
          Transmissions for Mission #{jobId}
        </h1>
      </div>

      {applications.length === 0 ? (
        <div className="glass-panel text-center py-20 border border-white/5 rounded-3xl">
          <p className="text-lg text-slate-400 font-light tracking-wide uppercase">No incoming signals detected.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {applications.map((app) => (
            <div key={app.id} className="glass-card rounded-2xl border border-white/5 p-6 md:p-8 hover:border-neon-purple/30 transition-all duration-300 hover:shadow-[0_0_30px_rgba(176,38,255,0.05)] relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-white/5 rounded-full -mr-16 -mt-16 blur-2xl pointer-events-none"></div>

              <div className="flex flex-col md:flex-row md:items-start justify-between gap-6 relative z-10">
                <div>
                  <h3 className="font-bold text-xl text-white tracking-wide">{app.candidateName}</h3>
                  <p className="text-sm text-neon-cyan/80 font-mono mt-1">{app.candidateEmail}</p>
                  {app.candidateHeadline && (
                    <p className="text-sm text-slate-300 mt-2">{app.candidateHeadline}</p>
                  )}
                  <p className="text-[10px] text-slate-500 mt-3 font-mono uppercase tracking-widest">Signal received: {new Date(app.appliedAt).toLocaleDateString()}</p>
                </div>
                <div className="flex flex-col items-end gap-3">
                  <span className={`px-4 py-1.5 text-[10px] rounded-full font-bold uppercase tracking-widest ${statusColors[app.status]}`}>
                    {app.status}
                  </span>
                </div>
              </div>

              {app.coverLetter && (
                <div className="mt-6 p-5 bg-space-900/50 border border-white/5 rounded-xl relative z-10">
                  <p className="text-xs text-neon-purple font-bold uppercase tracking-widest mb-2">Attached Message Protocol</p>
                  <p className="text-sm text-slate-300 leading-relaxed font-light">{app.coverLetter}</p>
                </div>
              )}

              {/* Status Update Buttons */}
              {app.status !== 'WITHDRAWN' && (
                <div className="mt-8 pt-6 border-t border-white/5 flex flex-wrap items-center gap-3 relative z-10">
                  <span className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mr-2">Override Status Code:</span>
                  {allStatuses.filter((s) => s !== app.status).map((status) => (
                    <button key={status}
                      onClick={() => handleStatusChange(app.id, status)}
                      className="px-4 py-1.5 text-[10px] rounded-full font-bold uppercase tracking-widest bg-space-900 border border-white/10 text-slate-400 hover:text-white hover:border-white/50 hover:bg-white/5 transition-all">
                      {status}
                    </button>
                  ))}
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
          <span className="px-4 py-2 text-sm text-neon-purple font-bold font-mono">
            {page + 1} / {totalPages}
          </span>
          <button disabled={page >= totalPages - 1} onClick={() => fetchApps(page + 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Next</button>
        </div>
      )}
    </div>
  );
}
