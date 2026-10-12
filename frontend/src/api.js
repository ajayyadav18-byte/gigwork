const API_BASE = import.meta.env.VITE_API_URL || 'https://gigwork.onrender.com/api';

export function getAuthToken() {
  return localStorage.getItem('gw_token');
}

export function setAuthSession(authData) {
  if (authData?.token) {
    localStorage.setItem('gw_token', authData.token);
  }
  if (authData?.name) {
    localStorage.setItem('gw_user_name', authData.name);
  }
  if (authData?.email) {
    localStorage.setItem('gw_user_email', authData.email);
  }
  if (authData?.role) {
    localStorage.setItem('gw_user_role', authData.role);
  }
}

export function clearAuthSession() {
  localStorage.removeItem('gw_token');
  localStorage.removeItem('gw_user_name');
  localStorage.removeItem('gw_user_email');
  localStorage.removeItem('gw_user_role');
}

export function getStoredUser() {
  const token = localStorage.getItem('gw_token');
  if (!token) return null;
  return {
    name: localStorage.getItem('gw_user_name') || 'Member',
    email: localStorage.getItem('gw_user_email') || '',
    role: localStorage.getItem('gw_user_role') || 'USER',
  };
}

export async function apiRequest(endpoint, options = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // Support local dev proxy if path is relative
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;

  const response = await fetch(url, {
    ...options,
    headers,
  });

  const contentType = response.headers.get('content-type');
  let data = null;
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const errorMsg = (data && data.message) || response.statusText || 'Request failed';
    throw new Error(errorMsg);
  }

  return data;
}
