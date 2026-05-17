import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5178,
    proxy: {
      '/api': { target: 'http://localhost:8082', changeOrigin: true },
      '/login': { target: 'http://localhost:8082', changeOrigin: true },
      '/logout': { target: 'http://localhost:8082', changeOrigin: true },
      '/uploads': { target: 'http://localhost:8082', changeOrigin: true },
      '/images': { target: 'http://localhost:8082', changeOrigin: true },
      '/css': { target: 'http://localhost:8082', changeOrigin: true },
    },
  },
})
