import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_BACK_END_URL}/api`,
  withCredentials: true,
});

// Add a request interceptor to attach the auth token
api.interceptors.request.use(
  (config) => {
    const authData = JSON.parse(localStorage.getItem("auth"));
    let token = Array.isArray(authData) ? authData[0]?.jwtToken : authData?.jwtToken;

    if (token) {
      
      if (token.startsWith("springBootEcom=")) {
        token = token.replace("springBootEcom=", "");
      }
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;