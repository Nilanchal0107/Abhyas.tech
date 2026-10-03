import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// One origin: the browser only talks to :5173, Vite forwards /api to Spring Boot (trd 3).
// 127.0.0.1, not "localhost": Spring binds 127.0.0.1 only and "localhost" may resolve to ::1.
export default defineConfig({
  plugins: [react()],
  server: { proxy: { '/api': 'http://127.0.0.1:8080' } },
})
