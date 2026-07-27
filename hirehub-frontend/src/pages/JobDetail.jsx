import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { jobService, applicationService, savedJobService } from '../services/dataService';
import { useAuth } from '../context/AuthContext';
import { HiLocationMarker, HiCurrencyRupee, HiOfficeBuilding, HiBookmark, HiCalendar } from 'react-icons/hi';
import { BsStars } from 'react-icons/bs';
import toast from 'react-hot-toast';

export default function JobDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [showApplyForm, setShowApplyForm] = useState(false);
  const [isSaved, setIsSaved] = useState(false);

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const res = await jobService.getById(id);
        setJob(res.data.data);
        if (user && user.role === 'CANDIDATE') {
          try {
            const savedRes = await savedJobService.check(id);
            setIsSaved(savedRes.data.data);
          } catch { /* ignore */ }
        }
      } catch {
        toast.error('Job not found');
        navigate('/jobs');
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  const handleApply = async (e) => {
    e.preventDefault();
    if (!user) { navigate('/login'); return; }
    setApplying(true);
    try {
      await applicationService.apply({ jobId: Number(id), coverLetter });
      toast.success('Application submitted!');
      setShowApplyForm(false);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to apply');
    } finally {
      setApplying(false);
    }
  };

  const handleSave = async () => {
    if (!user) { navigate('/login'); return; }
    try {
      if (isSaved) {
        await savedJobService.unsave(id);
        setIsSaved(false);
        toast.success('Job removed from saved');
      } else {
        await savedJobService.save(id);
        setIsSaved(true);
        toast.success('Job saved!');
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const formatSalary = (min, max) => {
    const fmt = (n) => (n >= 100000 ? `${(n / 100000).toFixed(1)}L` : `${(n / 1000).toFixed(0)}K`);
    return `₹${fmt(min)} - ₹${fmt(max)}`;
  };

  if (loading) return (
    <div className="flex flex-col items-center justify-center py-32 gap-4">
      <div className="relative w-20 h-20">
        <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin" />
        <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }} />
        <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin" />
        <div className="absolute inset-6 rounded-full border-l-2 border-neon-green animate-spin" style={{ animationDuration: '3s' }} />
      </div>
      <p className="text-sm text-slate-500 font-orbitron tracking-widest animate-pulse">LOADING ASSIGNMENT DATA...</p>
    </div>
  );

  if (!job) return null;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
      <div className="absolute top-0 right-0 w-96 h-96 bg-neon-cyan/10 rounded-full blur-[100px] pointer-events-none" />

      <div className="glass-panel rounded-3xl border border-neon-cyan/20 p-8 md:p-12 relative overflow-hidden grid-dots">
        {/* Top gradient bar */}
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-neon-purple via-neon-cyan to-neon-magenta" />

        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-start justify-between mb-10 gap-6 relative z-10">
          <div>
            <div className="inline-flex items-center space-x-2 bg-neon-cyan/10 border border-neon-cyan/30 px-3 py-1 rounded-full mb-4">
              <BsStars className="text-neon-cyan w-4 h-4" />
              <span className="text-neon-cyan font-orbitron font-medium text-[10px] tracking-widest uppercase">Assignment Details</span>
            </div>
            <h1 className="text-3xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-white to-slate-300 tracking-tight leading-tight font-orbitron">{job.title}</h1>
            <div className="flex flex-wrap items-center gap-6 mt-4 text-slate-400">
              <span className="flex items-center"><HiOfficeBuilding className="mr-2 text-neon-purple w-5 h-5" />{job.companyName || 'Unknown Entity'}</span>
              <span className="flex items-center"><HiLocationMarker className="mr-2 text-neon-purple w-5 h-5" />{job.location}</span>
            </div>
          </div>
          <span className={`px-4 py-2 text-sm rounded-full font-bold tracking-widest uppercase border whitespace-nowrap font-orbitron text-xs ${job.status === 'ACTIVE' ? 'bg-neon-green/10 text-neon-green border-neon-green/30 shadow-[0_0_10px_rgba(0,255,102,0.2)]' :
            job.status === 'CLOSED' ? 'bg-neon-magenta/10 text-neon-magenta border-neon-magenta/30 shadow-[0_0_10px_rgba(255,0,60,0.2)]' :
              'bg-yellow-500/10 text-yellow-400 border-yellow-500/30'
            }`}>
            <span className="w-2 h-2 rounded-full inline-block mr-2 animate-pulse" style={{ background: job.status === 'ACTIVE' ? '#00FF66' : job.status === 'CLOSED' ? '#FF003C' : '#EAB308' }} />
            {job.status}
          </span>
        </div>

        {/* Info Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-12 relative z-10">
          <div className="glass-card rounded-2xl p-6 border border-white/5 hover:border-neon-green/30 transition-all group">
            <p className="text-sm text-slate-500 flex items-center mb-2 uppercase tracking-wide font-orbitron text-[10px]"><HiCurrencyRupee className="mr-2 text-neon-green w-4 h-4" />Compensation</p>
            <p className="font-bold text-xl text-white drop-shadow-[0_0_5px_rgba(0,255,102,0.3)] font-orbitron">{formatSalary(job.salaryMin, job.salaryMax)}</p>
          </div>
          <div className="glass-card rounded-2xl p-6 border border-white/5 hover:border-neon-cyan/30 transition-all group">
            <p className="text-sm text-slate-500 flex items-center mb-2 uppercase tracking-wide font-orbitron text-[10px]"><HiCalendar className="mr-2 text-neon-cyan w-4 h-4" />Initialized</p>
            <p className="font-bold text-lg text-white font-orbitron">{new Date(job.createdAt).toLocaleDateString()}</p>
          </div>
          <div className="glass-card rounded-2xl p-6 border border-white/5 hover:border-neon-purple/30 transition-all group">
            <p className="text-sm text-slate-500 mb-2 uppercase tracking-wide font-orbitron text-[10px]">Parameters</p>
            <div className="flex flex-wrap gap-2">
              {job.categories && job.categories.length > 0
                ? job.categories.map((c) => (
                  <span key={c.id} className="px-3 py-1 bg-space-900 border border-slate-700 text-slate-300 text-xs rounded-full shadow-[0_0_5px_rgba(0,0,0,0.5)] font-orbitron text-[10px]">
                    {c.name}
                  </span>
                ))
                : <span className="text-slate-500 text-sm italic">Unspecified</span>}
            </div>
          </div>
        </div>

        {/* Description */}
        <div className="mb-12 relative z-10">
          <div className="absolute -left-8 top-0 bottom-0 w-1 bg-gradient-to-b from-neon-cyan to-transparent opacity-50 hidden md:block" />
          <h2 className="text-xl font-bold mb-6 text-white tracking-widest uppercase flex items-center font-orbitron">
            <span className="bg-neon-cyan w-2 h-2 rounded-full mr-3 shadow-[0_0_5px_rgba(0,240,255,0.8)] animate-pulse" />
            Mission Directives
          </h2>
          <div className="prose prose-invert max-w-none text-slate-300 leading-relaxed whitespace-pre-wrap font-light">
            {job.description || 'No directives provided.'}
          </div>
        </div>

        {/* Actions */}
        <div className="flex flex-wrap gap-4 border-t border-white/10 pt-8 mt-auto relative z-10">
          {user?.role === 'CANDIDATE' && job.status === 'ACTIVE' && (
            <>
              <button onClick={() => setShowApplyForm(!showApplyForm)}
                className="px-8 py-3 bg-neon-cyan text-space-900 font-bold text-lg rounded-xl hover:bg-white hover:text-neon-cyan hover:shadow-[0_0_30px_rgba(0,240,255,0.6)] transition-all duration-300 uppercase tracking-wider font-orbitron text-sm warp-btn relative overflow-hidden group">
                <span className="relative z-10">{showApplyForm ? 'Abort Sequence' : 'Commence Upload'}</span>
                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
              </button>
              <button onClick={handleSave}
                className={`px-8 py-3 border rounded-xl transition-all duration-300 font-bold uppercase tracking-wider flex items-center gap-2 font-orbitron text-sm ${isSaved ? 'bg-yellow-500/20 border-yellow-500 text-yellow-400 shadow-[0_0_15px_rgba(234,179,8,0.3)]' : 'border-slate-600 text-slate-300 hover:border-white hover:text-white hover:bg-white/5'
                  }`}>
                <HiBookmark className="w-5 h-5" /> {isSaved ? 'Data Saved' : 'Save Data'}
              </button>
            </>
          )}
          {!user && job.status === 'ACTIVE' && (
            <button onClick={() => navigate('/login')}
              className="px-8 py-3 btn-outline text-lg rounded-xl uppercase tracking-wider hover:bg-neon-cyan hover:text-space-900 font-orbitron text-sm warp-btn">
              Authenticate to Apply
            </button>
          )}
        </div>

        {/* Apply Form */}
        {showApplyForm && (
          <form onSubmit={handleApply} className="mt-8 border-t border-white/10 pt-8 relative z-10">
            <div className="glass-card p-6 rounded-2xl border border-neon-cyan/30 scan-line-overlay">
              <h3 className="font-bold mb-4 text-white uppercase tracking-widest text-sm flex items-center font-orbitron text-xs">
                <span className="w-2 h-2 rounded-full bg-neon-magenta mr-2 animate-pulse" />
                Transmit Cover Data (Optional)
              </h3>
              <textarea rows={5} value={coverLetter} onChange={(e) => setCoverLetter(e.target.value)}
                placeholder="Declare your suitability parameters..."
                className="w-full bg-space-900/50 border border-white/10 rounded-xl p-4 text-white placeholder-slate-500 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all mb-4 focus:shadow-[0_0_15px_rgba(0,240,255,0.2)]" />
              <button type="submit" disabled={applying}
                className="w-full sm:w-auto px-8 py-3 bg-neon-magenta/20 border border-neon-magenta text-neon-magenta rounded-xl hover:bg-neon-magenta hover:text-white transition-all duration-300 font-bold uppercase tracking-wider disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-[0_0_20px_rgba(255,0,60,0.4)] font-orbitron text-sm warp-btn">
                {applying ? 'Transmitting...' : 'Confirm Transmission'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
