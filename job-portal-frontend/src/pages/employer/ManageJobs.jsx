import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { employerService, jobService } from '../../services/dataService';
import { HiPencil, HiTrash, HiEye, HiUsers } from 'react-icons/hi';
import toast from 'react-hot-toast';

export default function ManageJobs() {
  const { user } = useAuth();
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [employerId, setEmployerId] = useState(null);

  useEffect(() => {
    employerService.getProfile()
      .then((res) => {
        setEmployerId(res.data.data.id);
        return employerService.getJobs(res.data.data.id, 0, 20);
      })
      .then((res) => {
        setJobs(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      })
      .catch(() => toast.error('Failed to load jobs'))
      .finally(() => setLoading(false));
  }, []);

  const fetchJobs = async (p) => {
    if (!employerId) return;
    setLoading(true);
    try {
      const res = await employerService.getJobs(employerId, p, 20);
      setJobs(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setPage(p);
    } catch {
      toast.error('Failed to load jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Purge this mission directive?')) return;
    try {
      await jobService.delete(id);
      toast.success('Directive purged');
      fetchJobs(page);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Purge failed');
    }
  };

  const statusColors = {
    ACTIVE: 'bg-neon-green/10 text-neon-green border border-neon-green/30 shadow-[0_0_10px_rgba(0,255,102,0.2)]',
    DRAFT: 'bg-yellow-500/10 text-yellow-500 border border-yellow-500/30',
    CLOSED: 'bg-neon-magenta/10 text-neon-magenta border border-neon-magenta/30 shadow-[0_0_10px_rgba(255,0,60,0.2)]',
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
      <div className="absolute top-10 left-10 w-96 h-96 bg-neon-cyan/5 rounded-full blur-[150px] pointer-events-none"></div>

      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
        <h1 className="text-3xl font-bold text-white uppercase tracking-widest flex items-center">
          <span className="w-2 h-2 rounded-full bg-neon-cyan block mr-3 shadow-[0_0_10px_rgba(0,240,255,0.8)]"></span>
          Manage Missions
        </h1>
        <Link to="/employer/post-job"
          className="px-6 py-3 bg-neon-cyan/20 border border-neon-cyan text-neon-cyan font-bold rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all duration-300 uppercase tracking-wider text-sm shadow-[0_0_15px_rgba(0,240,255,0.3)]">
          + Initialize New Mission
        </Link>
      </div>

      {jobs.length === 0 ? (
        <div className="glass-panel text-center py-20 border border-white/5 rounded-3xl">
          <p className="text-lg text-slate-400 font-light tracking-wide uppercase">No active directives found.</p>
        </div>
      ) : (
        <div className="glass-panel rounded-2xl border border-white/10 overflow-hidden shadow-[0_0_30px_rgba(0,0,0,0.5)]">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-white/5 border-b border-white/10 text-neon-cyan/80 text-[10px] uppercase tracking-widest font-bold">
                  <th className="px-6 py-4">Title Designation</th>
                  <th className="px-6 py-4">Sector</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4">Initialization Date</th>
                  <th className="px-6 py-4 text-right">Overrides</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {jobs.map((job) => (
                  <tr key={job.id} className="hover:bg-white/5 transition-colors group">
                    <td className="px-6 py-4 font-bold text-white tracking-wide">{job.title}</td>
                    <td className="px-6 py-4 text-sm text-slate-400 font-mono tracking-wide">{job.location}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 text-[10px] rounded-full font-bold uppercase tracking-widest ${statusColors[job.status] || 'bg-white/10 text-slate-400'}`}>
                        {job.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-500 font-mono">{new Date(job.createdAt).toLocaleDateString()}</td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-3 opacity-80 group-hover:opacity-100 transition-opacity">
                        <Link to={`/jobs/${job.id}`} className="p-2 bg-space-900/50 rounded-lg border border-white/10 text-slate-400 hover:text-neon-cyan hover:border-neon-cyan transition-all" title="Inspect">
                          <HiEye className="w-4 h-4" />
                        </Link>
                        <Link to={`/employer/jobs/${job.id}/applications`}
                          className="p-2 bg-space-900/50 rounded-lg border border-white/10 text-slate-400 hover:text-neon-purple hover:border-neon-purple transition-all" title="Incoming Transmissions">
                          <HiUsers className="w-4 h-4" />
                        </Link>
                        <Link to={`/employer/jobs/${job.id}/edit`}
                          className="p-2 bg-space-900/50 rounded-lg border border-white/10 text-slate-400 hover:text-neon-green hover:border-neon-green transition-all" title="Modify Parameters">
                          <HiPencil className="w-4 h-4" />
                        </Link>
                        <button onClick={() => handleDelete(job.id)}
                          className="p-2 bg-space-900/50 rounded-lg border border-white/10 text-slate-400 hover:text-neon-magenta hover:border-neon-magenta transition-all" title="Purge Record">
                          <HiTrash className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center mt-12 gap-3 glass-panel p-4 rounded-full max-w-md mx-auto border border-white/10">
          <button disabled={page === 0} onClick={() => fetchJobs(page - 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Prev</button>
          <span className="px-4 py-2 text-sm text-neon-cyan font-bold font-mono">
            {page + 1} / {totalPages}
          </span>
          <button disabled={page >= totalPages - 1} onClick={() => fetchJobs(page + 1)}
            className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Next</button>
        </div>
      )}
    </div>
  );
}
