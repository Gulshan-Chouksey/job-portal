import { useState, useEffect } from 'react';
import { adminService } from '../../services/dataService';
import { HiTrash, HiChevronLeft, HiChevronRight } from 'react-icons/hi';
import toast from 'react-hot-toast';

const ROLES = ['CANDIDATE', 'EMPLOYER', 'ADMIN'];

export default function ManageUsers() {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchUsers = (p = 0) => {
    setLoading(true);
    adminService.getUsers(p, 10)
      .then((res) => {
        const data = res.data.data;
        setUsers(data.content);
        setTotalPages(data.totalPages);
        setPage(data.number);
      })
      .catch(() => toast.error('Failed to intercept entities'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await adminService.updateRole(userId, newRole);
      toast.success('Access level overwritten');
      fetchUsers(page);
    } catch {
      toast.error('Override failed');
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm('Purge this entity permanently from the Nexus?')) return;
    try {
      await adminService.deleteUser(userId);
      toast.success('Entity purged');
      fetchUsers(page);
    } catch {
      toast.error('Purge failed');
    }
  };

  const roleBadge = (role) => {
    const map = {
      ADMIN: 'bg-red-500/20 text-red-500 border border-red-500/50 shadow-[0_0_10px_rgba(239,68,68,0.3)]',
      EMPLOYER: 'bg-neon-purple/20 text-neon-purple border border-neon-purple/50 shadow-[0_0_10px_rgba(176,38,255,0.3)]',
      CANDIDATE: 'bg-neon-cyan/20 text-neon-cyan border border-neon-cyan/50 shadow-[0_0_10px_rgba(0,240,255,0.3)]',
    };
    return map[role] || 'bg-white/10 text-slate-400 border border-white/20';
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
      <div className="absolute top-10 right-10 w-96 h-96 bg-red-500/5 rounded-full blur-[150px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-red-500 block mr-3 shadow-[0_0_10px_rgba(239,68,68,0.8)] animate-pulse"></span>
        Global Entity Configuration
      </h1>

      {loading ? (
        <div className="flex justify-center py-32">
          <div className="relative w-16 h-16">
            <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin"></div>
            <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }}></div>
            <div className="absolute inset-4 rounded-full border-b-2 border-red-500 animate-spin"></div>
          </div>
        </div>
      ) : users.length === 0 ? (
        <div className="glass-panel text-center py-20 border border-white/5 rounded-3xl">
          <p className="text-lg text-slate-400 font-light tracking-wide uppercase">No registered entities found.</p>
        </div>
      ) : (
        <>
          <div className="glass-panel rounded-2xl border border-white/10 overflow-hidden shadow-[0_0_30px_rgba(0,0,0,0.5)] bg-space-900/40">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse min-w-[800px]">
                <thead>
                  <tr className="bg-white/5 border-b border-white/10 text-red-500/80 text-[10px] uppercase tracking-widest font-bold">
                    <th className="px-6 py-4">Node ID</th>
                    <th className="px-6 py-4">Entity Designation</th>
                    <th className="px-6 py-4">Net Address (Email)</th>
                    <th className="px-6 py-4">Auth Level</th>
                    <th className="px-6 py-4">Override Access</th>
                    <th className="px-6 py-4 text-center">Execute</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5">
                  {users.map((u) => (
                    <tr key={u.id} className="hover:bg-white/5 transition-colors group">
                      <td className="px-6 py-4 text-sm font-mono text-slate-500">#{u.id}</td>
                      <td className="px-6 py-4 font-bold text-white tracking-wide">{u.name}</td>
                      <td className="px-6 py-4 text-sm text-neon-cyan/70 font-mono tracking-wider">{u.email}</td>
                      <td className="px-6 py-4">
                        <span className={`px-3 py-1 text-[10px] uppercase tracking-widest font-bold rounded-full ${roleBadge(u.role)}`}>
                          {u.role}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <select
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.id, e.target.value)}
                          className="bg-space-900/80 border border-white/10 text-slate-300 rounded-lg px-3 py-1.5 text-xs font-bold uppercase tracking-widest focus:ring-1 focus:ring-red-500 focus:border-red-500 focus:outline-none transition-all cursor-pointer hover:border-white/30"
                        >
                          {ROLES.map((r) => <option key={r} value={r} className="bg-space-900 text-white">{r}</option>)}
                        </select>
                      </td>
                      <td className="px-6 py-4 text-center">
                        <button
                          onClick={() => handleDelete(u.id)}
                          className="p-2 bg-space-900/50 rounded-lg border border-white/10 text-slate-500 hover:text-red-500 hover:border-red-500 hover:bg-red-500/10 hover:shadow-[0_0_15px_rgba(239,68,68,0.3)] transition-all"
                          title="Purge Entity"
                        >
                          <HiTrash className="w-5 h-5 mx-auto" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center mt-12 gap-3 glass-panel p-4 rounded-full max-w-md mx-auto border border-white/10">
              <button disabled={page === 0} onClick={() => fetchUsers(page - 1)}
                className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Prev</button>
              <span className="px-4 py-2 text-sm text-red-500 font-bold font-mono">
                {page + 1} / {totalPages}
              </span>
              <button disabled={page >= totalPages - 1} onClick={() => fetchUsers(page + 1)}
                className="px-6 py-2 border border-white/10 rounded-full disabled:opacity-30 hover:bg-white/5 text-slate-300 text-sm font-bold uppercase tracking-widest transition-all">Next</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}