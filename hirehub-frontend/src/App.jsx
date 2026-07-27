import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Layout
import Layout from './components/Layout';

// Public Pages
import Home from './pages/Home';
import Jobs from './pages/Jobs';
import JobDetail from './pages/JobDetail';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';

// Candidate Pages
import CandidateDashboard from './pages/candidate/Dashboard';
import CandidateProfile from './pages/candidate/Profile';
import MyApplications from './pages/candidate/MyApplications';
import SavedJobs from './pages/candidate/SavedJobs';

// Employer Pages
import EmployerDashboard from './pages/employer/Dashboard';
import EmployerProfile from './pages/employer/Profile';
import PostJob from './pages/employer/PostJob';
import ManageJobs from './pages/employer/ManageJobs';
import EditJob from './pages/employer/EditJob';
import JobApplications from './pages/employer/JobApplications';

// Admin Pages
import AdminDashboard from './pages/admin/Dashboard';
import ManageUsers from './pages/admin/ManageUsers';

// Shared
import ChangePassword from './pages/auth/ChangePassword';
import NotFound from './pages/NotFound';

function ProtectedRoute({ children, roles }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4">
        <div className="relative w-16 h-16">
          <div className="absolute inset-0 rounded-full border-t-2 border-neon-cyan animate-spin" />
          <div className="absolute inset-2 rounded-full border-r-2 border-neon-purple animate-spin" style={{ animationDuration: '2s', animationDirection: 'reverse' }} />
          <div className="absolute inset-4 rounded-full border-b-2 border-neon-magenta animate-spin" />
        </div>
        <p className="text-xs text-slate-500 font-orbitron tracking-widest animate-pulse">AUTHENTICATING...</p>
      </div>
    );
  }

  if (!user) return <Navigate to="/login" />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/" />;

  return children;
}

function GuestRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (user) {
    if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" />;
    if (user.role === 'EMPLOYER') return <Navigate to="/employer/dashboard" />;
    return <Navigate to="/candidate/dashboard" />;
  }
  return children;
}

export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="jobs" element={<Jobs />} />
        <Route path="jobs/:id" element={<JobDetail />} />

        {/* Auth */}
        <Route path="login" element={<GuestRoute><Login /></GuestRoute>} />
        <Route path="register" element={<GuestRoute><Register /></GuestRoute>} />

        {/* Change Password (all authenticated) */}
        <Route path="change-password" element={
          <ProtectedRoute><ChangePassword /></ProtectedRoute>
        } />

        {/* Candidate Routes */}
        <Route path="candidate/dashboard" element={
          <ProtectedRoute roles={['CANDIDATE']}><CandidateDashboard /></ProtectedRoute>
        } />
        <Route path="candidate/profile" element={
          <ProtectedRoute roles={['CANDIDATE']}><CandidateProfile /></ProtectedRoute>
        } />
        <Route path="candidate/applications" element={
          <ProtectedRoute roles={['CANDIDATE']}><MyApplications /></ProtectedRoute>
        } />
        <Route path="candidate/saved-jobs" element={
          <ProtectedRoute roles={['CANDIDATE']}><SavedJobs /></ProtectedRoute>
        } />

        {/* Employer Routes */}
        <Route path="employer/dashboard" element={
          <ProtectedRoute roles={['EMPLOYER']}><EmployerDashboard /></ProtectedRoute>
        } />
        <Route path="employer/profile" element={
          <ProtectedRoute roles={['EMPLOYER']}><EmployerProfile /></ProtectedRoute>
        } />
        <Route path="employer/post-job" element={
          <ProtectedRoute roles={['EMPLOYER']}><PostJob /></ProtectedRoute>
        } />
        <Route path="employer/jobs" element={
          <ProtectedRoute roles={['EMPLOYER']}><ManageJobs /></ProtectedRoute>
        } />
        <Route path="employer/jobs/:id/edit" element={
          <ProtectedRoute roles={['EMPLOYER']}><EditJob /></ProtectedRoute>
        } />
        <Route path="employer/jobs/:jobId/applications" element={
          <ProtectedRoute roles={['EMPLOYER']}><JobApplications /></ProtectedRoute>
        } />

        {/* Admin Routes */}
        <Route path="admin/dashboard" element={
          <ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>
        } />
        <Route path="admin/users" element={
          <ProtectedRoute roles={['ADMIN']}><ManageUsers /></ProtectedRoute>
        } />

        {/* 404 */}
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}
