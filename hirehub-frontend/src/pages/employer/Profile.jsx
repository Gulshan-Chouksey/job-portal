import { useState, useEffect } from 'react';
import { employerService } from '../../services/dataService';
import toast from 'react-hot-toast';

export default function EmployerProfile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isNew, setIsNew] = useState(false);

  const [form, setForm] = useState({
    companyName: '', companyDescription: '', companyWebsite: '', industry: '', location: '',
  });

  useEffect(() => {
    employerService.getProfile()
      .then((res) => {
        const p = res.data.data;
        setProfile(p);
        setForm({
          companyName: p.companyName || '', companyDescription: p.companyDescription || '',
          companyWebsite: p.companyWebsite || '', industry: p.industry || '', location: p.location || '',
        });
      })
      .catch((err) => {
        if (err.response?.status === 404) setIsNew(true);
        else toast.error('Failed to load profile');
      })
      .finally(() => setLoading(false));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      let res;
      if (isNew) {
        res = await employerService.createProfile(form);
        setIsNew(false);
        toast.success('Corp Profile established!');
      } else {
        res = await employerService.updateProfile(form);
        toast.success('Corp Profile updated!');
      }
      setProfile(res.data.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to sync');
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
      <div className="absolute top-0 right-0 w-96 h-96 bg-neon-purple/10 rounded-full blur-[120px] pointer-events-none"></div>

      <h1 className="text-3xl font-bold mb-8 text-white uppercase tracking-widest flex items-center border-b border-white/10 pb-6">
        <span className="w-2 h-2 rounded-full bg-neon-purple block mr-3 shadow-[0_0_10px_rgba(176,38,255,0.8)]"></span>
        {isNew ? 'Initialize Corporate Profile' : 'Corporate Identity Settings'}
      </h1>

      <form onSubmit={handleSubmit} className="glass-panel border-neon-purple/20 rounded-3xl p-8 shadow-[0_0_30px_rgba(0,0,0,0.5)] relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-neon-purple via-transparent to-neon-purple opacity-50"></div>

        <div className="space-y-6 relative z-10">
          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Entity Name (Company) *</label>
            <input type="text" required value={form.companyName}
              onChange={(e) => setForm({ ...form, companyName: e.target.value })}
              placeholder="Acme Corporation"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Operational Description</label>
            <textarea rows={5} value={form.companyDescription}
              onChange={(e) => setForm({ ...form, companyDescription: e.target.value })}
              placeholder="Detail your corporate objectives..."
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all resize-none" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Network Hub (Website)</label>
              <input type="url" value={form.companyWebsite}
                onChange={(e) => setForm({ ...form, companyWebsite: e.target.value })}
                placeholder="https://nexus-corp.com"
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Class (Industry)</label>
              <input type="text" value={form.industry}
                onChange={(e) => setForm({ ...form, industry: e.target.value })}
                placeholder="Cybernetics, Terraforming..."
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Headquarters (Location)</label>
            <input type="text" value={form.location}
              onChange={(e) => setForm({ ...form, location: e.target.value })}
              placeholder="Sector 7G, Earth"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
          </div>

          <button type="submit" disabled={saving}
            className="w-full py-4 mt-6 bg-neon-purple/10 border border-neon-purple text-neon-purple rounded-xl hover:bg-neon-purple hover:text-white transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(176,38,255,0.4)] disabled:opacity-50">
            {saving ? 'Syncing Data...' : isNew ? 'Establish Identity' : 'Sync Configuration'}
          </button>
        </div>
      </form>
    </div>
  );
}
