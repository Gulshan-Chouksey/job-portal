import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { jobService } from '../services/dataService';
import { HiSearch, HiLocationMarker, HiCurrencyRupee, HiOfficeBuilding } from 'react-icons/hi';
import toast from 'react-hot-toast';
import HolographicCard from '../components/HolographicCard';

export default function Jobs() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '');
  const [location, setLocation] = useState(searchParams.get('location') || '');
  const [minSalary, setMinSalary] = useState(searchParams.get('minSalary') || '');
  const [maxSalary, setMaxSalary] = useState(searchParams.get('maxSalary') || '');

  const fetchJobs = async (p = 0) => {
    setLoading(true);
    try {
      const hasFilters = keyword || location || minSalary || maxSalary;
      let res;
      if (hasFilters) {
        res = await jobService.search({
          keyword: keyword || undefined,
          location: location || undefined,
          minSalary: minSalary || undefined,
          maxSalary: maxSalary || undefined,
          status: 'ACTIVE',
          page: p,
          size: 9,
        });
      } else {
        res = await jobService.search({ status: 'ACTIVE', page: p, size: 9 });
      }
      setJobs(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setPage(p);
    } catch (err) {
      toast.error('Failed to load jobs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, [searchParams]);

  const handleSearch = (e) => {
    e.preventDefault();
    const params = {};
    if (keyword) params.keyword = keyword;
    if (location) params.location = location;
    if (minSalary) params.minSalary = minSalary;
    if (maxSalary) params.maxSalary = maxSalary;
    setSearchParams(params);
    fetchJobs(0);
  };

  const formatSalary = (min, max) => {
    const fmt = (n) => (n >= 100000 ? `${(n / 100000).toFixed(1)}L` : `${(n / 1000).toFixed(0)}K`);
    return `₹${fmt(min)} - ₹${fmt(max)}`;
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-4xl font-extrabold tracking-tight font-orbitron">
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-white to-slate-400">Active </span>
          <span className="text-neon-cyan drop-shadow-[0_0_10px_rgba(0,240,255,0.8)]">Assignments</span>
        </h1>
      </div>

      {/* Search & Filters */}
      <form onSubmit={handleSearch} className="glass-panel p-6 rounded-2xl mb-12 border border-neon-cyan/20 relative overflow-hidden group radar-bg">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-neon-cyan to-transparent opacity-50 group-hover:opacity-100 transition-opacity" />
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 relative z-10">
          <div className="relative md:col-span-2">
            <HiSearch className="absolute left-4 top-3.5 text-neon-cyan" />
            <input type="text" placeholder="Job title or keyword..." value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="w-full pl-12 pr-4 py-3 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all focus:shadow-[0_0_15px_rgba(0,240,255,0.2)]" />
          </div>
          <div className="relative">
            <HiLocationMarker className="absolute left-4 top-3.5 text-neon-purple" />
            <input type="text" placeholder="Sector / Location..." value={location}
              onChange={(e) => setLocation(e.target.value)}
              className="w-full pl-12 pr-4 py-3 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all focus:shadow-[0_0_15px_rgba(176,38,255,0.2)]" />
          </div>
          <div className="flex gap-3">
            <input type="number" placeholder="Min ₹" value={minSalary}
              onChange={(e) => setMinSalary(e.target.value)}
              className="w-full px-4 py-3 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-neon-magenta focus:ring-1 focus:ring-neon-magenta transition-all" />
            <input type="number" placeholder="Max ₹" value={maxSalary}
              onChange={(e) => setMaxSalary(e.target.value)}
              className="w-full px-4 py-3 bg-space-900/50 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-neon-magenta focus:ring-1 focus:ring-neon-magenta transition-all" />
          </div>
          <button type="submit"
            className="bg-neon-cyan/10 border border-neon-cyan text-neon-cyan py-3 rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all font-bold tracking-wider hover:shadow-[0_0_20px_rgba(0,240,255,0.4)] font-orbitron text-sm warp-btn">
            INITIALIZE SEARCH
          </button>
        </div>
      </form>

      {/* Job List */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-32 gap-4">
          <div className="relative w-20 h-20">
            <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin" />
            <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }} />
            <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin" />
            <div className="absolute inset-6 rounded-full border-l-2 border-neon-green animate-spin" style={{ animationDuration: '3s' }} />
          </div>
          <p className="text-sm text-slate-500 font-orbitron tracking-widest animate-pulse">SCANNING SECTORS...</p>
        </div>
      ) : jobs.length === 0 ? (
        <div className="glass-panel text-center py-24 rounded-3xl border border-white/5 scan-line-overlay">
          <div className="w-20 h-20 bg-space-800 rounded-full flex items-center justify-center mx-auto mb-6 border border-white/10">
            <HiSearch className="w-8 h-8 text-slate-500" />
          </div>
          <p className="text-xl text-slate-400 font-light tracking-wide font-orbitron">No assignments found in current sector.</p>
          <p className="text-sm text-slate-500 mt-2">Adjust parameters and re-initialize search.</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {jobs.map((job, index) => (
              <HolographicCard key={job.id} as={Link} to={`/jobs/${job.id}`}
                className="glass-card rounded-2xl p-6 flex flex-col h-full"
                glowColor={['rgba(0,240,255,0.3)', 'rgba(176,38,255,0.3)', 'rgba(255,0,60,0.3)'][index % 3]}>
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h3 className="font-bold text-xl text-white group-hover:text-neon-cyan transition-colors duration-300 font-orbitron text-sm">
                      {job.title}
                    </h3>
                    <div className="flex items-center text-sm text-slate-400 mt-2">
                      <HiOfficeBuilding className="mr-2 text-neon-purple" />
                      {job.companyName || 'Unknown Entity'}
                    </div>
                  </div>
                  <span className={`px-3 py-1 text-xs rounded-full font-bold tracking-wider border font-orbitron ${job.status === 'ACTIVE' ? 'bg-neon-green/10 text-neon-green border-neon-green/30 shadow-[0_0_10px_rgba(0,255,102,0.2)]' :
                    job.status === 'CLOSED' ? 'bg-neon-magenta/10 text-neon-magenta border-neon-magenta/30 shadow-[0_0_10px_rgba(255,0,60,0.2)]' :
                      'bg-yellow-500/10 text-yellow-400 border-yellow-500/30'
                    }`}>
                    {job.status}
                  </span>
                </div>

                <div className="space-y-3 mb-6 flex-1">
                  <div className="flex items-center text-sm text-slate-400">
                    <HiLocationMarker className="mr-2 text-neon-purple w-4 h-4" />
                    {job.location}
                  </div>
                  <div className="flex items-center text-sm text-slate-400">
                    <HiCurrencyRupee className="mr-2 text-neon-green w-4 h-4" />
                    <span className="text-slate-300">{formatSalary(job.salaryMin, job.salaryMax)}</span>
                  </div>
                </div>

                {job.categories && job.categories.length > 0 && (
                  <div className="flex flex-wrap gap-2 mb-4">
                    {job.categories.map((cat) => (
                      <span key={cat.id} className="px-3 py-1 bg-space-900 border border-slate-700 text-slate-300 text-xs rounded-full font-orbitron">
                        {cat.name}
                      </span>
                    ))}
                  </div>
                )}

                <div className="pt-4 border-t border-white/10 mt-auto flex justify-between items-center">
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-mono">
                    Log: {new Date(job.createdAt).toLocaleDateString()}
                  </p>
                  <span className="text-neon-cyan text-sm opacity-0 group-hover:opacity-100 transition-opacity transform translate-x-2 group-hover:translate-x-0 font-orbitron text-xs">
                    Access &rarr;
                  </span>
                </div>
              </HolographicCard>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center mt-12 gap-4">
              <button disabled={page === 0} onClick={() => fetchJobs(page - 1)}
                className="px-6 py-3 border border-white/20 glass-panel text-white rounded-xl disabled:opacity-30 disabled:cursor-not-allowed hover:border-neon-cyan hover:text-neon-cyan transition-all font-orbitron text-xs warp-btn">
                &larr; Prev Sector
              </button>
              <span className="px-6 py-3 glass-panel rounded-xl text-sm text-neon-cyan font-orbitron border border-neon-cyan/20 flex items-center shadow-[0_0_10px_rgba(0,240,255,0.1)]">
                SEQ {page + 1}/{totalPages}
              </span>
              <button disabled={page >= totalPages - 1} onClick={() => fetchJobs(page + 1)}
                className="px-6 py-3 border border-white/20 glass-panel text-white rounded-xl disabled:opacity-30 disabled:cursor-not-allowed hover:border-neon-cyan hover:text-neon-cyan transition-all font-orbitron text-xs warp-btn">
                Next Sector &rarr;
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
