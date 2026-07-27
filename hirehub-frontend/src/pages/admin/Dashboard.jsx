import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminService } from '../../services/dataService';
import { HiUsers, HiBriefcase, HiDocumentText, HiUserGroup, HiShieldCheck, HiOfficeBuilding } from 'react-icons/hi';
import toast from 'react-hot-toast';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getDashboard()
      .then((res) => setStats(res.data.data))
      .catch(() => toast.error('Failed to sync master metrics'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="flex justify-center py-32">
      <div className="relative w-16 h-16">
        <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin"></div>
        <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }}></div>
        <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin"></div>
      </div>
    </div>
  );

  if (!stats) return null;

  const cards = [
    { label: 'Total Entities', value: stats.totalUsers, icon: HiUsers, color: 'cyan' },
    { label: 'Operatives', value: stats.totalCandidates, icon: HiUserGroup, color: 'green' },
    { label: 'Corporations', value: stats.totalEmployers, icon: HiOfficeBuilding, color: 'purple' },
    { label: 'Overlords', value: stats.totalAdmins, icon: HiShieldCheck, color: 'red' },
    { label: 'Active Missions', value: stats.totalJobs, icon: HiBriefcase, color: 'yellow' },
    { label: 'Total Transmissions', value: stats.totalApplications, icon: HiDocumentText, color: 'magenta' },
  ];

  const colorMap = {
    cyan: 'bg-neon-cyan/10 text-neon-cyan shadow-[0_0_15px_rgba(0,240,255,0.3)]',
    green: 'bg-neon-green/10 text-neon-green shadow-[0_0_15px_rgba(0,255,102,0.3)]',
    yellow: 'bg-yellow-500/10 text-yellow-400 shadow-[0_0_15px_rgba(234,179,8,0.3)]',
    red: 'bg-red-500/10 text-red-500 shadow-[0_0_15px_rgba(239,68,68,0.3)]',
    purple: 'bg-neon-purple/10 text-neon-purple shadow-[0_0_15px_rgba(176,38,255,0.3)]',
    magenta: 'bg-neon-magenta/10 text-neon-magenta shadow-[0_0_15px_rgba(255,0,60,0.3)]',
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
      <div className="absolute top-20 left-10 w-[500px] h-[500px] bg-red-500/5 rounded-full blur-[150px] pointer-events-none"></div>

      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-6 border-b border-white/10 pb-6">
        <div>
          <h1 className="text-3xl font-bold text-white uppercase tracking-widest flex items-center">
            <span className="w-3 h-3 rounded-full bg-red-500 block mr-3 shadow-[0_0_10px_rgba(239,68,68,0.8)] animate-pulse"></span>
            Nexus Command Core
          </h1>
          <p className="text-slate-400 mt-2 font-mono text-sm pl-6 border-l-2 border-red-500/50">ADMIN_ACCESS_LEVEL // OMNISCIENT</p>
        </div>
        <Link to="/admin/users"
          className="px-6 py-3 bg-red-500/10 border border-red-500 text-red-500 font-bold rounded-xl hover:bg-red-500 hover:text-white transition-all duration-300 uppercase tracking-wider text-sm shadow-[0_0_15px_rgba(239,68,68,0.2)]">
          Override User Access
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cards.map((card) => (
          <div key={card.label} className="glass-card rounded-2xl p-6 relative overflow-hidden flex flex-col justify-between border border-white/5 hover:border-white/20 transition-all duration-300 hover:shadow-[0_0_30px_rgba(255,255,255,0.05)] group">
            <div className={`absolute top-0 right-0 w-24 h-24 rounded-full -mr-12 -mt-12 blur-2xl transition-transform duration-500 group-hover:scale-150 opacity-20 ${colorMap[card.color].split(' ')[0]}`}></div>

            <div className="flex flex-col gap-4 relative z-10">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-[10px] text-slate-400 uppercase tracking-widest font-bold">{card.label}</p>
                  <p className="text-4xl font-extrabold text-white tracking-tighter mt-1">{card.value}</p>
                </div>
                <div className={`w-14 h-14 rounded-full flex items-center justify-center border border-white/10 ${colorMap[card.color]}`}>
                  <card.icon className="w-7 h-7" />
                </div>
              </div>
            </div>

            <div className="w-full h-1 bg-white/5 absolute bottom-0 left-0">
              <div className={`h-full ${colorMap[card.color].split(' ')[0]} opacity-50 w-2/3`}></div>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-12">
        <h2 className="text-lg font-bold text-white uppercase tracking-widest mb-6 border-l-2 border-red-500 pl-3">Global Protocols</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <Link to="/admin/users" className="glass-card rounded-xl border border-white/5 p-6 hover:border-red-500/50 hover:shadow-[0_0_20px_rgba(239,68,68,0.15)] transition-all group flex items-center gap-4">
            <div className="p-4 bg-red-500/10 rounded-lg text-red-500 group-hover:scale-110 transition-transform"><span className="text-2xl">👥</span></div>
            <div>
              <p className="font-bold text-white uppercase tracking-wider text-sm">Entity Management</p>
              <p className="text-xs text-slate-500 mt-1">Modify access levels and purge records</p>
            </div>
          </Link>
          <Link to="/change-password" className="glass-card rounded-xl border border-white/5 p-6 hover:border-neon-cyan/50 hover:shadow-[0_0_20px_rgba(0,240,255,0.15)] transition-all group flex items-center gap-4">
            <div className="p-4 bg-neon-cyan/10 rounded-lg text-neon-cyan group-hover:scale-110 transition-transform"><span className="text-2xl">🔒</span></div>
            <div>
              <p className="font-bold text-white uppercase tracking-wider text-sm">Security Override</p>
              <p className="text-xs text-slate-500 mt-1">Regenerate command access keys</p>
            </div>
          </Link>
        </div>
      </div>
    </div>
  );
}
