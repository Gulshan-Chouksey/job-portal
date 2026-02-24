import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jobService, categoryService } from '../../services/dataService';
import toast from 'react-hot-toast';

export default function PostJob() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    title: '', description: '', location: '', salaryMin: '',
    salaryMax: '', status: 'ACTIVE', categoryIds: [],
  });

  useEffect(() => {
    categoryService.getAll()
      .then((res) => setCategories(res.data.data))
      .catch(() => { });
  }, []);

  const toggleCategory = (id) => {
    setForm((prev) => ({
      ...prev,
      categoryIds: prev.categoryIds.includes(id)
        ? prev.categoryIds.filter((c) => c !== id)
        : [...prev.categoryIds, id],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await jobService.create({
        ...form,
        salaryMin: parseInt(form.salaryMin),
        salaryMax: parseInt(form.salaryMax),
        categoryIds: form.categoryIds.length > 0 ? form.categoryIds : undefined,
      });
      toast.success('Mission configuration uploaded!');
      navigate('/employer/jobs');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to initialize mission');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-12 relative z-10">
      <div className="absolute top-0 right-0 w-96 h-96 bg-neon-cyan/10 rounded-full blur-[120px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-neon-cyan block mr-3 shadow-[0_0_10px_rgba(0,240,255,0.8)]"></span>
        Initialize New Mission
      </h1>

      <form onSubmit={handleSubmit} className="glass-panel border-neon-cyan/20 rounded-3xl p-8 shadow-[0_0_30px_rgba(0,0,0,0.5)] relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-neon-cyan via-transparent to-neon-cyan opacity-50"></div>

        <div className="space-y-6 relative z-10">
          <div>
            <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Title Designation *</label>
            <input type="text" required value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="e.g. Senior Cybernetics Engineer"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Mission Parameters (Description)</label>
            <textarea rows={6} value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Outline objectives, requirements, and hardware needs..."
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all resize-none" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Deployment Sector (Location) *</label>
            <input type="text" required value={form.location}
              onChange={(e) => setForm({ ...form, location: e.target.value })}
              placeholder="Sector 7G, Earth / Remote Hub"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Min Credit Allocation (₹) *</label>
              <input type="number" required value={form.salaryMin}
                onChange={(e) => setForm({ ...form, salaryMin: e.target.value })}
                placeholder="50000"
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all font-mono" />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Max Credit Allocation (₹) *</label>
              <input type="number" required value={form.salaryMax}
                onChange={(e) => setForm({ ...form, salaryMax: e.target.value })}
                placeholder="100000"
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all font-mono" />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-cyan mb-2 uppercase tracking-widest pl-1">Initial Status</label>
            <select value={form.status}
              onChange={(e) => setForm({ ...form, status: e.target.value })}
              className="w-full px-5 py-3.5 bg-space-900/80 border border-white/10 rounded-xl text-white focus:outline-none focus:border-neon-cyan focus:ring-1 focus:ring-neon-cyan transition-all">
              <option value="ACTIVE" className="bg-space-900 text-white">🟢 ACTIVE (Broadcast immediately)</option>
              <option value="DRAFT" className="bg-space-900 text-white">🟡 DRAFT (Keep encrypted)</option>
              <option value="CLOSED" className="bg-space-900 text-white">🔴 CLOSED</option>
            </select>
          </div>

          {categories.length > 0 && (
            <div>
              <label className="block text-[11px] font-bold text-neon-cyan mb-3 uppercase tracking-widest pl-1">Specialization Tags</label>
              <div className="flex flex-wrap gap-3">
                {categories.map((cat) => (
                  <button key={cat.id} type="button"
                    onClick={() => toggleCategory(cat.id)}
                    className={`px-4 py-2 text-xs rounded-full font-bold uppercase tracking-widest transition-all duration-300 border ${form.categoryIds.includes(cat.id)
                        ? 'bg-neon-cyan/20 border-neon-cyan text-neon-cyan shadow-[0_0_10px_rgba(0,240,255,0.3)]'
                        : 'bg-space-900/50 border-white/10 text-slate-400 hover:border-white/30 hover:text-white'
                      }`}>
                    {cat.name}
                  </button>
                ))}
              </div>
            </div>
          )}

          <button type="submit" disabled={loading}
            className="w-full py-4 mt-6 bg-neon-cyan/10 border border-neon-cyan text-neon-cyan rounded-xl hover:bg-neon-cyan hover:text-space-900 transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(0,240,255,0.4)] disabled:opacity-50">
            {loading ? 'Transmitting Data...' : 'Broadcast Mission'}
          </button>
        </div>
      </form>
    </div>
  );
}
