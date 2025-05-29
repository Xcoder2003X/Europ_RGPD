const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || '',
  AUTH: {
    SIGNUP: '/api/auth/signup',
    SIGNIN: '/api/auth/signin',
    LOGOUT: '/api/auth/signout'
  },
  FILE: {
    UPLOAD: '/api/files/upload',
    GENERATE_REPORT: '/api/files/generate-report',
    DOWNLOAD_REPORT: '/api/files/download'
  },
  REPORTS: {
    ANALYZE: '/api/reports/analyze',
    DASHBOARD: '/api/reports/dashboard'
  },
  RAG: {
    ANALYZE: '/rag/analyze',
    STATUS: '/rag/status',
    RESULTS: '/rag/results'
  }
};

export default API_CONFIG;
