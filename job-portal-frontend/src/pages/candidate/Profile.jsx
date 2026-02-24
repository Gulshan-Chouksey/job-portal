import { useState, useEffect } from 'react';
import { candidateService } from '../../services/dataService';
import toast from 'react-hot-toast';

export default function CandidateProfile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isNew, setIsNew] = useState(false);
  const [resumeFile, setResumeFile] = useState(null);

  const [form, setForm] = useState({
    headline: '', summary: '', skills: '', experienceYears: 0,
    education: '', phone: '', location: '',
  });

  useEffect(() => {
    candidateService.getProfile()
      .then((res) => {
        const p = res.data.data;
        setProfile(p);
        setForm({
          headline: p.headline || '', summary: p.summary || '', skills: p.skills || '',
          experienceYears: p.experienceYears || 0, education: p.education || '',
          phone: p.phone || '', location: p.location || '',
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
        res = await candidateService.createProfile(form);
        setIsNew(false);
        toast.success('Matrix configured!');
      } else {
        res = await candidateService.updateProfile(form);
        toast.success('Matrix updated!');
      }
      setProfile(res.data.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to sync');
    } finally {
      setSaving(false);
    }
  };

  const handleResumeUpload = async () => {
    if (!resumeFile) return;
    try {
      const res = await candidateService.uploadResume(resumeFile);
      setProfile(res.data.data);
      setResumeFile(null);
      toast.success('Data core uploaded!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Upload failed');
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
        {isNew ? 'Initialize Profile Matrix' : 'Entity Configuration'}
      </h1>

      <form onSubmit={handleSubmit} className="glass-panel border-neon-purple/20 rounded-3xl p-8 shadow-[0_0_30px_rgba(0,0,0,0.5)] relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-neon-purple via-transparent to-neon-purple opacity-50"></div>

        <div className="space-y-6 relative z-10">
          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Primary Designation (Headline)</label>
            <input type="text" value={form.headline}
              onChange={(e) => setForm({ ...form, headline: e.target.value })}
              placeholder="e.g. Full Stack Developer"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">System Summary</label>
            <textarea rows={4} value={form.summary}
              onChange={(e) => setForm({ ...form, summary: e.target.value })}
              placeholder="Provide a brief readout..."
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all resize-none" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Capability Protocols (Skills)</label>
              <input type="text" value={form.skills}
                onChange={(e) => setForm({ ...form, skills: e.target.value })}
                placeholder="Java, React, Node..."
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Uptime (Experience Years)</label>
              <input type="number" min="0" value={form.experienceYears}
                onChange={(e) => setForm({ ...form, experienceYears: parseInt(e.target.value) || 0 })}
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Knowledge Bases (Education)</label>
            <input type="text" value={form.education}
              onChange={(e) => setForm({ ...form, education: e.target.value })}
              placeholder="B.Tech in Compute Sciences"
              className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Comm Channel (Phone)</label>
              <input type="text" value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                placeholder="+91 9876543210"
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-neon-purple mb-2 uppercase tracking-widest pl-1">Sector (Location)</label>
              <input type="text" value={form.location}
                onChange={(e) => setForm({ ...form, location: e.target.value })}
                placeholder="Sector 7G"
                className="w-full px-5 py-3.5 bg-space-900/60 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-neon-purple focus:ring-1 focus:ring-neon-purple transition-all" />
            </div>
          </div>

          <button type="submit" disabled={saving}
            className="w-full py-4 mt-4 bg-neon-purple/10 border border-neon-purple text-neon-purple rounded-xl hover:bg-neon-purple hover:text-white transition-all font-bold tracking-widest uppercase hover:shadow-[0_0_20px_rgba(176,38,255,0.4)] disabled:opacity-50">
            {saving ? 'Syncing...' : isNew ? 'Establish Matrix' : 'Sync Changes'}
          </button>
        </div>
      </form>

      {/* Resume Upload */}
      {!isNew && (
        <div className="glass-card mt-8 p-8 border-neon-cyan/20">
          <h3 className="font-bold text-white uppercase tracking-widest mb-4 flex items-center text-sm">
            <span className="text-neon-cyan mr-2">🔗</span> Neural Data Link (Resume Format)
          </h3>

          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 bg-space-900/50 p-6 rounded-xl border border-white/5">
            <div className="flex-1">
              {profile?.resumeUrl ? (
                <p className="text-sm text-slate-400 font-mono flex items-center">
                  <span className="text-neon-cyan mr-2 px-2 py-0.5 bg-neon-cyan/10 border border-neon-cyan/30 rounded text-xs">ACTIVE_LINK</span>
                  {profile.resumeUrl}
                </p>
              ) : (
                <p className="text-sm text-slate-500 font-mono italic">No data core attached.</p>
              )}
            </div>
            <div className="flex items-center gap-4">
              <label className="cursor-pointer px-4 py-2 border border-slate-600 text-slate-300 text-xs font-bold uppercase tracking-wider rounded-lg hover:border-white hover:text-white transition-all">
                <input type="file" accept=".pdf,.doc,.docx" className="hidden" onChange={(e) => setResumeFile(e.target.files[0])} />
                Select File
              </label>
              <button onClick={handleResumeUpload} disabled={!resumeFile}
                className="px-6 py-2 bg-neon-cyan/80 text-space-900 text-xs font-bold uppercase tracking-wider rounded-lg hover:bg-white transition-all disabled:opacity-30 disabled:cursor-not-allowed shadow-[0_0_10px_rgba(0,240,255,0.4)]">
                Execute Upload
              </button>
            </div>
          </div>
          {resumeFile && (
            <p className="text-xs text-neon-cyan mt-3 font-mono pl-2">Pending upload: {resumeFile.name}</p>
          )}
        </div>
      )}
    </div>
  );
}
