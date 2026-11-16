import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Vite configuration for Foodify Frontend
// Port 5173 matches the CORS whitelist in Spring Boot SecurityConfig
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: false,
  },
});

