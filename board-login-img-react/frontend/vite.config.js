import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 개발 시 브라우저는 localhost:5173 → API/이미지는 백엔드 8080 으로 프록시 (세션 쿠키 동일 출처)
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
      '/uploads': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
