import API from './api';

export const authService = {
  login: (data) => API.post('/auth/login', data),
  register: (data) => API.post('/auth/register', data),
  me: () => API.get('/auth/me'),
  changePassword: (data) => API.put('/auth/change-password', data),
  refreshToken: (data) => API.post('/auth/refresh-token', data),
  logout: (data) => API.post('/auth/logout', data),
};

export const jobService = {
  getAll: (page = 0, size = 10) => API.get(`/jobs?page=${page}&size=${size}&sort=id,desc`),
  getById: (id) => API.get(`/jobs/${id}`),
  search: (params) => {
    const query = new URLSearchParams();
    if (params.keyword) query.append('keyword', params.keyword);
    if (params.location) query.append('location', params.location);
    if (params.minSalary) query.append('minSalary', params.minSalary);
    if (params.maxSalary) query.append('maxSalary', params.maxSalary);
    if (params.status) query.append('status', params.status);
    query.append('page', params.page || 0);
    query.append('size', params.size || 10);
    return API.get(`/jobs/search?${query.toString()}`);
  },
  create: (data) => API.post('/jobs', data),
  update: (id, data) => API.put(`/jobs/${id}`, data),
  delete: (id) => API.delete(`/jobs/${id}`),
};

export const categoryService = {
  getAll: () => API.get('/categories'),
  getById: (id) => API.get(`/categories/${id}`),
  create: (data) => API.post('/categories', data),
  update: (id, data) => API.put(`/categories/${id}`, data),
  delete: (id) => API.delete(`/categories/${id}`),
};

export const candidateService = {
  getProfile: () => API.get('/candidates/profile'),
  createProfile: (data) => API.post('/candidates/profile', data),
  updateProfile: (data) => API.put('/candidates/profile', data),
  getById: (id) => API.get(`/candidates/${id}`),
  getDashboard: () => API.get('/candidates/dashboard'),
  uploadResume: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return API.post('/candidates/resume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  downloadResume: (id) => API.get(`/candidates/${id}/resume`, { responseType: 'blob' }),
};

export const employerService = {
  getProfile: () => API.get('/employers/profile'),
  createProfile: (data) => API.post('/employers/profile', data),
  updateProfile: (data) => API.put('/employers/profile', data),
  getById: (id) => API.get(`/employers/${id}`),
  getDashboard: () => API.get('/employers/dashboard'),
  getJobs: (id, page = 0, size = 10) => API.get(`/employers/${id}/jobs?page=${page}&size=${size}`),
};

export const applicationService = {
  apply: (data) => API.post('/applications', data),
  getMyApplications: (page = 0, size = 10) => API.get(`/applications/my?page=${page}&size=${size}`),
  getForJob: (jobId, page = 0, size = 10) => API.get(`/applications/job/${jobId}?page=${page}&size=${size}`),
  getForEmployer: (page = 0, size = 10) => API.get(`/applications/employer?page=${page}&size=${size}`),
  getById: (id) => API.get(`/applications/${id}`),
  updateStatus: (id, data) => API.patch(`/applications/${id}/status`, data),
  withdraw: (id) => API.patch(`/applications/${id}/withdraw`),
};

export const savedJobService = {
  save: (jobId) => API.post(`/saved-jobs/${jobId}`),
  unsave: (jobId) => API.delete(`/saved-jobs/${jobId}`),
  getMy: (page = 0, size = 10) => API.get(`/saved-jobs?page=${page}&size=${size}`),
  check: (jobId) => API.get(`/saved-jobs/${jobId}/check`),
};

export const adminService = {
  getUsers: (page = 0, size = 10, sortBy = 'createdAt', direction = 'desc') =>
    API.get(`/admin/users?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`),
  getUserById: (id) => API.get(`/admin/users/${id}`),
  updateRole: (id, data) => API.put(`/admin/users/${id}/role`, data),
  deleteUser: (id) => API.delete(`/admin/users/${id}`),
  getDashboard: () => API.get('/admin/dashboard'),
};
