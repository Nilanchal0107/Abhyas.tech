import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath } from 'node:url'

// One origin: the browser only talks to :5173, Vite forwards /api to Spring Boot (trd 3).
// 127.0.0.1, not "localhost": Spring binds 127.0.0.1 only and "localhost" may resolve to ::1.
// The copied OpenResume parser imports "lib/..." (its own tsconfig paths); map those to the copy.
const openresume = fileURLToPath(new URL('./src/lib/openresume', import.meta.url))
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: [
      { find: 'lib/parse-resume-from-pdf', replacement: openresume },
      { find: 'lib/redux/types', replacement: `${openresume}/resume-types.ts` },
      { find: 'lib/deep-clone', replacement: `${openresume}/deep-clone.ts` },
    ],
  },
  server: { proxy: { '/api': 'http://127.0.0.1:8080' } },
})
