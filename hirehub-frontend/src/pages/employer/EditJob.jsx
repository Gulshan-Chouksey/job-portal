import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { jobService, categoryService } from '../../services/dataService';
import toast from 'react-hot-toast';

export default function EditJob() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    title: '', description: '', location: '', salaryMin: '',
    salaryMax: '', status: 'ACTIVE', categoryIds: [],
  });

  useEffect(() => {
    Promise.all([
      jobService.getById(id),
      categoryService.getAll(),
    ]).then(([jobRes, catRes]) => {
      const job = jobRes.data.data;
      setForm({
        title: job.title || '', description: job.description || '',
        location: job.location || '', salaryMin: job.salaryMin || '',
        salaryMax: job.salaryMax || '', status: job.status || 'ACTIVE',
        categoryIds: job.categories ? job.categories.map((c) => c.id) : [],
      });
      setCategories(catRes.data.data);
    }).catch(() => {
      toast.error('Data corrupted. Aborting.');
      navigate('/employer/jobs');
    }).finally(() => setLoading(false));
  }, [id]);

  const toggleCategory = (catId) => {
    setForm((prev) => ({
      ...prev,
      categoryIds: prev.categoryIds.includes(catId)
        ? prev.categoryIds.filter((c) => c !== catId)
        : [...prev.categoryIds, catId],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await jobService.update(id, {
        ...form,
        salaryMin: parseInt(form.salaryMin),
        salaryMax: parseInt(form.salaryMax),
        categoryIds: form.categoryIds.length > 0 ? form.categoryIds : undefined,
      });
      toast.success('Mission configuration updated.');
      navigate('/employer/jobs');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Sync failed');
    } finally {
      setSaving(false);
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
    <div className="max-w-4xl mx-auto px-4 py-12 relative z-10">
      <div className="absolute top-0 right-0 w-96 h-96 bg-neon-green/10 rounded-full blur-[120px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-neon-green block mr-3 shadow-[0_0_10px_rgba(0,255,102,0.8)]"></span>
        Modify Mission Parameters
      </h1>

      <form onSubmit={handleSubmit} className="glass-panel border-neon-green/20 rounded-3xl p-8 shadow-[0_0_30px_rgba(0,0,0,0.5)] relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-neon-green via-transparent to-neon-green opacity-50"></div>

        <div className="space-y-6 relative z-10">
          <div>
            <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Title Designation *</label>
            <input type="text" required value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Mission Parameters (Description)</label>
            <textarea rows={6} value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all resize-none" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Deployment Sector (Location) *</label>
            <input type="text" required value={form.location}
              onChange={(e) => setForm({ ...form, location: e.target.value })}
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Min Credit Allocation (₹) *</label>
              <input type="number" required value={form.salaryMin}
                onChange={(e) => setForm({ ...form, salaryMin: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all font-mono" />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Max Credit Allocation (₹) *</label>
              <input type="number" required value={form.salaryMax}
                onChange={(e) => setForm({ ...form, salaryMax: e.target.value })}
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all font-mono" />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-green mb-2 uppercase tracking-widest pl-1">Override Status</label>
            <select value={form.status}
              onChange={(e) => setForm({ ...form, status: e.target.value })}
              className="w-full px-5 py-3.5 bg-space-900/80 border border-white/10 rounded-xl text-white focus:outline-none focus:border-neon-green focus:ring-1 focus:ring-neon-green transition-all">
              <option value="ACTIVE" className="bg-space-900 text-white">🟢 ACTIVE</option>
              <option value="DRAFT" className="bg-space-900 text-white">🟡 DRAFT</option>
              <option value="CLOSED" className="bg-space-900 text-white">🔴 CLOSED</option>
            </select>
          </div>

          {categories.length > 0 && (
            <div>
              <label className="block text-[11px] font-bold text-neon-green mb-3 uppercase tracking-widest pl-1">Specialization Tags</label>
              <div className="flex flex-wrap gap-3">
                {categories.map((cat) => (
                  <button key={cat.id} type="button"
                    onClick={() => toggleCategory(cat.id)}
                    className={`px-4 py-2 text-xs rounded-full font-bold uppercase tracking-widest transition-all duration-300 border ${form.categoryIds.includes(cat.id)
                        ? 'bg-neon-green/20 border-neon-green text-neon-green shadow-[0_0_10px_rgba(0,255,102,0.3)]'
                        : 'bg-space-900/50 border-white/10 text-slate-400 hover:border-white/30 hover:text-white'
                      }`}>
                    {cat.name}
                  </button>
                ))}
              </div>
            </div>
          )}

          <div className="flex flex-col sm:flex-row gap-4 mt-8">
            <button type="submit" disabled={saving}
              className="flex-1 py-4 bg-neon-green/10 border border-neon-green text-neon-green rounded-xl hover:bg-neon-green hover:text-space-900 transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(0,255,102,0.4)] disabled:opacity-50">
              {saving ? 'Syncing...' : 'Confirm Modifications'}
            </button>
            <button type="button" onClick={() => navigate('/employer/jobs')}
              className="px-8 py-4 border border-white/20 bg-space-900/50 text-slate-300 rounded-xl hover:bg-white/10 hover:text-white transition-all font-bold tracking-widest uppercase">
              Abort
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
